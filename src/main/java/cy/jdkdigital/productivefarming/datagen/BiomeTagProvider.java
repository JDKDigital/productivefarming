package cy.jdkdigital.productivefarming.datagen;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.registry.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class BiomeTagProvider extends BiomeTagsProvider
{
    public BiomeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> future, ExistingFileHelper helper) {
        super(output, future, ProductiveFarming.MODID, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModTags.Biomes.HAS_FLOWERS)
                .addTag(Tags.Biomes.IS_FOREST)
                .addTag(Tags.Biomes.IS_LUSH)
                .addTag(Tags.Biomes.IS_PLAINS)
                .addTag(Tags.Biomes.IS_FLORAL)
                .addTag(Tags.Biomes.IS_HILL)
                .addTag(Tags.Biomes.IS_JUNGLE)
                .addTag(Tags.Biomes.IS_MOUNTAIN)
                .addTag(Tags.Biomes.IS_MAGICAL)
                .addTag(Tags.Biomes.IS_SWAMP)
                .addTag(Tags.Biomes.IS_TEMPERATE_OVERWORLD)
                .addTag(Tags.Biomes.IS_WET_OVERWORLD);
    }

    @Override
    public String getName() {
        return "Productive Farming Biome Tag Provider";
    }
}
