package cy.jdkdigital.productivefarming.datagen.recipe;

import com.misterd.agritechevolved.recipe.CropRecipe;
import com.misterd.agritechevolved.recipe.DropEntry;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record AgriTechEvolvedCropRecipeBuilder(Identifier id, Ingredient seed, List<Ingredient> soils, List<CropDropSpec> drops) implements RecipeBuilder
{
    public static AgriTechEvolvedCropRecipeBuilder crop(Identifier id, Ingredient seed, List<Ingredient> soils, List<CropDropSpec> drops) {
        return new AgriTechEvolvedCropRecipeBuilder(id, seed, soils, drops);
    }

    @Override
    public RecipeBuilder unlockedBy(String pName, Criterion<?> pCriterion) {
        return null;
    }

    @Override
    public RecipeBuilder group(@Nullable String pGroup) {
        return null;
    }

    @Override
    public ResourceKey<Recipe<?>> defaultId() {
        return ResourceKey.create(Registries.RECIPE, id);
    }

    @Override
    public void save(RecipeOutput pRecipeOutput, ResourceKey<Recipe<?>> pId) {
        List<DropEntry> entries = drops.stream().map(d -> new DropEntry(d.item(), d.min(), d.max(), d.chance())).toList();
        pRecipeOutput.accept(pId, new CropRecipe(seed, soils, entries), null);
    }
}
