package cy.jdkdigital.productivefarming.datagen.recipe;

import cy.jdkdigital.productivefarming.recipe.CropPollinationRecipe;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

public record CropPollinationRecipeBuilder(ResourceLocation targetCrop, ResourceLocation pollenCrop, ResourceLocation result, float chance) implements RecipeBuilder
{
    public static CropPollinationRecipeBuilder direct(ResourceLocation targetCrop, ResourceLocation pollenCrop, ResourceLocation result, float chance) {
        return new CropPollinationRecipeBuilder(targetCrop, pollenCrop, result, chance);
    }

    @Override
    public RecipeBuilder unlockedBy(String pName, Criterion<?> pCriterion) {
        return null;
    }

    @Override
    public RecipeBuilder group(@Nullable String p_176495_) {
        return null;
    }

    @Override
    public Item getResult() {
        return null;
    }

    @Override
    public void save(RecipeOutput consumer, ResourceLocation id) {
        consumer.accept(id, new CropPollinationRecipe(targetCrop, pollenCrop, result, chance), null);
    }
}
