package cy.jdkdigital.productivefarming.util;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.recipe.CropPollinationRecipe;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class RecipeHelper
{
    public static RecipeHolder<CropPollinationRecipe> getPollinationRecipe(Level level, ResourceLocation targetCrop, ResourceLocation pollenCrop) {

        List<RecipeHolder<CropPollinationRecipe>> matchedRecipes = new ArrayList<>();
        var allRecipes = level.getRecipeManager().getAllRecipesFor(FarmingRegistrator.CROP_POLLINATION_TYPE.get());
        ProductiveFarming.LOGGER.info("find recipe. targetCrop: " + targetCrop + " pollenCrop: " + pollenCrop + " in " + allRecipes.size() + " recipes");
        for (RecipeHolder<CropPollinationRecipe> CropPollinationRecipe : allRecipes) {
            if (CropPollinationRecipe.value().matches(targetCrop, pollenCrop)) {
                matchedRecipes.add(CropPollinationRecipe);
            }
        }
        return !matchedRecipes.isEmpty() ? matchedRecipes.get(level.random.nextInt(matchedRecipes.size())) : null;
    }
}
