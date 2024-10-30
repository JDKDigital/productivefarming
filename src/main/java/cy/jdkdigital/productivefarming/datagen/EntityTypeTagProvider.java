package cy.jdkdigital.productivefarming.datagen;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import cy.jdkdigital.productivefarming.registry.ModTags;
import cy.jdkdigital.productivefarming.util.FishConfig;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import java.util.concurrent.CompletableFuture;

public class EntityTypeTagProvider extends EntityTypeTagsProvider
{
    public EntityTypeTagProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pProvider) {
        super(pOutput, pProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(ModTags.FISH_FARM_ENTITIES).add(EntityType.COD).add(EntityType.SALMON).add(EntityType.TROPICAL_FISH).add(EntityType.SQUID).add(EntityType.GLOW_SQUID).add(EntityType.PUFFERFISH);
        for (FishConfig fish: FarmingRegistrator.FISHIES) {
            if (fish.entitySupplier() != null) {
                tag(ModTags.FISH_FARM_ENTITIES).add(BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, fish.name())));
            }
        }
    }

    @Override
    public String getName() {
        return "Productive Farming Entity Tags Provider";
    }
}
