package cy.jdkdigital.productivefarming.datagen;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.common.block.*;
import cy.jdkdigital.productivefarming.datagen.model.RenderTypedModelTemplate;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import cy.jdkdigital.productivefarming.util.CropConfig;
import cy.jdkdigital.productivefarming.util.FishConfig;
import cy.jdkdigital.productivefarming.util.FlowerConfig;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.models.blockstates.*;
import net.minecraft.data.models.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ModelProvider implements DataProvider
{
    protected final PackOutput packOutput;

    protected final Map<ResourceLocation, Supplier<JsonElement>> models = new HashMap<>();

    public ModelProvider(PackOutput packOutput) {
        this.packOutput = packOutput;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        Map<Block, BlockStateGenerator> blockModels = Maps.newHashMap();
        Consumer<BlockStateGenerator> blockStateOutput = (blockStateGenerator) -> {
            Block block = blockStateGenerator.getBlock();
            BlockStateGenerator blockstategenerator = blockModels.put(block, blockStateGenerator);
            if (blockstategenerator != null) {
                throw new IllegalStateException("Duplicate blockstate definition for " + block);
            }
        };
        Map<ResourceLocation, Supplier<JsonElement>> itemModels = Maps.newHashMap();
        BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput = (resourceLocation, elementSupplier) -> {
            Supplier<JsonElement> supplier = itemModels.put(resourceLocation, elementSupplier);
            if (supplier != null) {
                throw new IllegalStateException("Duplicate model definition for " + resourceLocation);
            }
        };

        ModelGenerator generator = new ModelGenerator();
        try {
            generator.registerStatesAndModels(blockStateOutput, modelOutput);
        } catch (Exception e) {
            ProductiveFarming.LOGGER.error("Error registering states and models", e);
        }

        PackOutput.PathProvider blockstatePathProvider = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates");
        PackOutput.PathProvider modelPathProvider = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models");

        for (CropConfig crop : FarmingRegistrator.BERRIES) {
            generateFlatItem(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name())), "item/fruits/", modelOutput);
        }

        for (CropConfig crop : FarmingRegistrator.HERBS) {
            generateFlatItem(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name())), "item/crops/", modelOutput);
        }

        for (CropConfig crop : FarmingRegistrator.CROPS) {
            generateFlatItem(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name())), "item/crops/", modelOutput);
            if (crop.hasSeed()) {
                generateFlatItem(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name() + "_seeds")), "item/seeds/", modelOutput);
            }
        }

        for (CropConfig crop : FarmingRegistrator.TRELLIS) {
            generateFlatItem(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name())), "item/crops/", modelOutput);
        }

        for (CropConfig crop : FarmingRegistrator.VERTICAL_TRELLIS) {
            generateFlatItem(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name())), "item/crops/", modelOutput);
        }

        for (CropConfig crop : FarmingRegistrator.VINES) {
            generateFlatItem(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name())), "item/crops/", modelOutput);
        }

        for (CropConfig crop : FarmingRegistrator.STEMS) {
            addBlockItemParentModel(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name())), "crops/", "", itemModels);
            generateFlatItem(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name() + "_slice")), "item/fruit/", modelOutput);
            if (crop.hasSeed()) {
                generateFlatItem(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name() + "_seeds")), "item/seeds/", modelOutput);
            }
        }

        for (FishConfig fish : FarmingRegistrator.FISHIES) {
            if (fish.hasBlock()) {
                generateFlatItem(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, fish.name())), "item/fish/", modelOutput);
            } else {
                generateFlatItem(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "raw_" + fish.name())), "item/fish/", modelOutput);
            }
            if (fish.entitySupplier() != null) {
                // TODO Spawn egg item model
            }
            generateFlatItem(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "cooked_" + fish.name())), "item/fish/", modelOutput);
        }

        FarmingRegistrator.CRATED_CROPS.forEach(crate -> {
            addBlockItemParentModel(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crate.getPath() + "_crate")), "crates/", "", itemModels);
        });
        FarmingRegistrator.SEED_BAGS.forEach(seedName -> {
            addItemParentModel(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, seedName.getPath() + "_bag")), ResourceLocation.withDefaultNamespace("builtin/entity"), "", "", itemModels);
//            ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, seedName.getPath() + "_bag"))), getFlatItemTextureMap(new ResourceLocation("item/bundle_filled"), "", ""), modelOutput);
        });

        for (FlowerConfig flower : FarmingRegistrator.FLOWERS) {
            var block = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, flower.name()));
            if (block instanceof ColorfulTallFlowerBlock) {
                generateFlowerItem(block.asItem(), "block/flowers/", "/upper", modelOutput);
            } else {
                generateFlowerItem(block.asItem(), "block/flowers/", "/" + flower.name(), modelOutput);
            }
        }

        generateFlatItem(FarmingRegistrator.POLLEN.get(), "item/", modelOutput);

        generateFlatItem(FarmingRegistrator.DRIED_LUFFA.get(), "item/materials/", modelOutput);
        generateFlatItem(FarmingRegistrator.DRIED_TOBACCO.get(), "item/materials/", modelOutput);
        generateFlatItem(FarmingRegistrator.CORN_COB_PIPE.get(), "item/", modelOutput);

        addBlockItemParentModel(FarmingRegistrator.FEEDING_TROUGH.get(), "", "/empty", itemModels);
        addBlockItemParentModel(FarmingRegistrator.WATERING_TROUGH.get(), "", "/empty", itemModels);

        List<CompletableFuture<?>> output = new ArrayList<>();
        blockModels.forEach((block, supplier) -> {
            output.add(DataProvider.saveStable(cache, supplier.get(), blockstatePathProvider.json(BuiltInRegistries.BLOCK.getKey(block))));
        });
        itemModels.forEach((rLoc, supplier) -> {
            output.add(DataProvider.saveStable(cache, supplier.get(), modelPathProvider.json(rLoc)));
        });

        return CompletableFuture.allOf(output.toArray(CompletableFuture[]::new));
    }

    private void generateFlowerItem(Item item, String prefix, String suffix, BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput) {
        ModelTemplates.TWO_LAYERED_ITEM.create(ModelLocationUtils.getModelLocation(item), getDoubleFlatItemTextureMap(item, prefix, suffix), modelOutput);
    }

    private void generateFlatItem(Item item, String prefix, BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput) {
        ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(item), getFlatItemTextureMap(item, prefix), modelOutput);
    }

    private static TextureMapping getFlatItemTextureMap(Item item, String prefix) {
        return getFlatItemTextureMap(item, prefix, "");
    }

    private static TextureMapping getFlatItemTextureMap(Item item, String prefix, String suffix) {
        return getFlatItemTextureMap(BuiltInRegistries.ITEM.getKey(item), prefix, suffix);
    }

    private static TextureMapping getFlatItemTextureMap(ResourceLocation resourceLocation, String prefix, String suffix) {
        return (new TextureMapping()).put(TextureSlot.LAYER0, resourceLocation.withPrefix(prefix).withSuffix(suffix));
    }

    private static TextureMapping getDoubleFlatItemTextureMap(Item item, String prefix, String suffix) {
        return getDoubleFlatItemTextureMap(BuiltInRegistries.ITEM.getKey(item), prefix, suffix);
    }

    private static TextureMapping getDoubleFlatItemTextureMap(ResourceLocation resourceLocation, String prefix, String suffix) {
        return (new TextureMapping())
                .put(TextureSlot.LAYER0, resourceLocation.withPrefix(prefix).withSuffix(suffix))
                .put(TextureSlot.LAYER1, resourceLocation.withPrefix(prefix).withSuffix(suffix + "_flower"));
    }

    private void addItemModel(Item item, Supplier<JsonElement> supplier, Map<ResourceLocation, Supplier<JsonElement>> itemModels) {
        if (item != null) {
            ResourceLocation resourcelocation = ModelLocationUtils.getModelLocation(item);
            if (!itemModels.containsKey(resourcelocation)) {
                itemModels.put(resourcelocation, supplier);
            }
        }
    }

    private void addBlockItemModel(Block block, String base, Map<ResourceLocation, Supplier<JsonElement>> itemModels) {
        Item item = Item.BY_BLOCK.get(block);
        if (item != null) {
            addItemModel(item, new DelegatedModel(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "block/" + base)), itemModels);
        }
    }

    private void addBlockItemParentModel(Block block, String prefix, String suffix, Map<ResourceLocation, Supplier<JsonElement>> itemModels) {
        Item item = Item.BY_BLOCK.get(block);
        if (item != null) {
            var rl = BuiltInRegistries.BLOCK.getKey(block);
            addItemParentModel(item, rl, "block/" + prefix, suffix, itemModels);
        }
    }

    private void addItemParentModel(Item item, ResourceLocation rl, String prefix, String suffix, Map<ResourceLocation, Supplier<JsonElement>> itemModels) {
        addItemModel(item, new DelegatedModel(ResourceLocation.fromNamespaceAndPath(rl.getNamespace(), prefix + rl.getPath() + suffix)), itemModels);
    }

    @Override
    public String getName() {
        return "Productive Farming Blockstate and Model generator";
    }

    static class ModelGenerator
    {
        Consumer<BlockStateGenerator> blockStateOutput;
        BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput;

        protected void registerStatesAndModels(Consumer<BlockStateGenerator> blockStateOutput, BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput) {
            this.blockStateOutput = blockStateOutput;
            this.modelOutput = modelOutput;

            var NUTRIENT_WATER_BLOCK = BuiltInRegistries.BLOCK.get(FarmingRegistrator.NUTRIENT_WATER.getId());
            this.blockStateOutput.accept(createSimpleBlock(NUTRIENT_WATER_BLOCK, ModelTemplates.CUBE_ALL.create(NUTRIENT_WATER_BLOCK, TextureMapping.cube(ResourceLocation.withDefaultNamespace("block/water_still")), modelOutput)));

            Stream.concat(FarmingRegistrator.CROPS.stream(), FarmingRegistrator.VANILLA_CROPS.stream()).forEach(crop -> {
                var block = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name()));
                switch (block) {
                    case GrainCropBlock grainCropBlock -> createCropPlant(grainCropBlock, "crops/");
                    case DoubleGrainCropBlock doubleGrainCropBlock -> createDoubleCropPlant(doubleGrainCropBlock, "crops/");
                    case DoubleCropBlock doubleCropBlock -> createDoubleCrossPlant(doubleCropBlock, "crops/");
                    default -> createCrossPlant(block, "crops/");
                }
            });
            FarmingRegistrator.TRELLIS.forEach(crop -> {
                var leafBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name() + "_leaves"));
                var stemBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name()));
                var attachedStemBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "attached_" + crop.name() + "_stem"));
                createFenceGrowingPlantBlock(stemBlock, attachedStemBlock, leafBlock, "crops/", "trellis/");
            });
            FarmingRegistrator.VERTICAL_TRELLIS.forEach(crop -> {
                var leafBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name()));
                createFenceGrowingPlantLeafBlock(leafBlock, "trellis/", verticalTrellisLeaves);
            });
            FarmingRegistrator.VINES.forEach(crop -> {
                var leafBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name() + "_leaves"));
                var stemBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name()));
                var attachedStemBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "attached_" + crop.name() + "_stem"));
                createFenceGrowingPlantBlock(stemBlock, attachedStemBlock, leafBlock, "crops/", "vines/");
            });
            FarmingRegistrator.STEMS.forEach(crop -> {
                var fruitBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name()));
                var stemBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name() + "_stem"));
                var attachedStemBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "attached_" + crop.name() + "_stem"));
                createStemBlock(stemBlock, attachedStemBlock, fruitBlock, "crops/");
            });
            FarmingRegistrator.HERBS.forEach(crop -> {
                createCrossPlant(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name())), "herbs/");
            });
            FarmingRegistrator.BERRIES.forEach(crop -> {
                createCrossPlant(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name())), "bushes/");
            });
            FarmingRegistrator.FISHIES.forEach(fish -> {
                if (fish.hasBlock()) {
                    var block = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, fish.name()));
                    if (block instanceof ClamBlock clamBlock) {
                        createClam(clamBlock);
                    }
                }
            });
            FarmingRegistrator.CRATED_CROPS.forEach(crate -> {
                createCrate(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crate.getPath() + "_crate")));
            });
            FarmingRegistrator.FLOWERS.forEach(flower -> {
                var block = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, flower.name()));
                if (block instanceof ColorfulTallFlowerBlock) {
                    createTallCrossFlower(block, "flowers/", flower.name());
                } else {
                    createCrossFlower(block, "flowers/", flower.name());
                }
            });

            createFeedingTrough(FarmingRegistrator.FEEDING_TROUGH.get());
            createFeedingTrough(FarmingRegistrator.WATERING_TROUGH.get());

            createAttachedMushroom(FarmingRegistrator.BROWN_MUSHROOM_GROWTH.get());
            createAttachedMushroom(FarmingRegistrator.RED_MUSHROOM_GROWTH.get());
            createAttachedMushroom(FarmingRegistrator.CRIMSON_FUNGUS_GROWTH.get());
            createAttachedMushroom(FarmingRegistrator.WARPED_FUNGUS_GROWTH.get());
        }

        static ModelTemplate verticalTrellisLeaves = new ModelTemplate(Optional.of(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "block/vertical_trellis_leaves")), Optional.empty(), TextureSlot.ALL);
        static ModelTemplate vineLeaves = new ModelTemplate(Optional.of(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "block/inset_leaves")), Optional.empty(), TextureSlot.ALL);
        static ModelTemplate attachedFencedStemModel = new ModelTemplate(Optional.of(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "block/fenced_stem_attached")), Optional.empty(), TextureSlot.STEM);

        private void createFenceGrowingPlantBlock(Block stem, Block attachedStem, Block leafBlock, String prefix, String type) {
            IntegerProperty prop = stem instanceof IAgeableCropBlock cropBlock ? cropBlock.getAgeProperty() : BlockStateProperties.AGE_3;
            this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(stem).with(PropertyDispatch.property(prop).generate((age) -> {
                return Variant.variant().with(VariantProperties.MODEL, createSuffixedStemVariant(stem, prefix, "_stage_" + age, new ModelTemplate(Optional.of(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "block/fenced_stem_full")), Optional.empty(), TextureSlot.STEM), type, "stage_" + age));
            })));

            this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(attachedStem, Variant.variant().with(VariantProperties.MODEL, createSuffixedStemVariant(attachedStem, prefix, "", attachedFencedStemModel, type, "attached"))));

            createFenceGrowingPlantLeafBlock(leafBlock, type, vineLeaves);
        }

        private void createFenceGrowingPlantLeafBlock(Block leafBlock, String type, ModelTemplate model) {
            IntegerProperty prop = leafBlock instanceof IAgeableCropBlock cropBlock ? cropBlock.getAgeProperty() : BlockStateProperties.AGE_3;
            this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(leafBlock).with(PropertyDispatch.property(prop).generate(age -> {
                return Variant.variant().with(
                        VariantProperties.MODEL, model.create(TextureMapping.getBlockTexture(leafBlock).withPath(p -> "block/" + type + p.replace("block/", "") + "_" + age),
                                (new TextureMapping()).put(TextureSlot.ALL, TextureMapping.getBlockTexture(leafBlock).withPath(p -> "block/" + type + p.replace("block/", "").replace("_leaves", "") + "/stage_" + age)), modelOutput)
                );
            })));
        }

        private void createStemBlock(Block stem, Block attachedStem, Block fruitBlock, String prefix) {
            // Stem fruit block
            this.blockStateOutput.accept(createSimpleBlock(fruitBlock,
                    TexturedModel.COLUMN.get(fruitBlock).getTemplate().create(
                            ModelLocationUtils.getModelLocation(fruitBlock).withPath(p -> p.replace("block/", "block/" + prefix)),
                            TextureMapping.column(
                                    TextureMapping.getBlockTexture(fruitBlock, "_side").withPath(p -> p.replace("block/", "block/" + prefix)),
                                    TextureMapping.getBlockTexture(fruitBlock, "_top").withPath(p -> p.replace("block/", "block/" + prefix))
                            ),
                            this.modelOutput
                    )
            ));

            var stemTexture = TextureMapping.getBlockTexture(stem).withPath(p -> p.replace("block/", "block/" + prefix));
            var attachedStemTexture = TextureMapping.getBlockTexture(attachedStem).withPath(p -> p.replace("block/", "block/" + prefix));
            TextureMapping stemTextureMap = TextureMapping.singleSlot(TextureSlot.STEM, stemTexture);
            TextureMapping attachedStemTextureMap = new TextureMapping().put(TextureSlot.STEM, stemTexture).put(TextureSlot.UPPER_STEM, attachedStemTexture);

            ResourceLocation attachedStemModel = RenderTypedModelTemplate.ATTACHED_STEM.create(ModelLocationUtils.getModelLocation(attachedStem).withPath(p -> p.replace("block/", "block/" + prefix)), attachedStemTextureMap, this.modelOutput);
            this.blockStateOutput.accept(
                    MultiVariantGenerator.multiVariant(attachedStem, Variant.variant().with(VariantProperties.MODEL, attachedStemModel))
                            .with(
                                    PropertyDispatch.property(BlockStateProperties.HORIZONTAL_FACING)
                                            .select(Direction.WEST, Variant.variant())
                                            .select(Direction.SOUTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                                            .select(Direction.NORTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                                            .select(Direction.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                            )
            );
            this.blockStateOutput.accept(
                    MultiVariantGenerator.multiVariant(stem)
                            .with(
                                    PropertyDispatch.property(BlockStateProperties.AGE_7)
                                            .generate(age -> Variant.variant().with(VariantProperties.MODEL, RenderTypedModelTemplate.STEMS[age].create(ModelLocationUtils.getModelLocation(stem).withPath(p -> p.replace("block/", "block/" + prefix) + "_stage" + age), stemTextureMap, this.modelOutput)))
                            )
            );
        }

        static ModelTemplate crop = new ModelTemplate(Optional.of(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "block/crop")), Optional.empty(), TextureSlot.CROP);
        private void createCropPlant(Block block, String prefix) {
            IntegerProperty prop = block instanceof IAgeableCropBlock cropBlock ? cropBlock.getAgeProperty() : BlockStateProperties.AGE_3;
            this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block).with(PropertyDispatch.property(prop).generate((age) -> {
                return Variant.variant().with(VariantProperties.MODEL, createSuffixedVariant(block, prefix, "/stage_" + age, crop, TextureMapping::crop));
            })));
        }
        private void createDoubleCropPlant(Block block, String prefix) {
            IntegerProperty prop = block instanceof ProductiveCropBlock cropBlock ? cropBlock.getAgeProperty() : BlockStateProperties.AGE_3;
            this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block).with(PropertyDispatch.properties(prop, BlockStateProperties.DOUBLE_BLOCK_HALF).generate((age, part) -> {
                return Variant.variant().with(VariantProperties.MODEL, createSuffixedVariant(block, prefix, "/" + part.getSerializedName() + "/stage_" + age, crop, TextureMapping::crop));
            })));
        }

        static ModelTemplate cross = new ModelTemplate(Optional.of(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "block/cross")), Optional.empty(), TextureSlot.CROSS);
        static ModelTemplate crossOverlay = new ModelTemplate(Optional.of(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "block/cross_overlay")), Optional.empty(), TextureSlot.CROSS, TextureSlot.EDGE);

        private void createCrossPlant(Block block, String prefix) {
            IntegerProperty prop = block instanceof ProductiveCropBlock cropBlock ? cropBlock.getAgeProperty() : BlockStateProperties.AGE_3;
            this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block).with(PropertyDispatch.property(prop).generate((age) -> {
                return Variant.variant().with(VariantProperties.MODEL, createSuffixedVariant(block, prefix, "/stage_" + age, cross, TextureMapping::cross));
            })));
        }
        private void createDoubleCrossPlant(Block block, String prefix) {
            IntegerProperty prop = block instanceof ProductiveCropBlock cropBlock ? cropBlock.getAgeProperty() : BlockStateProperties.AGE_3;
            this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block).with(PropertyDispatch.properties(prop, BlockStateProperties.DOUBLE_BLOCK_HALF).generate((age, part) -> {
                return Variant.variant().with(VariantProperties.MODEL, createSuffixedVariant(block, prefix, "/" + part.getSerializedName() + "/stage_" + age, cross, TextureMapping::cross));
            })));
        }

        private void createCrossFlower(Block block, String prefix, String suffix) {
            this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block, Variant.variant().with(VariantProperties.MODEL, createSuffixedVariant(block, prefix, "/" + suffix, crossOverlay,
                    (resourceLocation) -> (new TextureMapping())
                    .put(TextureSlot.CROSS, resourceLocation.withPath(p -> p))
                    .put(TextureSlot.EDGE, resourceLocation.withPath(p -> p + "_flower"))))));
        }
        private void createTallCrossFlower(Block block, String prefix, String suffix) {
            this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block).with(PropertyDispatch.property(BlockStateProperties.DOUBLE_BLOCK_HALF).generate((part) -> {
                return Variant.variant().with(VariantProperties.MODEL, createSuffixedVariant(block, prefix, "/" + part.getSerializedName(), crossOverlay,
                        (resourceLocation) -> (new TextureMapping())
                        .put(TextureSlot.CROSS, resourceLocation.withPath(p -> p))
                        .put(TextureSlot.EDGE, resourceLocation.withPath(p -> p + "_flower"))));
            })));
        }
        private static TextureMapping crossOverlay(Block block) {
            return (new TextureMapping())
                    .put(TextureSlot.CROSS, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "block/crate/top"))
                    .put(TextureSlot.EDGE, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "block/crate/side"));
        }

        private ResourceLocation createSuffixedVariant(Block pBlock, String pPrefix, String pSuffix, ModelTemplate pModelTemplate, Function<ResourceLocation, TextureMapping> pTextureMappingGetter) {
            ResourceLocation resourceLocation = BuiltInRegistries.BLOCK.getKey(pBlock).withPath((path) -> "block/" + pPrefix + path + pSuffix);
            return pModelTemplate.create(resourceLocation, pTextureMappingGetter.apply(resourceLocation), this.modelOutput);
        }

        private ResourceLocation createSuffixedStemVariant(Block pBlock, String pPrefix, String pSuffix, ModelTemplate pModelTemplate, String type, String textureSuffix) {
            ResourceLocation resourceLocation = BuiltInRegistries.BLOCK.getKey(pBlock).withPath((path) -> "block/" + pPrefix + path + pSuffix);
            return pModelTemplate.create(resourceLocation, TextureMapping.singleSlot(TextureSlot.STEM, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "block/" + type + "stem/" + textureSuffix)), this.modelOutput);
        }

        // TODO move to lib
        static ModelTemplate crateModel = new ModelTemplate(Optional.of(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "block/base_crate")), Optional.empty(), TextureSlot.BOTTOM, TextureSlot.SIDE, TextureSlot.TOP, TextureSlot.CROP);
        private void createCrate(Block block) {
            ResourceLocation top = BuiltInRegistries.BLOCK.getKey(block).withPath((p) -> "block/crate/" + p);
            var textureMapping = (new TextureMapping())
                    .put(TextureSlot.TOP, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "block/crate/top"))
                    .put(TextureSlot.SIDE, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "block/crate/side"))
                    .put(TextureSlot.BOTTOM, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "block/crate/bottom"))
                    .put(TextureSlot.CROP, top);

            this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block, Variant.variant().with(VariantProperties.MODEL, crateModel.create(BuiltInRegistries.BLOCK.getKey(block).withPath((p) -> "block/crates/" + p), textureMapping, this.modelOutput))));
        }

        private void createClam(Block block) {
            ResourceLocation resourcelocation = BuiltInRegistries.BLOCK.getKey(block).withPath((path) -> "block/fish/" + path);
            this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block).with(PropertyDispatch.property(BlockStateProperties.OPEN).generate((open) -> {
                return Variant.variant().with(VariantProperties.MODEL, resourcelocation.withPath(path -> open ? path + "_open" : path));
            })).with(createFacingDispatch()));
        }

        private static PropertyDispatch createFacingDispatch() {
            return PropertyDispatch.properties(BlockStateProperties.ATTACH_FACE, BlockStateProperties.HORIZONTAL_FACING)
                    .select(AttachFace.FLOOR, Direction.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                    .select(AttachFace.FLOOR, Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                    .select(AttachFace.FLOOR, Direction.SOUTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                    .select(AttachFace.FLOOR, Direction.NORTH, Variant.variant())
                    .select(AttachFace.WALL, Direction.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90))
                    .select(AttachFace.WALL, Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90))
                    .select(AttachFace.WALL, Direction.SOUTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90))
                    .select(AttachFace.WALL, Direction.NORTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90))
                    .select(AttachFace.CEILING, Direction.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180))
                    .select(AttachFace.CEILING, Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180))
                    .select(AttachFace.CEILING, Direction.SOUTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180))
                    .select(AttachFace.CEILING, Direction.NORTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180));
        }

        private void createFeedingTrough(Block block) {
            this.blockStateOutput.accept(
                    MultiPartGenerator.multiPart(block)
                            .with(Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(block, "/empty")))
                            .with(Condition.condition().term(FeedingTroughBlock.LEVEL, 0), Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(block, "/empty")))
                            .with(Condition.condition().term(FeedingTroughBlock.LEVEL, 1), Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(block, "/half")))
                            .with(Condition.condition().term(FeedingTroughBlock.LEVEL, 2), Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(block, "/full")))
            );
        }

        void createAttachedMushroom(Block block) {
            ResourceLocation mushroomPlace = BuiltInRegistries.BLOCK.getKey(block).withPath((p) -> "block/mushroom_growth/" + p.replace("_growth", ""));

            this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block)
                    .with(PropertyDispatch.property(BlockStateProperties.AGE_4).generate((age) -> {
                        ResourceLocation modelLocation = RenderTypedModelTemplate.CROP.create(mushroomPlace.withPath(p -> p + "_stage_" + age), TextureMapping.crop(TextureMapping.getBlockTexture(block)), this.modelOutput);
                        return Variant.variant().with(VariantProperties.MODEL, modelLocation);
                    }))
                    .with(PropertyDispatch.property(BlockStateProperties.HORIZONTAL_FACING).
                            select(Direction.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)).
                            select(Direction.SOUTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)).
                            select(Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)).
                            select(Direction.NORTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90))
            ));
        }

        void createHorizontalFacing(Block block, ResourceLocation modelLocation) {
            this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block, Variant.variant().with(VariantProperties.MODEL, modelLocation)).with(
                    PropertyDispatch.property(BlockStateProperties.HORIZONTAL_FACING).
                            select(Direction.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.MODEL, modelLocation)).
                            select(Direction.SOUTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).
                            select(Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).
                            select(Direction.NORTH, Variant.variant())
            ));
        }

        static MultiVariantGenerator createSimpleBlock(Block block, ResourceLocation modelLocation) {
            return MultiVariantGenerator.multiVariant(block, Variant.variant().with(VariantProperties.MODEL, modelLocation));
        }
    }
}
