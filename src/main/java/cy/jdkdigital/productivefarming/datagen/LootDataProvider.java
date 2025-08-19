package cy.jdkdigital.productivefarming.datagen;

import com.google.common.collect.Maps;
import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.common.block.ColorfulTallFlowerBlock;
import cy.jdkdigital.productivefarming.registry.FarmingDataComponents;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import cy.jdkdigital.productivefarming.util.CropConfig;
import cy.jdkdigital.productivefarming.util.FishConfig;
import cy.jdkdigital.productivefarming.util.FlowerConfig;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.*;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class LootDataProvider implements DataProvider
{
    private final PackOutput.PathProvider pathProvider;
    private final List<LootTableProvider.SubProviderEntry> subProviders;
    private final CompletableFuture<HolderLookup.Provider> registries;

    public LootDataProvider(PackOutput output, List<LootTableProvider.SubProviderEntry> providers, CompletableFuture<HolderLookup.Provider> registries) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "loot_table");
        this.subProviders = providers;
        this.registries = registries;
    }

    @Override
    public String getName() {
        return "Productive Trees Block Loot Table datagen";
    }

    @Override
    public CompletableFuture<?> run(CachedOutput pOutput) {
        return this.registries.thenCompose(provider -> this.run(pOutput, provider));
    }

    private CompletableFuture<?> run(CachedOutput pOutput, HolderLookup.Provider pProvider) {
        final Map<ResourceLocation, LootTable> map = Maps.newHashMap();
        this.subProviders.forEach((providerEntry) -> {
            providerEntry.provider().apply(pProvider).generate((resourceKey, builder) -> {
                builder.setRandomSequence(resourceKey.location());
                if (map.put(resourceKey.location(), builder.setParamSet(providerEntry.paramSet()).build()) != null) {
                    throw new IllegalStateException("Duplicate loot table " + resourceKey.location());
                }
            });
        });

        return CompletableFuture.allOf(map.entrySet().stream().map((entry) -> {
            return DataProvider.saveStable(pOutput, pProvider, LootTable.DIRECT_CODEC, entry.getValue(), this.pathProvider.json(entry.getKey()));
        }).toArray(CompletableFuture[]::new));
    }

    public static class LootProvider extends BlockLootSubProvider
    {
        private static final Map<Block, Function<Block, LootTable.Builder>> functionTable = new HashMap<>();

        private List<Block> knownBlocks = new ArrayList<>();

        public LootProvider(HolderLookup.Provider provider) {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
        }

        @Override
        protected void generate() {
            HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);

            dropSelf(FarmingRegistrator.FARM_CONTROLLER.get());
            dropSelf(FarmingRegistrator.FEEDING_TROUGH.get());
            dropSelf(FarmingRegistrator.WATERING_TROUGH.get());

            for (CropConfig crop : FarmingRegistrator.VANILLA_CROPS) {
                if (crop.hasSeed()) {
                    dropSeedCrop(crop);
                } else {
                    dropSeedlessCrop(crop);
                }
            }
            for (CropConfig crop : FarmingRegistrator.CROPS) {
                if (crop.hasSeed()) {
                    dropSeedCrop(crop);
                } else {
                    dropSeedlessCrop(crop);
                }
            }
            for (CropConfig crop : FarmingRegistrator.HERBS) {
                dropSeedlessCrop(crop);
            }
            for (CropConfig crop : FarmingRegistrator.BERRIES) {
                dropSeedlessCrop(crop);
            }
            for (CropConfig crop : FarmingRegistrator.TRELLIS) {
                if (crop.hasSeed()) {
                    dropSeedCrop(crop);
                } else {
                    dropSeedlessCrop(crop);
                }
                var seed = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name() + (crop.hasSeed() ? "_seeds" : "")));
                this.add(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name())), block -> this.createStemDrops(block, seed));
                this.add(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "attached_" + crop.name() + "_stem")), block -> this.createAttachedStemDrops(block, seed));
            }
            for (CropConfig crop : FarmingRegistrator.GRAPES) {
                dropSeedlessCrop(crop);
                var seed = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name() + (crop.hasSeed() ? "_seeds" : "")));
                this.add(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name())), block -> this.createStemDrops(block, seed));
                this.add(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "attached_" + crop.name() + "_stem")), block -> this.createAttachedVineStemDrops(block, seed));
            }
            for (CropConfig crop : FarmingRegistrator.STEMS) {
                this.add(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name() + "_stem")), block -> this.createStemDrops(block, BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name() + "_seeds"))));
                this.add(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "attached_" + crop.name() + "_stem")), block -> this.createAttachedStemDrops(block, BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name() + "_seeds"))));
                this.add(
                        BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name())),
                        block -> this.createSilkTouchDispatchTable(
                                block,
                                this.applyExplosionDecay(
                                        block,
                                        LootItem.lootTableItem(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name() + "_slice")))
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0F, 7.0F)))
                                                .apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))
                                                .apply(LimitCount.limitCount(IntRange.upperBound(9)))
                                )
                        )
                );
            }
            for (FishConfig fish : FarmingRegistrator.FISHIES) {
                if (fish.hasBlock()) {
                    dropSelf(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, fish.name())));
                }
            }
            for (FlowerConfig flower : FarmingRegistrator.FLOWERS) {
                createFlowerDrops(flower);
            }
            for (FlowerConfig flower : FarmingRegistrator.VINES) {
                createFlowerDrops(flower);
            }
        }

        @Override
        protected void add(Block block, LootTable.Builder builder) {
            super.add(block, builder);
            knownBlocks.add(block);
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return knownBlocks;
        }

        protected void add(Block block, Function<Block, LootTable.Builder> builderFunction) {
            this.add(block, builderFunction.apply(block));
        }

        protected void dropSeedCrop(CropConfig crop) {
            var block = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name()));
            if (block instanceof CropBlock cropBlock) {
                var cropItem = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name()));
                var seedItem = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name() + "_seeds"));
                if (cropItem.equals(Items.AIR)) {
                    cropItem = BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace(crop.name()));
                }if (seedItem.equals(Items.AIR)) {
                    seedItem = BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace(crop.name() + "_seeds"));
                }
                LootItemCondition.Builder builder = LootItemBlockStatePropertyCondition
                        .hasBlockStateProperties(cropBlock)
                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(cropBlock.getAgeProperty(), cropBlock.getMaxAge()));

                if (block.defaultBlockState().hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)) {
                    builder = LootItemBlockStatePropertyCondition
                            .hasBlockStateProperties(cropBlock)
                            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(cropBlock.getAgeProperty(), cropBlock.getMaxAge()).hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER));
                }

                this.add(cropBlock, this.createCropDrops(cropBlock, cropItem, seedItem, builder));
            }
        }

        protected void dropSeedlessCrop(CropConfig crop) {
            var block = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name()));
            if (block instanceof CropBlock cropBlock) {
                var cropItem = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name()));
                if (cropItem.equals(Items.AIR)) {
                    cropItem = BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace(crop.name()));
                }
                LootItemCondition.Builder builder = LootItemBlockStatePropertyCondition
                        .hasBlockStateProperties(cropBlock)
                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(cropBlock.getAgeProperty(), cropBlock.getMaxAge()));

                if (block.defaultBlockState().hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)) {
                    builder = LootItemBlockStatePropertyCondition
                            .hasBlockStateProperties(cropBlock)
                            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(cropBlock.getAgeProperty(), cropBlock.getMaxAge()).hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER));
                }

                this.add(cropBlock, this.createSeedlessCropDrops(cropBlock, cropItem, cropItem, builder));
            }
        }

        public void dropSelf(@NotNull Block block) {
            Function<Block, LootTable.Builder> func = functionTable.getOrDefault(block, LootProvider::genOptionalBlockDrop);
            this.add(block, func.apply(block));
        }

        protected static @NotNull LootTable.Builder createSelfDropDispatchTable(Block block, LootItemCondition.Builder conditions, LootPoolEntryContainer.Builder<?> alternative) {
            return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(block).when(conditions).otherwise(alternative)));
        }

        protected static LootTable.Builder genOptionalBlockDrop(Block block) {
            LootPoolEntryContainer.Builder<?> builder = LootItem.lootTableItem(block).when(ExplosionCondition.survivesExplosion());

            return LootTable.lootTable().withPool(
                    LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(builder));
        }

        public LootTable.Builder createAttachedVineStemDrops(Block block, Item item) {
            return LootTable.lootTable()
                    .withPool(
                            this.applyExplosionDecay(
                                    block,
                                    LootPool.lootPool()
                                            .setRolls(UniformGenerator.between(1.0F, 3.0F))
                                            .add(LootItem.lootTableItem(item).apply(SetItemCountFunction.setCount(BinomialDistributionGenerator.binomial(3, 0.53333336F))))
                            )
                    );
        }

        @Override
        protected LootTable.Builder createCropDrops(Block cropBlock, Item grownCropItem, Item seedsItem, LootItemCondition.Builder dropGrownCropCondition) {
            HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
            return this.applyExplosionDecay(
                    cropBlock,
                    LootTable.lootTable()
                            .withPool(
                                    LootPool.lootPool().add(
                                            LootItem.lootTableItem(grownCropItem).when(dropGrownCropCondition)
                                                    .otherwise(LootItem.lootTableItem(seedsItem).apply(cropComponents()))

                                    )
                            )
                            .withPool(
                                    LootPool.lootPool().when(dropGrownCropCondition)
                                            .add(
                                                    LootItem.lootTableItem(seedsItem)
                                                            .apply(ApplyBonusCount.addBonusBinomialDistributionCount(registrylookup.getOrThrow(Enchantments.FORTUNE), 0.5714286F, 3))
                                                            .apply(cropComponents())
                                            )
                            )
            );
        }

        protected LootTable.Builder createSeedlessCropDrops(Block cropBlock, Item grownCropItem, Item seedsItem, LootItemCondition.Builder dropGrownCropCondition) {
            HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
            return this.applyExplosionDecay(
                    cropBlock,
                    LootTable.lootTable()
                            .withPool(
                                    LootPool.lootPool().add(
                                            LootItem.lootTableItem(grownCropItem).apply(cropComponents()).when(dropGrownCropCondition)
                                                    .otherwise(LootItem.lootTableItem(seedsItem).apply(cropComponents()))

                                    )
                            )
                            .withPool(
                                    LootPool.lootPool()
                                            .when(dropGrownCropCondition)
                                            .add(
                                                    LootItem.lootTableItem(seedsItem)
                                                            .apply(ApplyBonusCount.addBonusBinomialDistributionCount(registrylookup.getOrThrow(Enchantments.FORTUNE), 0.5714286F, 3))
                                                            .apply(cropComponents())
                                            )
                            )
            );
        }

        private static LootItemFunction.Builder cropComponents() {
            return CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                    .include(FarmingDataComponents.GROWTH.get())
                    .include(FarmingDataComponents.YIELD.get())
                    .include(FarmingDataComponents.RESISTANCE.get())
                    .include(FarmingDataComponents.MUTABILITY.get());
        }

        private void createFlowerDrops(FlowerConfig flower) {
            var block = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, flower.name()));
            if (block instanceof ColorfulTallFlowerBlock) {
                // handled in code because double plants don't get components applied correctly, thanks Mojang
//                this.add(block, createSinglePropConditionTable(block, DoublePlantBlock.HALF, DoubleBlockHalf.LOWER));
            } else {
                LootPoolEntryContainer.Builder<?> builder = LootItem.lootTableItem(block)
                        .apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY).include(FarmingDataComponents.COLOR.get()))
                        .when(ExplosionCondition.survivesExplosion());

                this.add(block, LootTable.lootTable().withPool(
                        LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                                .add(builder)));
            }
        }

        protected <T extends Comparable<T> & StringRepresentable> LootTable.Builder createSinglePropConditionTable(Block block, Property<T> property, T value) {
            return LootTable.lootTable().withPool(this.applyExplosionCondition(block,
                    LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(block)
                            .apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                                    .include(FarmingDataComponents.COLOR.get()))
                            .when(
                                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(property, value))
                            )
                    )
                )
            );
        }
    }
}
