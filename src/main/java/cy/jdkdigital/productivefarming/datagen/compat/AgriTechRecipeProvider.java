package cy.jdkdigital.productivefarming.datagen.compat;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.datagen.recipe.AgriTechCropRecipeBuilder;
import cy.jdkdigital.productivefarming.datagen.recipe.AgriTechEvolvedCropRecipeBuilder;
import cy.jdkdigital.productivefarming.datagen.recipe.CropDropSpec;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import cy.jdkdigital.productivefarming.util.CropConfig;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class AgriTechRecipeProvider extends RecipeProvider
{
    private static final String AGRITECH = "agritechtwo";
    private static final String AGRITECH_EVOLVED = "agritechevolved";
    private static final TagKey<Item> AGRITECH_SOILS = ItemTags.create(Identifier.fromNamespaceAndPath(AGRITECH, "farmland_soils"));
    private static final TagKey<Item> AGRITECH_EVOLVED_SOILS = ItemTags.create(Identifier.fromNamespaceAndPath(AGRITECH_EVOLVED, "farmland_soils"));

    public AgriTechRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        RecipeOutput agritech = this.output.withConditions(new ModLoadedCondition(AGRITECH));
        RecipeOutput agritechEvolved = this.output.withConditions(new ModLoadedCondition(AGRITECH_EVOLVED));

        Stream.of(FarmingRegistrator.CROPS, FarmingRegistrator.HERBS, FarmingRegistrator.BERRIES,
                        FarmingRegistrator.TRELLIS, FarmingRegistrator.VERTICAL_TRELLIS, FarmingRegistrator.GRAPES)
                .flatMap(List::stream)
                .forEach(crop -> addCropRecipe(agritech, agritechEvolved, crop));

        FarmingRegistrator.STEMS.forEach(crop -> addStemRecipe(agritech, agritechEvolved, crop));
    }

    private void addCropRecipe(RecipeOutput agritech, RecipeOutput agritechEvolved, CropConfig crop) {
        Item produce = item(crop.name());
        if (produce.equals(Items.AIR)) {
            return;
        }

        List<CropDropSpec> drops = new ArrayList<>();
        Item seed;
        if (crop.hasSeed()) {
            seed = item(crop.name() + "_seeds");
            if (seed.equals(Items.AIR)) {
                return;
            }
            drops.add(new CropDropSpec(produce, 1, 1, 1.0F));
            drops.add(new CropDropSpec(seed, 1, 2, 0.5F));
        } else {
            seed = produce;
            drops.add(new CropDropSpec(produce, 2, 5, 1.0F));
        }

        save(agritech, agritechEvolved, crop.name(), Ingredient.of(seed), drops);
    }

    private void addStemRecipe(RecipeOutput agritech, RecipeOutput agritechEvolved, CropConfig crop) {
        Item seed = item(crop.name() + "_seeds");
        Item slice = item(crop.name() + "_slice");
        if (seed.equals(Items.AIR) || slice.equals(Items.AIR)) {
            return;
        }

        List<CropDropSpec> drops = new ArrayList<>();
        drops.add(new CropDropSpec(slice, 3, 7, 1.0F));

        save(agritech, agritechEvolved, crop.name(), Ingredient.of(seed), drops);
    }

    private void save(RecipeOutput agritech, RecipeOutput agritechEvolved, String name, Ingredient seed, List<CropDropSpec> drops) {
        Identifier agritechId = Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "agritech/" + name);
        AgriTechCropRecipeBuilder.crop(agritechId, seed, List.of(tag(AGRITECH_SOILS)), drops)
                .save(agritech, ResourceKey.create(Registries.RECIPE, agritechId));

        Identifier evolvedId = Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "agritech_evolved/" + name);
        AgriTechEvolvedCropRecipeBuilder.crop(evolvedId, seed, List.of(tag(AGRITECH_EVOLVED_SOILS)), drops)
                .save(agritechEvolved, ResourceKey.create(Registries.RECIPE, evolvedId));
    }

    private static Item item(String name) {
        return BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, name));
    }

    public static class Runner extends RecipeProvider.Runner
    {
        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new AgriTechRecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return "Productive Farming AgriTech Compat Recipes";
        }
    }
}
