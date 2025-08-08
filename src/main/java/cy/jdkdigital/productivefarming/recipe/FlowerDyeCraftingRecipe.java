package cy.jdkdigital.productivefarming.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivefarming.common.block.ColorfulFlowerBlock;
import cy.jdkdigital.productivefarming.common.block.ColorfulTallFlowerBlock;
import cy.jdkdigital.productivefarming.registry.FarmingDataComponents;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import cy.jdkdigital.productivefarming.util.FarmUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public class FlowerDyeCraftingRecipe implements CraftingRecipe
{
    private Integer count;

    public FlowerDyeCraftingRecipe(Integer count) {
        this.count = count;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return input.ingredientCount() == 1 && input.getItem(0).has(FarmingDataComponents.COLOR) && input.getItem(0).getItem() instanceof BlockItem blockItem && (blockItem.getBlock() instanceof ColorfulFlowerBlock || blockItem.getBlock() instanceof ColorfulTallFlowerBlock);
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
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
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return Items.WHITE_DYE.getDefaultInstance();
    }

//    @Override
//    public NonNullList<Ingredient> getIngredients() {
//        NonNullList<Ingredient> nonnulllist = NonNullList.create();
//        nonnulllist.add(Ingredient.of(ItemTags.FLOWERS));
//        return nonnulllist;
//    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return FarmingRegistrator.FLOWER_DYE_CRAFTING.get();
    }

    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }

    public static class Serializer implements RecipeSerializer<FlowerDyeCraftingRecipe>
    {
        private static final MapCodec<FlowerDyeCraftingRecipe> CODEC = RecordCodecBuilder.mapCodec(
                builder -> builder.group(
                                Codec.INT.fieldOf("count").orElse(1).forGetter(recipe -> recipe.count)
                        )
                        .apply(builder, FlowerDyeCraftingRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, FlowerDyeCraftingRecipe> STREAM_CODEC = StreamCodec.of(
                FlowerDyeCraftingRecipe.Serializer::toNetwork, FlowerDyeCraftingRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<FlowerDyeCraftingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FlowerDyeCraftingRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        public static FlowerDyeCraftingRecipe fromNetwork(@Nonnull RegistryFriendlyByteBuf buffer) {
            return new FlowerDyeCraftingRecipe(buffer.readInt());
        }

        public static void toNetwork(@Nonnull RegistryFriendlyByteBuf buffer, FlowerDyeCraftingRecipe recipe) {
            buffer.writeInt(recipe.count);
        }
    }
}
