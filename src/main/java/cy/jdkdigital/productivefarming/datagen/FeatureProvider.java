package cy.jdkdigital.productivefarming.datagen;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.CountConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class FeatureProvider extends DatapackBuiltinEntriesProvider
{
    public FeatureProvider(PackOutput output, RegistrySetBuilder builder, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, builder, Set.of(ProductiveFarming.MODID));
    }

    public static RegistrySetBuilder getBuilder() {
        var builder = new RegistrySetBuilder();
        builder.add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, context -> {
            // Clam
            var biomeHolder = context.lookup(Registries.BIOME).getOrThrow(BiomeTags.IS_RIVER);
            var configuredFeature = new ConfiguredFeature<>(FarmingRegistrator.CLAM_FEATURE.get(), new CountConfiguration(10));
            var placedFeature = new PlacedFeature(Holder.direct(configuredFeature), List.of(BiomeFilter.biome(), InSquarePlacement.spread(), RarityFilter.onAverageOnceEvery(10), HeightmapPlacement.onHeightmap(Heightmap.Types.OCEAN_FLOOR_WG)));

            var modifier = new BiomeModifiers.AddFeaturesBiomeModifier(biomeHolder, HolderSet.direct(Holder.direct(placedFeature)), GenerationStep.Decoration.TOP_LAYER_MODIFICATION);

            context.register(ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "clam_in_river")), modifier);
            // Oyster
            biomeHolder = context.lookup(Registries.BIOME).getOrThrow(BiomeTags.IS_OCEAN);
            configuredFeature = new ConfiguredFeature<>(FarmingRegistrator.OYSTER_FEATURE.get(), new CountConfiguration(7));
            placedFeature = new PlacedFeature(Holder.direct(configuredFeature), List.of(BiomeFilter.biome(), InSquarePlacement.spread(), RarityFilter.onAverageOnceEvery(10), HeightmapPlacement.onHeightmap(Heightmap.Types.OCEAN_FLOOR_WG)));

            modifier = new BiomeModifiers.AddFeaturesBiomeModifier(biomeHolder, HolderSet.direct(Holder.direct(placedFeature)), GenerationStep.Decoration.TOP_LAYER_MODIFICATION);

            context.register(ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "oyster_in_ocean")), modifier);
            // Mussel
            biomeHolder = context.lookup(Registries.BIOME).getOrThrow(BiomeTags.IS_BEACH);
            configuredFeature = new ConfiguredFeature<>(FarmingRegistrator.MUSSEL_FEATURE.get(), new CountConfiguration(10));
            placedFeature = new PlacedFeature(Holder.direct(configuredFeature), List.of(BiomeFilter.biome(), InSquarePlacement.spread(), RarityFilter.onAverageOnceEvery(30), HeightmapPlacement.onHeightmap(Heightmap.Types.OCEAN_FLOOR_WG)));

            modifier = new BiomeModifiers.AddFeaturesBiomeModifier(biomeHolder, HolderSet.direct(Holder.direct(placedFeature)), GenerationStep.Decoration.TOP_LAYER_MODIFICATION);

            context.register(ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "mussel_in_beach")), modifier);
        });
        return builder;
    }

//    static class SetBuilder extends RegistrySetBuilder
//    {
//        @Override
//        public HolderLookup.Provider build(RegistryAccess registryAccess) {
//            RegistrySetBuilder.BuildState state = this.createState(registryAccess);
//            Map<ResourceKey<? extends Registry<?>>, RegistryContents<?>> map = new HashMap<>();
//            state.registries().forEach((registryContents) -> map.put(registryContents.key(), registryContents));
//            this.entries.stream().map((RegistryStub<?> stub) -> stub.collectChanges(state)).forEach((contents) -> map.put(contents.key(), contents));
//            Stream<HolderLookup.RegistryLookup<?>> stream = registryAccess.registries().map((entry) -> entry.value().asLookup());
//            HolderLookup.Provider holderLookupProvider = HolderLookup.Provider.create(Stream.concat(stream, map.values().stream().map(RegistrySetBuilder.RegistryContents::buildAsLookup).peek(state::addOwner)));
//            state.fillMissingHolders(provider);
//            // don't validate missing holder values
//            state.throwOnError();
//            return holderLookupProvider;
//        }
//    }
}
