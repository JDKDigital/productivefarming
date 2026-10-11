package cy.jdkdigital.productivefarming.datagen;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import cy.jdkdigital.productivefarming.registry.ModTags;
import cy.jdkdigital.productivefarming.registry.ModTrades;
import cy.jdkdigital.productivefarming.util.FarmUtil;
import cy.jdkdigital.productivefarming.util.FlowerConfig;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TallFlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.DualNoiseProvider;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class FeatureProvider extends DatapackBuiltinEntriesProvider
{
    public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWERS_CONFIGURED = ResourceKey.create(
            Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "flowers"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> DOUBLE_FLOWERS_CONFIGURED = ResourceKey.create(
            Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "double_flowers"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> THICC_FLOWERS_CONFIGURED = ResourceKey.create(
            Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "thicc_flowers"));

    public static final ResourceKey<PlacedFeature> FLOWERS_PLACED = ResourceKey.create(
            Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "flowers"));
    public static final ResourceKey<PlacedFeature> DOUBLE_FLOWERS_PLACED = ResourceKey.create(
            Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "double_flowers"));
    public static final ResourceKey<PlacedFeature> THICC_FLOWERS_PLACED = ResourceKey.create(
            Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "thicc_flowers"));

    public static final ResourceKey<BiomeModifier> FLOWERS_MODIFIER = ResourceKey.create(
            NeoForgeRegistries.Keys.BIOME_MODIFIERS, Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "flowers"));
    public static final ResourceKey<BiomeModifier> DOUBLE_FLOWERS_MODIFIER = ResourceKey.create(
            NeoForgeRegistries.Keys.BIOME_MODIFIERS, Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "double_flowers"));
    public static final ResourceKey<BiomeModifier> THICC_FLOWERS_MODIFIER = ResourceKey.create(
            NeoForgeRegistries.Keys.BIOME_MODIFIERS, Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "thicc_flowers"));

    public FeatureProvider(PackOutput output, RegistrySetBuilder builder, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, builder, Set.of(ProductiveFarming.MODID));
    }

    public static RegistrySetBuilder getBuilder() {
        var builder = new RegistrySetBuilder();

        builder.add(Registries.CONFIGURED_FEATURE, FeatureProvider::bootstrapConfiguredFeatures);
        builder.add(Registries.PLACED_FEATURE, FeatureProvider::bootstrapPlacedFeatures);
        builder.add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, FeatureProvider::bootstrapBiomeModifiers);
        builder.add(Registries.VILLAGER_TRADE, ModTrades::bootstrapTrades);

        return builder;
    }

    private static void bootstrapConfiguredFeatures(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        DualNoiseProvider provider = new DualNoiseProvider(
                new InclusiveRange<>(1, 3),
                new NormalNoise.NoiseParameters(-10, 1.0),
                1.0F,
                2345L,
                new NormalNoise.NoiseParameters(-3, 1.0),
                1.0F,
                smallFlowerStates());
        context.register(FLOWERS_CONFIGURED, new ConfiguredFeature<>(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(provider)));

        DualNoiseProvider doubleProvider = new DualNoiseProvider(
                new InclusiveRange<>(1, 3),
                new NormalNoise.NoiseParameters(-10, 1.0),
                1.0F,
                2345L,
                new NormalNoise.NoiseParameters(-3, 1.0),
                1.0F,
                doubleFlowerStates());
        context.register(DOUBLE_FLOWERS_CONFIGURED, new ConfiguredFeature<>(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(doubleProvider)));

        DualNoiseProvider thiccProvider = new DualNoiseProvider(
                new InclusiveRange<>(1, 3),
                new NormalNoise.NoiseParameters(-10, 1.0),
                1.0F,
                2345L,
                new NormalNoise.NoiseParameters(-3, 1.0),
                1.0F,
                thiccFlowerStates());
        context.register(THICC_FLOWERS_CONFIGURED, new ConfiguredFeature<>(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(thiccProvider)));
    }

    private static void bootstrapPlacedFeatures(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configured = context.lookup(Registries.CONFIGURED_FEATURE);
        List<PlacementModifier> placement = List.of(
                NoiseThresholdCountPlacement.of(-0.8, 2, 8),
                RarityFilter.onAverageOnceEvery(24),
                InSquarePlacement.spread(),
                PlacementUtils.HEIGHTMAP,
                BiomeFilter.biome(),
                CountPlacement.of(32),
                RandomOffsetPlacement.ofTriangle(5, 2),
                BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE)
        );
        context.register(FLOWERS_PLACED, new PlacedFeature(configured.getOrThrow(FLOWERS_CONFIGURED), placement));

        List<PlacementModifier> DoublePlacement = List.of(
                RarityFilter.onAverageOnceEvery(30),
                InSquarePlacement.spread(),
                PlacementUtils.HEIGHTMAP,
                BiomeFilter.biome(),
                CountPlacement.of(24),
                RandomOffsetPlacement.ofTriangle(5, 1),
                BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE)
        );
        context.register(DOUBLE_FLOWERS_PLACED, new PlacedFeature(configured.getOrThrow(DOUBLE_FLOWERS_CONFIGURED), DoublePlacement));

        List<PlacementModifier> thiccPlacement = List.of(
                RarityFilter.onAverageOnceEvery(48),
                InSquarePlacement.spread(),
                PlacementUtils.HEIGHTMAP,
                BiomeFilter.biome(),
                CountPlacement.of(8),
                RandomOffsetPlacement.ofTriangle(3, 1),
                BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE)
        );
        context.register(THICC_FLOWERS_PLACED, new PlacedFeature(configured.getOrThrow(THICC_FLOWERS_CONFIGURED), thiccPlacement));
    }

    private static void bootstrapBiomeModifiers(BootstrapContext<BiomeModifier> context) {
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        HolderGetter<PlacedFeature> placed = context.lookup(Registries.PLACED_FEATURE);
        context.register(FLOWERS_MODIFIER, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(ModTags.Biomes.HAS_FLOWERS),
                HolderSet.direct(placed.getOrThrow(FLOWERS_PLACED)),
                GenerationStep.Decoration.VEGETAL_DECORATION));

        context.register(DOUBLE_FLOWERS_MODIFIER, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(ModTags.Biomes.HAS_FLOWERS),
                HolderSet.direct(placed.getOrThrow(DOUBLE_FLOWERS_PLACED)),
                GenerationStep.Decoration.VEGETAL_DECORATION));

        context.register(THICC_FLOWERS_MODIFIER, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(ModTags.Biomes.HAS_FLOWERS),
                HolderSet.direct(placed.getOrThrow(THICC_FLOWERS_PLACED)),
                GenerationStep.Decoration.VEGETAL_DECORATION));
    }

    private static List<BlockState> smallFlowerStates() {
        List<BlockState> states = new ArrayList<>();
        FarmingRegistrator.FLOWERS.forEach(flowerConfig -> {
            if (!isVanilla(flowerConfig) && !flowerConfig.isDouble() && !flowerConfig.isThicc()) {
                Block block = BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, flowerConfig.name()));
                BlockState state = block.defaultBlockState();
                states.add(state);
            }
        });
        return states;
    }

    private static List<BlockState> doubleFlowerStates() {
        List<BlockState> states = new ArrayList<>();
        FarmingRegistrator.FLOWERS.forEach(flowerConfig -> {
            if (!isVanilla(flowerConfig) && flowerConfig.isDouble() && !flowerConfig.isThicc()) {
                Block block = BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, flowerConfig.name()));
                BlockState state = block.defaultBlockState();
                if (state.hasProperty(TallFlowerBlock.HALF)) {
                    state = state.setValue(TallFlowerBlock.HALF, DoubleBlockHalf.LOWER);
                }
                states.add(state);
            }
        });
        return states;
    }

    private static List<BlockState> thiccFlowerStates() {
        List<BlockState> states = new ArrayList<>();
        FarmingRegistrator.FLOWERS.forEach(flowerConfig -> {
            if (!isVanilla(flowerConfig) && flowerConfig.isThicc()) {
                Block block = BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, flowerConfig.name()));
                BlockState state = block.defaultBlockState();
                if (state.hasProperty(TallFlowerBlock.HALF)) {
                    state = state.setValue(TallFlowerBlock.HALF, DoubleBlockHalf.LOWER);
                }
                states.add(state);
            }
        });
        return states;
    }

    private static boolean isVanilla(FlowerConfig flowerConfig) {
        return flowerConfig.name().equals("productivefarming:tulip") || FarmUtil.VANILLA_FLOWER_COLORS.containsKey(Identifier.parse("minecraft:" + Identifier.parse(flowerConfig.name()).getPath()));
    }
}
