package cy.jdkdigital.productivefarming.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivefarming.common.block.ColorfulFlowerBlock;
import cy.jdkdigital.productivefarming.common.block.ColorfulTallFlowerBlock;
import cy.jdkdigital.productivefarming.registry.FarmingDataComponents;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import cy.jdkdigital.productivefarming.util.FarmUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public class FlowerDyeCraftingRecipe implements CraftingRecipe
{
    private Integer count;

    public FlowerDyeCraftingRecipe(Integer count) {
        this.count = count;
    }

    public static final MapCodec<FlowerDyeCraftingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                            Codec.INT.fieldOf("count").orElse(1).forGetter(recipe -> recipe.count)
                    )
                    .apply(builder, FlowerDyeCraftingRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, FlowerDyeCraftingRecipe> STREAM_CODEC = StreamCodec.of(
            FlowerDyeCraftingRecipe::toNetwork, FlowerDyeCraftingRecipe::fromNetwork
    );

    public static final RecipeSerializer<FlowerDyeCraftingRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return input.ingredientCount() == 1 && input.getItem(0).has(FarmingDataComponents.COLOR) && input.getItem(0).getItem() instanceof BlockItem blockItem && (blockItem.getBlock() instanceof ColorfulFlowerBlock || blockItem.getBlock() instanceof ColorfulTallFlowerBlock);
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        var stack = input.getItem(0);
        if (stack.has(FarmingDataComponents.COLOR)) {
            var output = FarmUtil.getDyeFromColor(stack.get(FarmingDataComponents.COLOR));
            if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ColorfulTallFlowerBlock) {
                output.setCount(2);
            }
            return output;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<FlowerDyeCraftingRecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public boolean showNotification() {
        return true;
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    public static FlowerDyeCraftingRecipe fromNetwork(@Nonnull RegistryFriendlyByteBuf buffer) {
        return new FlowerDyeCraftingRecipe(buffer.readInt());
    }

    public static void toNetwork(@Nonnull RegistryFriendlyByteBuf buffer, FlowerDyeCraftingRecipe recipe) {
        buffer.writeInt(recipe.count);
    }
}
