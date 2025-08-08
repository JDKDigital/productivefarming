package cy.jdkdigital.productivefarming.datagen;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.common.block.ColorfulVineBlock;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import cy.jdkdigital.productivefarming.registry.ModTags;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.DualNoiseProvider;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Arrays;
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

            var biomeHolder = context.lookup(Registries.BIOME).getOrThrow(ModTags.Biomes.HAS_FLOWERS);
            var configuredFeature = new ConfiguredFeature<>(Feature.FLOWER, new RandomPatchConfiguration(
                    64,
                    7,
                    3,
                    PlacementUtils.onlyWhenEmpty(
                            Feature.SIMPLE_BLOCK,
                            new SimpleBlockConfiguration(
                                    new DualNoiseProvider(
                                            new InclusiveRange<>(1, 3),
                                            new NormalNoise.NoiseParameters(-10, 1.0),
                                            1.0F,
                                            2345L,
                                            new NormalNoise.NoiseParameters(-3, 1.0),
                                            1.0F,
                                            Arrays.stream(FarmingRegistrator.getFlowers()).filter(block -> !(block instanceof ColorfulVineBlock)).map(Block::defaultBlockState).toList()
                                    )
                            )
                    )
            ));
            var placedFeature = new PlacedFeature(Holder.direct(configuredFeature), List.of(BiomeFilter.biome(), InSquarePlacement.spread(), RarityFilter.onAverageOnceEvery(32), HeightmapPlacement.onHeightmap(Heightmap.Types.MOTION_BLOCKING)));
            var modifier = new BiomeModifiers.AddFeaturesBiomeModifier(biomeHolder, HolderSet.direct(Holder.direct(placedFeature)), GenerationStep.Decoration.VEGETAL_DECORATION);
            context.register(ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "flowers")), modifier);

            // Clam
//            var biomeHolder = context.lookup(Registries.BIOME).getOrThrow(BiomeTags.IS_RIVER);
//            var configuredFeature = new ConfiguredFeature<>(FarmingRegistrator.CLAM_FEATURE.get(), new CountConfiguration(10));
//            var placedFeature = new PlacedFeature(Holder.direct(configuredFeature), List.of(BiomeFilter.biome(), InSquarePlacement.spread(), RarityFilter.onAverageOnceEvery(10), HeightmapPlacement.onHeightmap(Heightmap.Types.OCEAN_FLOOR_WG)));
//
//            var modifier = new BiomeModifiers.AddFeaturesBiomeModifier(biomeHolder, HolderSet.direct(Holder.direct(placedFeature)), GenerationStep.Decoration.TOP_LAYER_MODIFICATION);
//
//            context.register(ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "clam_in_river")), modifier);
//            // Oyster
//            biomeHolder = context.lookup(Registries.BIOME).getOrThrow(BiomeTags.IS_OCEAN);
//            configuredFeature = new ConfiguredFeature<>(FarmingRegistrator.OYSTER_FEATURE.get(), new CountConfiguration(7));
//            placedFeature = new PlacedFeature(Holder.direct(configuredFeature), List.of(BiomeFilter.biome(), InSquarePlacement.spread(), RarityFilter.onAverageOnceEvery(10), HeightmapPlacement.onHeightmap(Heightmap.Types.OCEAN_FLOOR_WG)));
//
//            modifier = new BiomeModifiers.AddFeaturesBiomeModifier(biomeHolder, HolderSet.direct(Holder.direct(placedFeature)), GenerationStep.Decoration.TOP_LAYER_MODIFICATION);
//
//            context.register(ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "oyster_in_ocean")), modifier);
//            // Mussel
//            biomeHolder = context.lookup(Registries.BIOME).getOrThrow(BiomeTags.IS_BEACH);
//            configuredFeature = new ConfiguredFeature<>(FarmingRegistrator.MUSSEL_FEATURE.get(), new CountConfiguration(10));
//            placedFeature = new PlacedFeature(Holder.direct(configuredFeature), List.of(BiomeFilter.biome(), InSquarePlacement.spread(), RarityFilter.onAverageOnceEvery(30), HeightmapPlacement.onHeightmap(Heightmap.Types.OCEAN_FLOOR_WG)));
//
//            modifier = new BiomeModifiers.AddFeaturesBiomeModifier(biomeHolder, HolderSet.direct(Holder.direct(placedFeature)), GenerationStep.Decoration.TOP_LAYER_MODIFICATION);
//
//            context.register(ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "mussel_in_beach")), modifier);
        });
        return builder;
    }
}
