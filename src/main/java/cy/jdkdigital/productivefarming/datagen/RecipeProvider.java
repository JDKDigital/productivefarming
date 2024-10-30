package cy.jdkdigital.productivefarming.datagen;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import cy.jdkdigital.productivefarming.util.CropConfig;
import cy.jdkdigital.productivefarming.util.FishConfig;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.concurrent.CompletableFuture;

public class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider implements IConditionBuilder
{
    public RecipeProvider(PackOutput gen, CompletableFuture<HolderLookup.Provider> registries) {
        super(gen, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        for (FishConfig fish: FarmingRegistrator.FISHIES) {
            var raw = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "raw_" + fish.name()));
            if (fish.hasBlock()) {
                raw = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, fish.name()));
            }
            var cooked = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "cooked_" + fish.name()));
            simpleCookingRecipe(recipeOutput, "smoking", RecipeSerializer.SMOKING_RECIPE, 100, raw, cooked, 0.35F, SmokingRecipe::new);
            simpleCookingRecipe(recipeOutput, "campfire_cooking", RecipeSerializer.CAMPFIRE_COOKING_RECIPE, 600, raw, cooked, 0.35F, CampfireCookingRecipe::new);
        }

        for (CropConfig crop : FarmingRegistrator.STEMS) {
            var melonBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name()));
            var melonSlice = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name() + "_slice"));
            var melonSeeds = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name() + "_seeds"));
            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, melonBlock, 1)
                    .unlockedBy(getHasName(melonSlice), has(melonSlice))
                    .requires(melonSlice, 9)
                    .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name()));
            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC,melonSeeds, 1)
                    .unlockedBy(getHasName(melonSlice), has(melonSlice))
                    .requires(melonSlice)
                    .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name() + "_seeds"));
        }

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.SPONGE, 1)
                .unlockedBy(getHasName(FarmingRegistrator.DRIED_LUFFA.get()), has(FarmingRegistrator.DRIED_LUFFA.get()))
                .requires(FarmingRegistrator.DRIED_LUFFA.get(), 9)
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "sponge_from_dried_luffa"));

        buildCrateRecipes(recipeOutput);
        buildSeedBagRecipes(recipeOutput);
    }

    protected static <T extends AbstractCookingRecipe> void simpleCookingRecipe(RecipeOutput pFinishedRecipeConsumer, String pCookingMethod, RecipeSerializer<T> pCookingSerializer, int pCookingTime, ItemLike pIngredient, ItemLike pResult, float pExperience, AbstractCookingRecipe.Factory<T> recipeFactory) {
        SimpleCookingRecipeBuilder.generic(Ingredient.of(pIngredient), RecipeCategory.FOOD, pResult, pExperience, pCookingTime, pCookingSerializer, recipeFactory).unlockedBy(getHasName(pIngredient), has(pIngredient)).save(pFinishedRecipeConsumer, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "cooking/" + getItemName(pResult) + "_from_" + pCookingMethod));
    }

    private void buildCrateRecipes(RecipeOutput recipeOutput) {
        FarmingRegistrator.CRATED_CROPS.forEach(crate -> {
            var crateItem = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crate.getPath() + "_crate"));
            var cropItem = BuiltInRegistries.ITEM.get(crate);

            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, cropItem, 9)
                    .unlockedBy(getHasName(cropItem), has(cropItem))
                    .requires(crateItem)
                    .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "crates/" + crate.getPath() + "_unpack"));
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, crateItem)
                    .unlockedBy(getHasName(cropItem), has(cropItem))
                    .pattern("###")
                    .pattern("###")
                    .pattern("###")
                    .define('#', cropItem)
                    .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "crates/" + crate.getPath()));
        });
    }

    private void buildSeedBagRecipes(RecipeOutput recipeOutput) {
        FarmingRegistrator.SEED_BAGS.forEach(seedName -> {
            var bagItem = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, seedName.getPath() + "_bag"));
            var seedItem = BuiltInRegistries.ITEM.get(seedName);

            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, seedItem, 9)
                    .unlockedBy(getHasName(seedItem), has(seedItem))
                    .requires(bagItem)
                    .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "seed_bags/" + seedName.getPath() + "_unpack"));
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, bagItem)
                    .unlockedBy(getHasName(seedItem), has(seedItem))
                    .pattern("###")
                    .pattern("###")
                    .pattern("###")
                    .define('#', seedItem)
                    .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "seed_bags/" + seedName.getPath()));
        });
    }
}
