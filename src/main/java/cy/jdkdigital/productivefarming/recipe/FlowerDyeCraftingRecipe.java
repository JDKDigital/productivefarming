package cy.jdkdigital.productivefarming.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.common.block.ColorfulFlowerBlock;
import cy.jdkdigital.productivefarming.common.block.ColorfulTallFlowerBlock;
import cy.jdkdigital.productivefarming.registry.FarmingDataComponents;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import cy.jdkdigital.productivefarming.util.FarmUtil;
import cy.jdkdigital.productivelib.util.ColorUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;

import javax.annotation.Nonnull;
import java.util.Map;

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

    private static float colorDiff(int color1, int color2) {
        var color1Parts = ColorUtil.getCacheColor(color1);
        var color2Parts = ColorUtil.getCacheColor(color2);

        float redDifference = color1Parts[0] - color2Parts[0];
        float greenDifference = color1Parts[1] - color2Parts[1];
        float blueDifference = color1Parts[2] - color2Parts[2];

        return redDifference * redDifference + greenDifference * greenDifference + blueDifference * blueDifference;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        var stack = input.getItem(0);
        if (stack.has(FarmingDataComponents.COLOR)) {
            int color = stack.get(FarmingDataComponents.COLOR);
//            ProductiveFarming.LOGGER.info("color: " + color);
            float bestMatch = 0;
            Map<Integer, ResourceLocation> COLOR_MAP = FarmUtil.DYE_COLORS;
            if (ModList.get().isLoaded("dyenamics")) {
                COLOR_MAP.putAll(FarmUtil.DYENAMICS_DYE_COLORS);
            }

            ResourceLocation matchedColor = null;
            for (Map.Entry<Integer, ResourceLocation> entry : COLOR_MAP.entrySet()) {
                if (bestMatch == 0 || colorDiff(entry.getKey(), color) < bestMatch) {
                    bestMatch = colorDiff(entry.getKey(), color);
                    matchedColor = entry.getValue();
                }
            }
            var output = BuiltInRegistries.ITEM.get(matchedColor).getDefaultInstance();
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
