package cy.jdkdigital.productivefarming.datagen.recipe;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.recipe.CropMutationRecipe;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;

public record CropMutationRecipeBuilder(Identifier targetCrop, Identifier pollenCrop, Identifier result, float chance) implements RecipeBuilder
{
    public static CropMutationRecipeBuilder direct(Identifier targetCrop, Identifier pollenCrop, Identifier result, float chance) {
        return new CropMutationRecipeBuilder(targetCrop, pollenCrop, result, chance);
    }

    @Override
    public RecipeBuilder unlockedBy(String pName, Criterion<?> pCriterion) {
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String p_176495_) {
        return this;
    }

    @Override
    public ResourceKey<Recipe<?>> defaultId() {
        return ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "empty"));
    }

    @Override
    public void save(RecipeOutput consumer, ResourceKey<Recipe<?>> id) {
        consumer.accept(id, new CropMutationRecipe(targetCrop, pollenCrop, result, chance), null);
    }

    public void save(RecipeOutput consumer, Identifier id) {
        save(consumer, ResourceKey.create(Registries.RECIPE, id));
    }
}
