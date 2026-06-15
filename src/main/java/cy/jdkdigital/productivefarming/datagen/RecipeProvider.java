package cy.jdkdigital.productivefarming.datagen;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.datagen.recipe.CropMutationRecipeBuilder;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import cy.jdkdigital.productivefarming.registry.ModTags;
import cy.jdkdigital.productivefarming.util.CropConfig;
import cy.jdkdigital.productivefarming.util.FishConfig;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider
{
    public RecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        RecipeOutput recipeOutput = this.output;

        // TODO tmp recipe, use a drying rack
        cookingRecipe(recipeOutput, "smelting", SmokingRecipe.SERIALIZER, 100, BuiltInRegistries.ITEM.getValue(rL("tea")), FarmingRegistrator.BLACK_TEA.get(), 0.35F, SmokingRecipe::new);
        cookingRecipe(recipeOutput, "smelting", SmokingRecipe.SERIALIZER, 100, BuiltInRegistries.ITEM.getValue(rL("tobacco")), FarmingRegistrator.DRIED_TOBACCO.get(), 0.35F, SmokingRecipe::new);
        cookingRecipe(recipeOutput, "smelting", SmokingRecipe.SERIALIZER, 100, BuiltInRegistries.ITEM.getValue(rL("luffa")), FarmingRegistrator.DRIED_LUFFA.get(), 0.35F, SmokingRecipe::new);

        for (FishConfig fish: FarmingRegistrator.FISHIES) {
            var raw = BuiltInRegistries.ITEM.getValue(rL("raw_" + fish.name()));
            if (fish.hasBlock()) {
                raw = BuiltInRegistries.ITEM.getValue(rL(fish.name()));
            }
            var cooked = BuiltInRegistries.ITEM.getValue(rL("cooked_" + fish.name()));
            cookingRecipe(recipeOutput, "smoking", SmokingRecipe.SERIALIZER, 100, raw, cooked, 0.35F, SmokingRecipe::new);
            cookingRecipe(recipeOutput, "campfire_cooking", CampfireCookingRecipe.SERIALIZER, 600, raw, cooked, 0.35F, CampfireCookingRecipe::new);
        }

        for (CropConfig crop : FarmingRegistrator.STEMS) {
            var melonBlock = BuiltInRegistries.BLOCK.getValue(rL(crop.name()));
            var melonSlice = BuiltInRegistries.ITEM.getValue(rL(crop.name() + "_slice"));
            var melonSeeds = BuiltInRegistries.ITEM.getValue(rL(crop.name() + "_seeds"));
            this.shapeless(RecipeCategory.MISC, melonBlock, 1)
                    .unlockedBy(getHasName(melonSlice), this.has(melonSlice))
                    .requires(melonSlice, 9)
                    .save(recipeOutput, key(crop.name()));
            this.shapeless(RecipeCategory.MISC, melonSeeds, 1)
                    .unlockedBy(getHasName(melonSlice), this.has(melonSlice))
                    .requires(melonSlice)
                    .save(recipeOutput, key(crop.name() + "_seeds"));
        }

        this.shaped(RecipeCategory.MISC, FarmingRegistrator.FARM_CONTROLLER.get())
                .unlockedBy("has_bricks", this.has(ItemTags.STONE_BRICKS))
                .pattern("BDB").pattern("BPB").pattern("BCB")
                .define('D', Items.DAYLIGHT_DETECTOR)
                .define('P', Items.COMPOSTER)
                .define('C', Items.COMPARATOR)
                .define('B', ItemTags.STONE_BRICKS)
                .save(recipeOutput, key("farm_controller"));

        this.shaped(RecipeCategory.MISC, FarmingRegistrator.FEEDING_TROUGH.get())
                .unlockedBy("has_copper", this.has(Tags.Items.INGOTS_COPPER))
                .pattern("B B").pattern("BCB").pattern("BBB")
                .define('C', Tags.Items.CHESTS_WOODEN)
                .define('B', Tags.Items.INGOTS_COPPER)
                .save(recipeOutput, key("feeding_trough"));

        this.shaped(RecipeCategory.MISC, FarmingRegistrator.WATERING_TROUGH.get())
                .unlockedBy("has_iron", this.has(Tags.Items.INGOTS_IRON))
                .pattern("B B").pattern("BCB").pattern("BBB")
                .define('C', Tags.Items.BUCKETS_EMPTY)
                .define('B', Tags.Items.INGOTS_IRON)
                .save(recipeOutput, key("watering_trough"));

        this.shapeless(RecipeCategory.MISC, Items.SPONGE, 1)
                .unlockedBy(getHasName(FarmingRegistrator.DRIED_LUFFA.get()), this.has(FarmingRegistrator.DRIED_LUFFA.get()))
                .requires(FarmingRegistrator.DRIED_LUFFA.get(), 9)
                .save(recipeOutput, key("sponge_from_dried_luffa"));

        this.shaped(RecipeCategory.MISC, FarmingRegistrator.CORN_COB_PIPE.get())
                .unlockedBy("has_corn", this.has(ModTags.Items.CORN))
                .pattern("CS")
                .define('C', ModTags.Items.CORN)
                .define('S', Tags.Items.RODS_WOODEN)
                .save(recipeOutput, key("corn_cob_pipe"));

        buildCrateRecipes(recipeOutput);
        buildSeedBagRecipes(recipeOutput);
        buildMutationRecipes(recipeOutput);
    }

    protected <T extends AbstractCookingRecipe> void cookingRecipe(RecipeOutput pFinishedRecipeConsumer, String pCookingMethod, RecipeSerializer<T> pCookingSerializer, int pCookingTime, ItemLike pIngredient, ItemLike pResult, float pExperience, AbstractCookingRecipe.Factory<T> recipeFactory) {
        SimpleCookingRecipeBuilder.generic(Ingredient.of(pIngredient), RecipeCategory.FOOD, CookingBookCategory.FOOD, pResult, pExperience, pCookingTime, recipeFactory).unlockedBy(getHasName(pIngredient), this.has(pIngredient)).save(pFinishedRecipeConsumer, key("cooking/" + getItemName(pResult) + "_from_" + pCookingMethod));
    }

    private void buildCrateRecipes(RecipeOutput recipeOutput) {
        FarmingRegistrator.CRATED_CROPS.forEach(crate -> {
            var crateItem = BuiltInRegistries.ITEM.getValue(rL(crate.getPath() + "_crate"));
            var cropItem = BuiltInRegistries.ITEM.getValue(crate);

            this.shapeless(RecipeCategory.MISC, cropItem, 9)
                    .unlockedBy(getHasName(cropItem), this.has(cropItem))
                    .requires(crateItem)
                    .save(recipeOutput, key("crates/" + crate.getPath() + "_unpack"));
            this.shaped(RecipeCategory.MISC, crateItem)
                    .unlockedBy(getHasName(cropItem), this.has(cropItem))
                    .pattern("###")
                    .pattern("###")
                    .pattern("###")
                    .define('#', cropItem)
                    .save(recipeOutput, key("crates/" + crate.getPath()));
        });
    }

    private void buildSeedBagRecipes(RecipeOutput recipeOutput) {
        FarmingRegistrator.SEED_BAGS.forEach(seedName -> {
            var bagItem = BuiltInRegistries.ITEM.getValue(rL(seedName.getPath() + "_bag"));
            var seedItem = BuiltInRegistries.ITEM.getValue(seedName);

            this.shapeless(RecipeCategory.MISC, seedItem, 9)
                    .unlockedBy(getHasName(seedItem), this.has(seedItem))
                    .requires(bagItem)
                    .save(recipeOutput, key("seed_bags/" + seedName.getPath() + "_unpack"));
            this.shaped(RecipeCategory.MISC, bagItem)
                    .unlockedBy(getHasName(seedItem), this.has(seedItem))
                    .pattern("###")
                    .pattern("###")
                    .pattern("###")
                    .define('#', seedItem)
                    .save(recipeOutput, key("seed_bags/" + seedName.getPath()));
        });
    }

    private void buildMutationRecipes(RecipeOutput recipeOutput) {
        // Corn mutations
        CropMutationRecipeBuilder.direct(rL("yellow_dent_corn"), Identifier.withDefaultNamespace("cornflower"), rL("blue_jade_corn"), 1.0f)
                .save(recipeOutput, rL("pollination/blue_jade_corn"));
        CropMutationRecipeBuilder.direct(rL("yellow_dent_corn"), rL("blue_jade_corn"), rL("black_aztec_corn"), 0.8f)
                .save(recipeOutput, rL( "pollination/black_aztec_corn"));
        CropMutationRecipeBuilder.direct(rL("blue_jade_corn"), rL("black_aztec_corn"), rL("sugar_pearl_corn"), 0.8f)
                .save(recipeOutput, rL( "pollination/sugar_pearl_corn"));
        CropMutationRecipeBuilder.direct(rL("black_aztec_corn"), rL("sugar_pearl_corn"), rL("rainbow_corn"), 0.8f)
                .save(recipeOutput, rL( "pollination/rainbow_corn"));

        // Tomato mutations
        CropMutationRecipeBuilder.direct(rL("roma_tomato"), Identifier.withDefaultNamespace("potato"), rL("beefsteak_tomato"), 1.0f)
                .save(recipeOutput, rL("pollination/beefsteak_tomato"));
        CropMutationRecipeBuilder.direct(rL("beefsteak_tomato"), rL("roma_tomato"), rL("black_beauty_tomato"), 0.3f)
                .save(recipeOutput, rL("pollination/black_beauty_tomato"));
        CropMutationRecipeBuilder.direct(rL("beefsteak_tomato"), rL("roma_tomato"), rL("blue_beauty_tomato"), 0.3f)
                .save(recipeOutput, rL("pollination/blue_beauty_tomato"));
        CropMutationRecipeBuilder.direct(rL("beefsteak_tomato"), rL("roma_tomato"), rL("white_wonder_tomato"), 0.3f)
                .save(recipeOutput, rL("pollination/white_wonder_tomato"));

        // Cherry tomato mutations
        CropMutationRecipeBuilder.direct(rL("cherry_tomato"), Identifier.withDefaultNamespace("beetroot"), rL("chocolate_pear_tomato"), 0.3f)
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

    private static Identifier rL(String name) {
        return Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, name);
    }

    private static ResourceKey<Recipe<?>> key(String name) {
        return ResourceKey.create(Registries.RECIPE, rL(name));
    }

    public static class Runner extends net.minecraft.data.recipes.RecipeProvider.Runner
    {
        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, registries);
        }

        @Override
        protected net.minecraft.data.recipes.RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new RecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return "Productive Farming Recipes";
        }
    }
}
