package cy.jdkdigital.productivefarming.datagen;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.common.block.DoubleCropBlock;
import cy.jdkdigital.productivefarming.datagen.recipe.BotanyPotBlockDerivedCropRecipeBuilder;
import cy.jdkdigital.productivefarming.datagen.recipe.CropMutationRecipeBuilder;
import cy.jdkdigital.productivefarming.integrations.botanypots.itemdrops.ProductiveDropProvider;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import cy.jdkdigital.productivefarming.registry.ModTags;
import cy.jdkdigital.productivefarming.util.CropConfig;
import cy.jdkdigital.productivefarming.util.FishConfig;
import net.darkhax.botanypots.common.impl.data.display.types.AgingDisplayState;
import net.darkhax.botanypots.common.impl.data.display.types.BasicOptions;
import net.darkhax.botanypots.common.impl.data.display.types.SimpleDisplayState;
import net.darkhax.botanypots.common.impl.data.itemdrops.SimpleDropProvider;
import net.darkhax.botanypots.common.impl.data.recipe.crop.BasicCrop;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.crafting.BlockTagIngredient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider implements IConditionBuilder
{
    public RecipeProvider(PackOutput gen, CompletableFuture<HolderLookup.Provider> registries) {
        super(gen, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        // TODO tmp recipe, use a drying rack
        simpleCookingRecipe(recipeOutput, "smelting", RecipeSerializer.SMOKING_RECIPE, 100, BuiltInRegistries.ITEM.get(rL("tea")), FarmingRegistrator.BLACK_TEA.get(), 0.35F, SmokingRecipe::new);
        simpleCookingRecipe(recipeOutput, "smelting", RecipeSerializer.SMOKING_RECIPE, 100, BuiltInRegistries.ITEM.get(rL("tobacco")), FarmingRegistrator.DRIED_TOBACCO.get(), 0.35F, SmokingRecipe::new);
        simpleCookingRecipe(recipeOutput, "smelting", RecipeSerializer.SMOKING_RECIPE, 100, BuiltInRegistries.ITEM.get(rL("luffa")), FarmingRegistrator.DRIED_LUFFA.get(), 0.35F, SmokingRecipe::new);

        for (FishConfig fish: FarmingRegistrator.FISHIES) {
            var raw = BuiltInRegistries.ITEM.get(rL("raw_" + fish.name()));
            if (fish.hasBlock()) {
                raw = BuiltInRegistries.ITEM.get(rL(fish.name()));
            }
            var cooked = BuiltInRegistries.ITEM.get(rL("cooked_" + fish.name()));
            simpleCookingRecipe(recipeOutput, "smoking", RecipeSerializer.SMOKING_RECIPE, 100, raw, cooked, 0.35F, SmokingRecipe::new);
            simpleCookingRecipe(recipeOutput, "campfire_cooking", RecipeSerializer.CAMPFIRE_COOKING_RECIPE, 600, raw, cooked, 0.35F, CampfireCookingRecipe::new);
        }

        for (CropConfig crop : FarmingRegistrator.STEMS) {
            var melonBlock = BuiltInRegistries.BLOCK.get(rL(crop.name()));
            var melonSlice = BuiltInRegistries.ITEM.get(rL(crop.name() + "_slice"));
            var melonSeeds = BuiltInRegistries.ITEM.get(rL(crop.name() + "_seeds"));
            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, melonBlock, 1)
                    .unlockedBy(getHasName(melonSlice), has(melonSlice))
                    .requires(melonSlice, 9)
                    .save(recipeOutput, rL(crop.name()));
            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC,melonSeeds, 1)
                    .unlockedBy(getHasName(melonSlice), has(melonSlice))
                    .requires(melonSlice)
                    .save(recipeOutput, rL(crop.name() + "_seeds"));
        }

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, FarmingRegistrator.FARM_CONTROLLER.get())
                .unlockedBy("has_bricks", has(ItemTags.STONE_BRICKS))
                .pattern("BDB").pattern("BPB").pattern("BCB")
                .define('D', Items.DAYLIGHT_DETECTOR)
                .define('P', Items.COMPOSTER)
                .define('C', Items.COMPARATOR)
                .define('B', ItemTags.STONE_BRICKS)
                .save(recipeOutput, rL("farm_controller"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, FarmingRegistrator.FEEDING_TROUGH.get())
                .unlockedBy("has_copper", has(Tags.Items.INGOTS_COPPER))
                .pattern("B B").pattern("BCB").pattern("BBB")
                .define('C', Tags.Items.CHESTS_WOODEN)
                .define('B', Tags.Items.INGOTS_COPPER)
                .save(recipeOutput, rL("feeding_trough"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, FarmingRegistrator.WATERING_TROUGH.get())
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                .pattern("B B").pattern("BCB").pattern("BBB")
                .define('C', Tags.Items.BUCKETS_EMPTY)
                .define('B', Tags.Items.INGOTS_IRON)
                .save(recipeOutput, rL("watering_trough"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.SPONGE, 1)
                .unlockedBy(getHasName(FarmingRegistrator.DRIED_LUFFA.get()), has(FarmingRegistrator.DRIED_LUFFA.get()))
                .requires(FarmingRegistrator.DRIED_LUFFA.get(), 9)
                .save(recipeOutput, rL("sponge_from_dried_luffa"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, FarmingRegistrator.CORN_COB_PIPE.get())
                .unlockedBy("has_corn", has(ModTags.Items.CORN))
                .pattern("CS")
                .define('C', ModTags.Items.CORN)
                .define('S', Tags.Items.RODS_WOODEN)
                .save(recipeOutput, rL("corn_cob_pipe"));

        buildCrateRecipes(recipeOutput);
        buildSeedBagRecipes(recipeOutput);
        buildMutationRecipes(recipeOutput);
        if (ModList.get().isLoaded("botanypots")) {
            BotanyPotsCompat.buildRecipes(recipeOutput);
        }
    }

    protected static <T extends AbstractCookingRecipe> void simpleCookingRecipe(RecipeOutput pFinishedRecipeConsumer, String pCookingMethod, RecipeSerializer<T> pCookingSerializer, int pCookingTime, ItemLike pIngredient, ItemLike pResult, float pExperience, AbstractCookingRecipe.Factory<T> recipeFactory) {
        SimpleCookingRecipeBuilder.generic(Ingredient.of(pIngredient), RecipeCategory.FOOD, pResult, pExperience, pCookingTime, pCookingSerializer, recipeFactory).unlockedBy(getHasName(pIngredient), has(pIngredient)).save(pFinishedRecipeConsumer, rL("cooking/" + getItemName(pResult) + "_from_" + pCookingMethod));
    }

    private void buildCrateRecipes(RecipeOutput recipeOutput) {
        FarmingRegistrator.CRATED_CROPS.forEach(crate -> {
            var crateItem = BuiltInRegistries.ITEM.get(rL(crate.getPath() + "_crate"));
            var cropItem = BuiltInRegistries.ITEM.get(crate);

            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, cropItem, 9)
                    .unlockedBy(getHasName(cropItem), has(cropItem))
                    .requires(crateItem)
                    .save(recipeOutput, rL("crates/" + crate.getPath() + "_unpack"));
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, crateItem)
                    .unlockedBy(getHasName(cropItem), has(cropItem))
                    .pattern("###")
                    .pattern("###")
                    .pattern("###")
                    .define('#', cropItem)
                    .save(recipeOutput, rL("crates/" + crate.getPath()));
        });
    }

    private void buildSeedBagRecipes(RecipeOutput recipeOutput) {
        FarmingRegistrator.SEED_BAGS.forEach(seedName -> {
            var bagItem = BuiltInRegistries.ITEM.get(rL(seedName.getPath() + "_bag"));
            var seedItem = BuiltInRegistries.ITEM.get(seedName);

            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, seedItem, 9)
                    .unlockedBy(getHasName(seedItem), has(seedItem))
                    .requires(bagItem)
                    .save(recipeOutput, rL("seed_bags/" + seedName.getPath() + "_unpack"));
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, bagItem)
                    .unlockedBy(getHasName(seedItem), has(seedItem))
                    .pattern("###")
                    .pattern("###")
                    .pattern("###")
                    .define('#', seedItem)
                    .save(recipeOutput, rL("seed_bags/" + seedName.getPath()));
        });
    }

    private void buildMutationRecipes(RecipeOutput recipeOutput) {
        // Corn mutations
        CropMutationRecipeBuilder.direct(rL("yellow_dent_corn"), ResourceLocation.withDefaultNamespace("cornflower"), rL("blue_jade_corn"), 1.0f)
                .save(recipeOutput, rL("pollination/blue_jade_corn"));
        CropMutationRecipeBuilder.direct(rL("yellow_dent_corn"), rL("blue_jade_corn"), rL("black_aztec_corn"), 0.8f)
                .save(recipeOutput, rL( "pollination/black_aztec_corn"));
        CropMutationRecipeBuilder.direct(rL("blue_jade_corn"), rL("black_aztec_corn"), rL("sugar_pearl_corn"), 0.8f)
                .save(recipeOutput, rL( "pollination/sugar_pearl_corn"));
        CropMutationRecipeBuilder.direct(rL("black_aztec_corn"), rL("sugar_pearl_corn"), rL("rainbow_corn"), 0.8f)
                .save(recipeOutput, rL( "pollination/rainbow_corn"));

        // Tomato mutations
        CropMutationRecipeBuilder.direct(rL("roma_tomato"), ResourceLocation.withDefaultNamespace("potato"), rL("beefsteak_tomato"), 1.0f)
                .save(recipeOutput, rL("pollination/beefsteak_tomato"));
        CropMutationRecipeBuilder.direct(rL("beefsteak_tomato"), rL("roma_tomato"), rL("black_beauty_tomato"), 0.3f)
                .save(recipeOutput, rL("pollination/black_beauty_tomato"));
        CropMutationRecipeBuilder.direct(rL("beefsteak_tomato"), rL("roma_tomato"), rL("blue_beauty_tomato"), 0.3f)
                .save(recipeOutput, rL("pollination/blue_beauty_tomato"));
        CropMutationRecipeBuilder.direct(rL("beefsteak_tomato"), rL("roma_tomato"), rL("white_wonder_tomato"), 0.3f)
                .save(recipeOutput, rL("pollination/white_wonder_tomato"));

        // Cherry tomato mutations
            CropMutationRecipeBuilder.direct(rL("cherry_tomato"), ResourceLocation.withDefaultNamespace("beetroot"), rL("chocolate_pear_tomato"), 0.3f)
                .save(recipeOutput, rL("pollination/chocolate_pear_tomato"));
        CropMutationRecipeBuilder.direct(rL("cherry_tomato"), rL("chocolate_pear_tomato"), rL("yellow_pear_tomato"), 0.3f)
                .save(recipeOutput, rL("pollination/yellow_pear_tomato"));
        CropMutationRecipeBuilder.direct(rL("yellow_pear_tomato"), rL("chocolate_pear_tomato"), rL("sungold_tomato"), 0.3f)
                .save(recipeOutput, rL("pollination/sungold_tomato"));

        // Bell Pepper mutations
        CropMutationRecipeBuilder.direct(rL("yellow_bell_pepper"), rL("red_bell_pepper"), rL("orange_bell_pepper"), 0.3f)
                .save(recipeOutput, rL("pollination/orange_bell_pepper"));
        CropMutationRecipeBuilder.direct(rL("green_bell_pepper"), rL("orange_bell_pepper"), rL("black_bell_pepper"), 0.3f)
                .save(recipeOutput, rL("pollination/black_bell_pepper"));
        CropMutationRecipeBuilder.direct(rL("black_bell_pepper"), rL("red_bell_pepper"), rL("purple_bell_pepper"), 0.3f)
                .save(recipeOutput, rL("pollination/purple_bell_pepper"));
        CropMutationRecipeBuilder.direct(rL("yellow_bell_pepper"), rL("orange_bell_pepper"), rL("white_bell_pepper"), 0.3f)
                .save(recipeOutput, rL("pollination/white_bell_pepper"));

        // Grape mutations
        CropMutationRecipeBuilder.direct(rL("red_grape"), rL("green_grape"), rL("concord_grape"), 0.3f)
                .save(recipeOutput, rL("pollination/concord_grape"));
        CropMutationRecipeBuilder.direct(rL("concord_grape"), rL("green_grape"), rL("cotton_candy_grape"), 0.3f)
                .save(recipeOutput, rL("pollination/cotton_candy_grape"));

        // Berry mutation (miracle berry, golden raspberry)
        CropMutationRecipeBuilder.direct(rL("raspberry"), rL("sungold_tomato"), rL("golden_raspberry"), 0.7f)
                .save(recipeOutput, rL("pollination/golden_raspberry"));
        CropMutationRecipeBuilder.direct(rL("golden_raspberry"), rL("goji_berry"), rL("miracle_berry"), 0.5f)
                .save(recipeOutput, rL("pollination/miracle_berry"));
    }

    private static ResourceLocation rL(String name) {
        return ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, name);
    }

    static class BotanyPotsCompat {
        protected static void buildRecipes(RecipeOutput recipeOutput) {
            for (CropConfig crop: FarmingRegistrator.HERBS) {
                cropRecipe(crop, recipeOutput);
            }
            for (CropConfig crop: FarmingRegistrator.BERRIES) {
                cropRecipe(crop, recipeOutput);
            }
            for (CropConfig crop: FarmingRegistrator.CROPS) {
                cropRecipe(crop, recipeOutput);
            }
            for (CropConfig crop: FarmingRegistrator.TRELLIS) {
                cropRecipe(crop, recipeOutput);
            }
            for (CropConfig crop: FarmingRegistrator.VERTICAL_TRELLIS) {
                cropRecipe(crop, recipeOutput);
            }
            for (CropConfig crop: FarmingRegistrator.GRAPES) {
                cropRecipe(crop, recipeOutput);
            }
            for (CropConfig crop: FarmingRegistrator.STEMS) {
                cropRecipe(crop, recipeOutput);
            }
            for (CropConfig crop: FarmingRegistrator.SHROOMS) {
                shroomRecipe(crop, recipeOutput);
            }
        }

        private static void cropRecipe(CropConfig crop, RecipeOutput recipeOutput) {
            var seed = BuiltInRegistries.ITEM.get(rL(crop.hasSeed() ? crop.name() + "_seeds" : crop.name()));
            var cropBlock = BuiltInRegistries.BLOCK.get(rL(crop.name()));
            List<SimpleDropProvider.SimpleDrop> drops = new ArrayList<>(){{
                add(new SimpleDropProvider.SimpleDrop(BuiltInRegistries.ITEM.get(rL(crop.name())).getDefaultInstance(), 1f));
            }};
            if (crop.hasSeed()) {
                drops.add(new SimpleDropProvider.SimpleDrop(seed.getDefaultInstance(), 0.1f));
            }
            if (cropBlock instanceof DoubleCropBlock) {
                BotanyPotBlockDerivedCropRecipeBuilder.drops(cropBlock, Ingredient.of(seed), BasicCrop.DIRT, List.of(new SimpleDropProvider(drops)), List.of(
                                new SimpleDisplayState(cropBlock.defaultBlockState().setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER).setValue(((DoubleCropBlock) cropBlock).getAgeProperty(), ((DoubleCropBlock) cropBlock).getMaxAge()), BasicOptions.ofDefault()),
                                new SimpleDisplayState(cropBlock.defaultBlockState().setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER).setValue(((DoubleCropBlock) cropBlock).getAgeProperty(), ((DoubleCropBlock) cropBlock).getMaxAge()), BasicOptions.ofDefault())
                        ))
                        .save(recipeOutput.withConditions(new ModLoadedCondition("botanypots")), rL("botanypots/" + crop.name()));
            } else {
                BotanyPotBlockDerivedCropRecipeBuilder.drops(cropBlock, Ingredient.of(seed), List.of(new SimpleDropProvider(drops)))
                        .save(recipeOutput.withConditions(new ModLoadedCondition("botanypots")), rL("botanypots/" + crop.name()));
            }
        }

        private static void shroomRecipe(CropConfig shroom, RecipeOutput recipeOutput) {
            var shroomItem = BuiltInRegistries.ITEM.get(rL(shroom.name()));
            var growthBlock = BuiltInRegistries.BLOCK.get(rL(shroom.name() + "_growth"));
            List<SimpleDropProvider.SimpleDrop> drops = new ArrayList<>(){{
                add(new SimpleDropProvider.SimpleDrop(shroomItem.getDefaultInstance(), 1f));
            }};
            BotanyPotBlockDerivedCropRecipeBuilder.drops(growthBlock, Ingredient.of(shroomItem), new BlockTagIngredient(BlockTags.MUSHROOM_GROW_BLOCK).toVanilla(), List.of(new SimpleDropProvider(drops)), List.of(
                            new AgingDisplayState(growthBlock, BasicOptions.ofDefault())
                    ))
                    .save(recipeOutput.withConditions(new ModLoadedCondition("botanypots")), rL("botanypots/" + shroom.name()));
        }

        private static void productiveCropRecipe(CropConfig crop, RecipeOutput recipeOutput) {
            var seed = BuiltInRegistries.ITEM.get(rL(crop.hasSeed() ? crop.name() + "_seeds" : crop.name()));
            var cropBlock = BuiltInRegistries.BLOCK.get(rL(crop.name()));
            List<ProductiveDropProvider.ProductiveDrop> drops = new ArrayList<>(){{
                add(new ProductiveDropProvider.ProductiveDrop(BuiltInRegistries.ITEM.get(rL(crop.name())).getDefaultInstance(), 1f));
            }};
            if (crop.hasSeed()) {
                drops.add(new ProductiveDropProvider.ProductiveDrop(seed.getDefaultInstance(), 0.1f));
            }
            if (cropBlock instanceof DoubleCropBlock) {
                BotanyPotBlockDerivedCropRecipeBuilder.drops(cropBlock, Ingredient.of(seed), BasicCrop.DIRT, List.of(new ProductiveDropProvider(drops)), List.of(
                                new SimpleDisplayState(cropBlock.defaultBlockState().setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER).setValue(((DoubleCropBlock) cropBlock).getAgeProperty(), ((DoubleCropBlock) cropBlock).getMaxAge()), BasicOptions.ofDefault()),
                                new SimpleDisplayState(cropBlock.defaultBlockState().setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER).setValue(((DoubleCropBlock) cropBlock).getAgeProperty(), ((DoubleCropBlock) cropBlock).getMaxAge()), BasicOptions.ofDefault())
                        ))
                        .save(recipeOutput.withConditions(new ModLoadedCondition("botanypots")), rL("botanypots/" + crop.name()));
            } else {
                BotanyPotBlockDerivedCropRecipeBuilder.drops(cropBlock, Ingredient.of(seed), List.of(new ProductiveDropProvider(drops)))
                        .save(recipeOutput.withConditions(new ModLoadedCondition("botanypots")), rL("botanypots/" + crop.name()));
            }
        }
    }
}
