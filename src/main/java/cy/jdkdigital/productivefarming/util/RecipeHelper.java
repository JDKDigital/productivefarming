package cy.jdkdigital.productivefarming.util;

import cy.jdkdigital.productivefarming.recipe.CropMutationRecipe;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class RecipeHelper
{
    public static RecipeHolder<CropMutationRecipe> getPollinationRecipe(Level level, Identifier targetCrop, Identifier pollenCrop) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return null;
        }
        List<RecipeHolder<CropMutationRecipe>> matchedRecipes = new ArrayList<>();
        var allRecipes = serverLevel.recipeAccess().recipeMap().byType(FarmingRegistrator.CROP_MUTATION_TYPE.get());
        for (RecipeHolder<CropMutationRecipe> cropPollinationRecipe : allRecipes) {
            if (cropPollinationRecipe.value().matches(targetCrop, pollenCrop)) {
                matchedRecipes.add(cropPollinationRecipe);
            }
        }
        return !matchedRecipes.isEmpty() ? matchedRecipes.get(serverLevel.getRandom().nextInt(matchedRecipes.size())) : null;
    }

    public static boolean isMutatedCrop(CropConfig crop) {
        return  crop.name().equals("concord_grape") ||
                crop.name().equals("cotton_candy_grape") ||
                crop.name().equals("miracle_berry") ||
                crop.name().equals("golden_raspberry") ||
                crop.name().equals("white_wonder_tomato") ||
                crop.name().equals("blue_beauty_tomato") ||
                crop.name().equals("black_beauty_tomato") ||
                crop.name().equals("beefsteak_tomato") ||
                crop.name().equals("rainbow_corn") ||
                crop.name().equals("sugar_pearl_corn") ||
                crop.name().equals("black_aztec_corn") ||
                crop.name().equals("blue_jade_corn") ||
                crop.name().equals("chocolate_pear_tomato") ||
                crop.name().equals("yellow_pear_tomato") ||
                crop.name().equals("sungold_tomato") ||
                crop.name().equals("orange_bell_pepper") ||
                crop.name().equals("black_bell_pepper") ||
                crop.name().equals("purple_bell_pepper") ||
                crop.name().equals("white_bell_pepper");
    }
}
