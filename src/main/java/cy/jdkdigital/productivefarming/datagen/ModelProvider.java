package cy.jdkdigital.productivefarming.datagen;

import com.mojang.math.Quadrant;
import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.common.block.*;
import cy.jdkdigital.productivefarming.client.render.item.SeedBagItemRenderer;
import cy.jdkdigital.productivefarming.datagen.model.RenderTypedModelTemplate;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import cy.jdkdigital.productivefarming.util.CropConfig;
import cy.jdkdigital.productivefarming.util.FishConfig;
import cy.jdkdigital.productivefarming.util.FlowerConfig;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.ConditionBuilder;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.data.models.model.ItemModelUtils;
import cy.jdkdigital.productivefarming.client.color.FlowerItemTintSource;
import cy.jdkdigital.productivefarming.client.color.PollenItemTintSource;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.item.DynamicFluidContainerModel;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

public class ModelProvider extends net.minecraft.client.data.models.ModelProvider
{
    public ModelProvider(PackOutput packOutput) {
        super(packOutput, ProductiveFarming.MODID);
    }

    private static final Set<String> HAND_AUTHORED_BLOCKS = Set.of("farm_controller", "farm_hatch", "fish_trap");

    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        List<Holder<Block>> known = new ArrayList<>();
        ProductiveFarming.BLOCKS.getEntries().forEach(h -> {
            Identifier id = BuiltInRegistries.BLOCK.getKey(h.get());
            if (!HAND_AUTHORED_BLOCKS.contains(id.getPath())) {
                known.add(h.get().builtInRegistryHolder());
            }
        });
        return known.stream();
    }

    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        List<Holder<Item>> known = new ArrayList<>();
        ProductiveFarming.ITEMS.getEntries().forEach(h -> known.add(h.get().builtInRegistryHolder()));
        return known.stream();
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        Set<Item> handledItems = new HashSet<>();

        registerBlockModels(blockModels);
        registerItemModels(blockModels, itemModels, handledItems);

        for (var holder : ProductiveFarming.ITEMS.getEntries()) {
            Item item = holder.get();
            if (handledItems.add(item)) {
                Identifier id = BuiltInRegistries.ITEM.getKey(item);
                itemModels.itemModelOutput.accept(item, ItemModelUtils.plainModel(Identifier.fromNamespaceAndPath(id.getNamespace(), "item/" + id.getPath())));
            }
        }
    }

    private void registerBlockModels(BlockModelGenerators blockModels) {
        var nutrientWater = BuiltInRegistries.BLOCK.getValue(FarmingRegistrator.NUTRIENT_WATER.getId());
        Identifier waterModel = ModelTemplates.CUBE_ALL.create(nutrientWater, TextureMapping.cube(new Material(Identifier.withDefaultNamespace("block/water_still"))), blockModels.modelOutput);
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(nutrientWater, plainVariant(waterModel)));

        FarmingRegistrator.CROPS.forEach(crop -> {
            var block = BuiltInRegistries.BLOCK.getValue(rL(crop.name()));
            switch (block) {
                case GrainCropBlock grainCropBlock -> createCropPlant(blockModels, grainCropBlock, "crops/");
                case DoubleGrainCropBlock doubleGrainCropBlock -> createDoubleCropPlant(blockModels, doubleGrainCropBlock, "crops/");
                case DoubleCropBlock doubleCropBlock -> createDoubleCrossPlant(blockModels, doubleCropBlock, "crops/");
                default -> createCrossPlant(blockModels, block, "crops/");
            }
        });
        FarmingRegistrator.TRELLIS.forEach(crop -> {
            var leafBlock = BuiltInRegistries.BLOCK.getValue(rL(crop.name() + "_leaves"));
            var stemBlock = BuiltInRegistries.BLOCK.getValue(rL(crop.name()));
            var attachedStemBlock = BuiltInRegistries.BLOCK.getValue(rL("attached_" + crop.name() + "_stem"));
            createFenceGrowingPlantBlock(blockModels, stemBlock, attachedStemBlock, leafBlock, "crops/", "trellis/");
        });
        FarmingRegistrator.VERTICAL_TRELLIS.forEach(crop -> {
            var leafBlock = BuiltInRegistries.BLOCK.getValue(rL(crop.name()));
            createFenceGrowingPlantLeafBlock(blockModels, leafBlock, "trellis/", verticalTrellisLeaves);
        });
        FarmingRegistrator.GRAPES.forEach(crop -> {
            var leafBlock = BuiltInRegistries.BLOCK.getValue(rL(crop.name() + "_leaves"));
            var stemBlock = BuiltInRegistries.BLOCK.getValue(rL(crop.name()));
            var attachedStemBlock = BuiltInRegistries.BLOCK.getValue(rL("attached_" + crop.name() + "_stem"));
            createFenceGrowingPlantBlock(blockModels, stemBlock, attachedStemBlock, leafBlock, "crops/", "grapes/");
        });
        FarmingRegistrator.STEMS.forEach(crop -> {
            var fruitBlock = BuiltInRegistries.BLOCK.getValue(rL(crop.name()));
            var stemBlock = BuiltInRegistries.BLOCK.getValue(rL(crop.name() + "_stem"));
            var attachedStemBlock = BuiltInRegistries.BLOCK.getValue(rL("attached_" + crop.name() + "_stem"));
            createStemBlock(blockModels, stemBlock, attachedStemBlock, fruitBlock, "crops/");
        });
        FarmingRegistrator.HERBS.forEach(crop -> createCrossPlant(blockModels, BuiltInRegistries.BLOCK.getValue(rL(crop.name())), "herbs/"));
        FarmingRegistrator.BERRIES.forEach(crop -> createCrossPlant(blockModels, BuiltInRegistries.BLOCK.getValue(rL(crop.name())), "bushes/"));
        FarmingRegistrator.FISHIES.forEach(fish -> {
            if (fish.hasBlock()) {
                var block = BuiltInRegistries.BLOCK.getValue(rL(fish.name()));
                if (block instanceof ClamBlock clamBlock) {
                    createClam(blockModels, clamBlock);
                }
            }
        });
        FarmingRegistrator.CRATED_CROPS.forEach(crate -> createCrate(blockModels, BuiltInRegistries.BLOCK.getValue(rL(crate.getPath() + "_crate"))));
        FarmingRegistrator.FLOWERS.forEach(flower -> {
            var block = BuiltInRegistries.BLOCK.getValue(rL(flower.name()));
            if (flower.isDouble()) {
                createTallCrossFlower(blockModels, block, "flowers/", flower.name(), flower.isThicc());
            } else {
                createCrossFlower(blockModels, block, "flowers/", flower.name());
            }
        });
        FarmingRegistrator.VINES.forEach(flower -> createMultiface(blockModels, BuiltInRegistries.BLOCK.getValue(rL(flower.name())), "flowers/", flower.name()));

        createFeedingTrough(blockModels, FarmingRegistrator.FEEDING_TROUGH.get());
        createFeedingTrough(blockModels, FarmingRegistrator.WATERING_TROUGH.get());

        createAttachedMushroom(blockModels, FarmingRegistrator.BROWN_MUSHROOM_GROWTH.get());
        createAttachedMushroom(blockModels, FarmingRegistrator.RED_MUSHROOM_GROWTH.get());
        createAttachedMushroom(blockModels, FarmingRegistrator.CRIMSON_FUNGUS_GROWTH.get());
        createAttachedMushroom(blockModels, FarmingRegistrator.WARPED_FUNGUS_GROWTH.get());
        FarmingRegistrator.SHROOMS.forEach(cropConfig -> createAttachedMushroom(blockModels, BuiltInRegistries.BLOCK.getValue(rL(cropConfig.name() + "_growth"))));
    }

    static ModelTemplate verticalTrellisLeaves = new ModelTemplate(Optional.of(rL("block/vertical_trellis_leaves")), Optional.empty(), TextureSlot.ALL);
    static ModelTemplate vineLeaves = new ModelTemplate(Optional.of(rL("block/inset_leaves")), Optional.empty(), TextureSlot.ALL);
    static ModelTemplate attachedFencedStemModel = new ModelTemplate(Optional.of(rL("block/fenced_stem_attached")), Optional.empty(), TextureSlot.STEM);
    static ModelTemplate fencedStemFull = new ModelTemplate(Optional.of(rL("block/fenced_stem_full")), Optional.empty(), TextureSlot.STEM);
    static ModelTemplate crop = new ModelTemplate(Optional.of(rL("block/crop")), Optional.empty(), TextureSlot.CROP);
    static ModelTemplate cross = new ModelTemplate(Optional.of(rL("block/cross")), Optional.empty(), TextureSlot.CROSS);
    static ModelTemplate crossOverlay = new ModelTemplate(Optional.of(rL("block/cross_overlay")), Optional.empty(), TextureSlot.CROSS, TextureSlot.EDGE);
    static ModelTemplate thiccCrossOverlay = new ModelTemplate(Optional.of(rL("block/cross_overlay_thicc")), Optional.empty(), TextureSlot.CROSS, TextureSlot.EDGE);
    static ModelTemplate pottedBase = new ModelTemplate(Optional.of(rL("block/tinted_flower_pot_cross")), Optional.empty(), TextureSlot.PLANT, TextureSlot.STEM);
    static ModelTemplate vineOverlay = new ModelTemplate(Optional.of(rL("block/vine_overlay")), Optional.empty(), TextureSlot.PLANT, TextureSlot.EDGE);
    static ModelTemplate crateModel = new ModelTemplate(Optional.of(rL("block/base_crate")), Optional.empty(), TextureSlot.BOTTOM, TextureSlot.SIDE, TextureSlot.TOP, TextureSlot.CROP);
    static ModelTemplate stemColumn = new ModelTemplate(Optional.of(Identifier.withDefaultNamespace("block/cube_column")), Optional.empty(), TextureSlot.END, TextureSlot.SIDE);

    private IntegerProperty ageProp(Block block) {
        return block instanceof IAgeableCropBlock cropBlock ? cropBlock.getAgeProperty() : BlockStateProperties.AGE_3;
    }

    private void createFenceGrowingPlantBlock(BlockModelGenerators blockModels, Block stem, Block attachedStem, Block leafBlock, String prefix, String type) {
        IntegerProperty prop = ageProp(stem);
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(stem).with(
                PropertyDispatch.initial(prop).generate(age -> plainVariant(createSuffixedStemVariant(blockModels, stem, prefix, "stage_" + age, fencedStemFull, type, "stage_" + age)))));

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(attachedStem,
                plainVariant(createSuffixedStemVariant(blockModels, attachedStem, prefix, "attached", attachedFencedStemModel, type, "attached"))));

        createFenceGrowingPlantLeafBlock(blockModels, leafBlock, type, vineLeaves);
    }

    private void createFenceGrowingPlantLeafBlock(BlockModelGenerators blockModels, Block leafBlock, String type, ModelTemplate model) {
        IntegerProperty prop = ageProp(leafBlock);
        Identifier base = BuiltInRegistries.BLOCK.getKey(leafBlock);
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(leafBlock).with(PropertyDispatch.initial(prop).generate(age -> {
            Identifier modelLoc = Identifier.fromNamespaceAndPath(base.getNamespace(), "block/" + type + base.getPath() + "_" + age);
            Material texture = new Material(Identifier.fromNamespaceAndPath(base.getNamespace(), "block/" + type + base.getPath().replace("_leaves", "") + "/stage_" + age));
            Identifier created = model.create(modelLoc, new TextureMapping().put(TextureSlot.ALL, texture), blockModels.modelOutput);
            return plainVariant(created);
        })));
    }

    private void createStemBlock(BlockModelGenerators blockModels, Block stem, Block attachedStem, Block fruitBlock, String prefix) {
        Identifier fruitId = BuiltInRegistries.BLOCK.getKey(fruitBlock);
        Identifier fruitModelLoc = Identifier.fromNamespaceAndPath(fruitId.getNamespace(), "block/" + prefix + fruitId.getPath());
        Material fruitSide = new Material(Identifier.fromNamespaceAndPath(fruitId.getNamespace(), "block/" + prefix + fruitId.getPath() + "_side"));
        Material fruitTop = new Material(Identifier.fromNamespaceAndPath(fruitId.getNamespace(), "block/" + prefix + fruitId.getPath() + "_top"));
        Identifier fruitModel = stemColumn.create(fruitModelLoc, TextureMapping.column(fruitSide, fruitTop), blockModels.modelOutput);
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(fruitBlock, plainVariant(fruitModel)));

        Identifier stemId = BuiltInRegistries.BLOCK.getKey(stem);
        Identifier attachedId = BuiltInRegistries.BLOCK.getKey(attachedStem);
        Material stemTexture = new Material(Identifier.fromNamespaceAndPath(stemId.getNamespace(), "block/" + prefix + stemId.getPath()));
        Material attachedTexture = new Material(Identifier.fromNamespaceAndPath(attachedId.getNamespace(), "block/" + prefix + attachedId.getPath()));
        TextureMapping stemTextureMap = TextureMapping.singleSlot(TextureSlot.STEM, stemTexture);
        TextureMapping attachedStemTextureMap = new TextureMapping().put(TextureSlot.STEM, stemTexture).put(TextureSlot.UPPER_STEM, attachedTexture);

        Identifier attachedStemModel = RenderTypedModelTemplate.ATTACHED_STEM.create(
                Identifier.fromNamespaceAndPath(attachedId.getNamespace(), "block/" + prefix + attachedId.getPath()), attachedStemTextureMap, blockModels.modelOutput);
        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(attachedStem, plainVariant(attachedStemModel))
                        .with(PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_FACING)
                                .select(Direction.WEST, VariantMutator.Y_ROT.withValue(Quadrant.R0))
                                .select(Direction.SOUTH, VariantMutator.Y_ROT.withValue(Quadrant.R270))
                                .select(Direction.NORTH, VariantMutator.Y_ROT.withValue(Quadrant.R90))
                                .select(Direction.EAST, VariantMutator.Y_ROT.withValue(Quadrant.R180))));
        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(stem).with(PropertyDispatch.initial(BlockStateProperties.AGE_7).generate(age ->
                        plainVariant(RenderTypedModelTemplate.STEMS[age].create(
                                Identifier.fromNamespaceAndPath(stemId.getNamespace(), "block/" + prefix + stemId.getPath() + "_stage" + age), stemTextureMap, blockModels.modelOutput)))));
    }

    private void createCropPlant(BlockModelGenerators blockModels, Block block, String prefix) {
        IntegerProperty prop = ageProp(block);
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(block).with(PropertyDispatch.initial(prop).generate(age ->
                plainVariant(createSuffixedVariant(blockModels, block, prefix, "/stage_" + age, crop, loc -> TextureMapping.crop(new Material(loc)))))));
    }

    private void createDoubleCropPlant(BlockModelGenerators blockModels, Block block, String prefix) {
        IntegerProperty prop = ageProp(block);
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(block).with(PropertyDispatch.initial(prop, BlockStateProperties.DOUBLE_BLOCK_HALF).generate((age, part) ->
                plainVariant(createSuffixedVariant(blockModels, block, prefix, "/" + part.getSerializedName() + "/stage_" + age, crop, loc -> TextureMapping.crop(new Material(loc)))))));
    }

    private void createCrossPlant(BlockModelGenerators blockModels, Block block, String prefix) {
        IntegerProperty prop = ageProp(block);
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(block).with(PropertyDispatch.initial(prop).generate(age ->
                plainVariant(createSuffixedVariant(blockModels, block, prefix, "/stage_" + age, cross, loc -> TextureMapping.cross(new Material(loc)))))));
    }

    private void createDoubleCrossPlant(BlockModelGenerators blockModels, Block block, String name) {
        IntegerProperty prop = ageProp(block);
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(block).with(PropertyDispatch.initial(prop, BlockStateProperties.DOUBLE_BLOCK_HALF).generate((age, part) ->
                plainVariant(createSuffixedVariant(blockModels, block, name, "/" + part.getSerializedName() + "/stage_" + age, cross, loc -> TextureMapping.cross(new Material(loc)))))));
    }

    private void createCrossFlower(BlockModelGenerators blockModels, Block block, String prefix, String name) {
        Identifier flowerModel = createSuffixedVariant(blockModels, block, prefix, "/" + name, crossOverlay,
                loc -> new TextureMapping().put(TextureSlot.CROSS, new Material(loc)).put(TextureSlot.EDGE, new Material(loc.withSuffix("_flower"))));
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(block, plainVariant(flowerModel)));

        var pottedBlock = BuiltInRegistries.BLOCK.getValue(rL("potted_" + name));
        Identifier pottedModel = createSuffixedVariant(blockModels, block, prefix, "/potted_" + name, pottedBase,
                loc -> new TextureMapping()
                        .put(TextureSlot.PLANT, new Material(loc.withPath(p -> p.replace("potted_", ""))))
                        .put(TextureSlot.STEM, new Material(loc.withPath(p -> p.replace("potted_", "") + "_flower"))));
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(pottedBlock, plainVariant(pottedModel)));
    }

    private void createTallCrossFlower(BlockModelGenerators blockModels, Block block, String prefix, String suffix, boolean isThicc) {
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(block).with(PropertyDispatch.initial(BlockStateProperties.DOUBLE_BLOCK_HALF).generate(part ->
                plainVariant(createSuffixedVariant(blockModels, block, prefix, "/" + part.getSerializedName(), isThicc ? thiccCrossOverlay : crossOverlay,
                        loc -> new TextureMapping().put(TextureSlot.CROSS, new Material(loc)).put(TextureSlot.EDGE, new Material(loc.withSuffix("_flower"))))))));
    }

    private void createMultiface(BlockModelGenerators blockModels, Block block, String prefix, String name) {
        Identifier modelLoc = createSuffixedVariant(blockModels, block, prefix, "/" + name, vineOverlay,
                loc -> new TextureMapping().put(TextureSlot.PLANT, new Material(loc)).put(TextureSlot.EDGE, new Material(loc.withSuffix("_flower"))));

        MultiPartGenerator multipart = MultiPartGenerator.multiPart(block);
        MultiVariant variant = plainVariant(modelLoc);
        BlockModelGenerators.MULTIFACE_GENERATOR.forEach((direction, mutator) -> {
            var property = MultifaceBlock.getFaceProperty(direction);
            if (block.defaultBlockState().hasProperty(property)) {
                multipart.with(new ConditionBuilder().term(property, true), variant.with(mutator));
            }
        });
        blockModels.blockStateOutput.accept(multipart);
    }

    private Identifier createSuffixedVariant(BlockModelGenerators blockModels, Block block, String prefix, String suffix, ModelTemplate template, Function<Identifier, TextureMapping> mappingGetter) {
        Identifier base = BuiltInRegistries.BLOCK.getKey(block);
        Identifier modelLoc = Identifier.fromNamespaceAndPath(base.getNamespace(), "block/" + prefix + base.getPath() + suffix);
        return template.create(modelLoc, mappingGetter.apply(modelLoc), blockModels.modelOutput);
    }

    private Identifier createSuffixedStemVariant(BlockModelGenerators blockModels, Block block, String prefix, String suffix, ModelTemplate template, String type, String textureSuffix) {
        Identifier base = BuiltInRegistries.BLOCK.getKey(block);
        Identifier modelLoc = Identifier.fromNamespaceAndPath(base.getNamespace(), "block/" + prefix + base.getPath().replace("attached_", "").replace("_stem", "") + "/" + suffix);
        Material texture = new Material(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "block/" + type + "stem/" + textureSuffix));
        return template.create(modelLoc, TextureMapping.singleSlot(TextureSlot.STEM, texture), blockModels.modelOutput);
    }

    static String[] styles = new String[]{"ash", "pomegranate", "teak", "walnut", "whitebeam"};

    private void createCrate(BlockModelGenerators blockModels, Block block) {
        Identifier blockId = BuiltInRegistries.BLOCK.getKey(block);
        Identifier top = Identifier.fromNamespaceAndPath(blockId.getNamespace(), "block/crate/" + blockId.getPath());
        String style = styles[top.getPath().length() % 5];
        TextureMapping mapping = new TextureMapping()
                .put(TextureSlot.TOP, new Material(rL("block/crate/" + style + "/top")))
                .put(TextureSlot.SIDE, new Material(rL("block/crate/" + style + "/side")))
                .put(TextureSlot.BOTTOM, new Material(rL("block/crate/" + style + "/bottom")))
                .put(TextureSlot.CROP, new Material(top));
        Identifier model = crateModel.create(Identifier.fromNamespaceAndPath(blockId.getNamespace(), "block/crates/" + blockId.getPath()), mapping, blockModels.modelOutput);
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(block, plainVariant(model)));
    }

    private void createClam(BlockModelGenerators blockModels, Block block) {
        Identifier base = BuiltInRegistries.BLOCK.getKey(block);
        Identifier modelBase = Identifier.fromNamespaceAndPath(base.getNamespace(), "block/fish/" + base.getPath());
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(BlockStateProperties.OPEN).generate(open -> plainVariant(open ? modelBase.withSuffix("_open") : modelBase)))
                .with(createFacingDispatch()));
    }

    private static PropertyDispatch<VariantMutator> createFacingDispatch() {
        return PropertyDispatch.modify(BlockStateProperties.ATTACH_FACE, BlockStateProperties.HORIZONTAL_FACING)
                .select(AttachFace.FLOOR, Direction.EAST, VariantMutator.Y_ROT.withValue(Quadrant.R90))
                .select(AttachFace.FLOOR, Direction.WEST, VariantMutator.Y_ROT.withValue(Quadrant.R270))
                .select(AttachFace.FLOOR, Direction.SOUTH, VariantMutator.Y_ROT.withValue(Quadrant.R180))
                .select(AttachFace.FLOOR, Direction.NORTH, VariantMutator.Y_ROT.withValue(Quadrant.R0))
                .select(AttachFace.WALL, Direction.EAST, VariantMutator.Y_ROT.withValue(Quadrant.R90).then(VariantMutator.X_ROT.withValue(Quadrant.R90)))
                .select(AttachFace.WALL, Direction.WEST, VariantMutator.Y_ROT.withValue(Quadrant.R270).then(VariantMutator.X_ROT.withValue(Quadrant.R90)))
                .select(AttachFace.WALL, Direction.SOUTH, VariantMutator.Y_ROT.withValue(Quadrant.R180).then(VariantMutator.X_ROT.withValue(Quadrant.R90)))
                .select(AttachFace.WALL, Direction.NORTH, VariantMutator.X_ROT.withValue(Quadrant.R90))
                .select(AttachFace.CEILING, Direction.EAST, VariantMutator.Y_ROT.withValue(Quadrant.R270).then(VariantMutator.X_ROT.withValue(Quadrant.R180)))
                .select(AttachFace.CEILING, Direction.WEST, VariantMutator.Y_ROT.withValue(Quadrant.R90).then(VariantMutator.X_ROT.withValue(Quadrant.R180)))
                .select(AttachFace.CEILING, Direction.SOUTH, VariantMutator.X_ROT.withValue(Quadrant.R180))
                .select(AttachFace.CEILING, Direction.NORTH, VariantMutator.Y_ROT.withValue(Quadrant.R180).then(VariantMutator.X_ROT.withValue(Quadrant.R180)));
    }

    private void createFeedingTrough(BlockModelGenerators blockModels, Block block) {
        Identifier base = BuiltInRegistries.BLOCK.getKey(block);
        Function<String, MultiVariant> m = suffix -> plainVariant(Identifier.fromNamespaceAndPath(base.getNamespace(), "block/" + base.getPath() + suffix));
        blockModels.blockStateOutput.accept(MultiPartGenerator.multiPart(block)
                .with(m.apply("/empty"))
                .with(new ConditionBuilder().term(FeedingTroughBlock.LEVEL, 0), m.apply("/empty"))
                .with(new ConditionBuilder().term(FeedingTroughBlock.LEVEL, 1), m.apply("/half"))
                .with(new ConditionBuilder().term(FeedingTroughBlock.LEVEL, 2), m.apply("/full")));
    }

    private void createAttachedMushroom(BlockModelGenerators blockModels, Block block) {
        Identifier base = BuiltInRegistries.BLOCK.getKey(block);
        Identifier mushroomPlace = Identifier.fromNamespaceAndPath(base.getNamespace(), "block/mushroom_growth/" + base.getPath().replace("_growth", ""));
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(BlockStateProperties.AGE_4).generate(age -> {
                    Material texture = new Material(Identifier.fromNamespaceAndPath(base.getNamespace(), "block/shrooms/" + base.getPath() + "/stage_" + age));
                    Identifier model = RenderTypedModelTemplate.CROP.create(mushroomPlace.withSuffix("_stage_" + age), TextureMapping.crop(texture), blockModels.modelOutput);
                    return plainVariant(model);
                }))
                .with(PropertyDispatch.modify(BlockStateProperties.FACING)
                        .select(Direction.EAST, VariantMutator.Y_ROT.withValue(Quadrant.R90).then(VariantMutator.X_ROT.withValue(Quadrant.R90)))
                        .select(Direction.SOUTH, VariantMutator.Y_ROT.withValue(Quadrant.R180).then(VariantMutator.X_ROT.withValue(Quadrant.R90)))
                        .select(Direction.WEST, VariantMutator.Y_ROT.withValue(Quadrant.R270).then(VariantMutator.X_ROT.withValue(Quadrant.R90)))
                        .select(Direction.NORTH, VariantMutator.X_ROT.withValue(Quadrant.R90))
                        .select(Direction.DOWN, VariantMutator.X_ROT.withValue(Quadrant.R180))
                        .select(Direction.UP, VariantMutator.X_ROT.withValue(Quadrant.R0))));
    }

    private void registerItemModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels, Set<Item> handled) {
        for (CropConfig crop : FarmingRegistrator.BERRIES) {
            flatItem(itemModels, handled, BuiltInRegistries.ITEM.getValue(rL(crop.name())), "item/fruits/");
        }
        for (CropConfig crop : FarmingRegistrator.HERBS) {
            flatItem(itemModels, handled, BuiltInRegistries.ITEM.getValue(rL(crop.name())), "item/crops/");
            if (crop.hasSeed()) flatItem(itemModels, handled, BuiltInRegistries.ITEM.getValue(rL(crop.name() + "_seeds")), "item/seeds/");
        }
        for (CropConfig crop : FarmingRegistrator.CROPS) {
            flatItem(itemModels, handled, BuiltInRegistries.ITEM.getValue(rL(crop.name())), "item/crops/");
            if (crop.hasSeed()) flatItem(itemModels, handled, BuiltInRegistries.ITEM.getValue(rL(crop.name() + "_seeds")), "item/seeds/");
        }
        for (CropConfig crop : FarmingRegistrator.TRELLIS) {
            flatItem(itemModels, handled, BuiltInRegistries.ITEM.getValue(rL(crop.name())), "item/crops/");
            if (crop.hasSeed()) flatItem(itemModels, handled, BuiltInRegistries.ITEM.getValue(rL(crop.name() + "_seeds")), "item/seeds/");
        }
        for (CropConfig crop : FarmingRegistrator.VERTICAL_TRELLIS) {
            flatItem(itemModels, handled, BuiltInRegistries.ITEM.getValue(rL(crop.name())), "item/crops/");
            if (crop.hasSeed()) flatItem(itemModels, handled, BuiltInRegistries.ITEM.getValue(rL(crop.name() + "_seeds")), "item/seeds/");
        }
        for (CropConfig crop : FarmingRegistrator.GRAPES) {
            flatItem(itemModels, handled, BuiltInRegistries.ITEM.getValue(rL(crop.name())), "item/crops/");
            if (crop.hasSeed()) flatItem(itemModels, handled, BuiltInRegistries.ITEM.getValue(rL(crop.name() + "_seeds")), "item/seeds/");
        }
        for (CropConfig crop : FarmingRegistrator.STEMS) {
            blockItemParent(itemModels, handled, BuiltInRegistries.BLOCK.getValue(rL(crop.name())), "crops/", "");
            flatItem(itemModels, handled, BuiltInRegistries.ITEM.getValue(rL(crop.name() + "_slice")), "item/fruits/");
            if (crop.hasSeed()) flatItem(itemModels, handled, BuiltInRegistries.ITEM.getValue(rL(crop.name() + "_seeds")), "item/seeds/");
        }
        for (CropConfig crop : FarmingRegistrator.SHROOMS) {
            flatItem(itemModels, handled, BuiltInRegistries.ITEM.getValue(rL(crop.name())), "item/shrooms/");
        }
        for (FishConfig fish : FarmingRegistrator.FISHIES) {
            if (fish.hasBlock()) {
                flatItem(itemModels, handled, BuiltInRegistries.ITEM.getValue(rL(fish.name())), "item/fish/");
            } else {
                flatItem(itemModels, handled, BuiltInRegistries.ITEM.getValue(rL("raw_" + fish.name())), "item/fish/");
            }
            flatItem(itemModels, handled, BuiltInRegistries.ITEM.getValue(rL("cooked_" + fish.name())), "item/fish/");
        }

        FarmingRegistrator.CRATED_CROPS.forEach(crate -> blockItemParent(itemModels, handled, BuiltInRegistries.BLOCK.getValue(rL(crate.getPath() + "_crate")), "crates/", ""));
        FarmingRegistrator.SEED_BAGS.forEach(seedName -> seedBagItem(itemModels, handled, BuiltInRegistries.ITEM.getValue(rL(seedName.getPath() + "_bag"))));

        for (FlowerConfig flower : FarmingRegistrator.FLOWERS) {
            var block = BuiltInRegistries.BLOCK.getValue(rL(flower.name()));
            if (block instanceof ColorfulTallFlowerBlock) {
                twoLayerFlatItem(itemModels, handled, block.asItem(), "block/flowers/", "/upper", "_flower");
            } else {
                twoLayerFlatItem(itemModels, handled, block.asItem(), "block/flowers/", "/" + flower.name(), "_flower");
            }
        }
        for (FlowerConfig vine : FarmingRegistrator.VINES) {
            var block = BuiltInRegistries.BLOCK.getValue(rL(vine.name()));
            twoLayerFlatItem(itemModels, handled, block.asItem(), "block/flowers/", "/" + vine.name(), "_flower");
        }

        flatItemTinted(itemModels, handled, FarmingRegistrator.POLLEN.get(), "item/", PollenItemTintSource.INSTANCE);
        flatItem(itemModels, handled, FarmingRegistrator.DRIED_LUFFA.get(), "item/materials/");
        flatItem(itemModels, handled, FarmingRegistrator.DRIED_TOBACCO.get(), "item/materials/");
        flatItem(itemModels, handled, FarmingRegistrator.CORN_COB_PIPE.get(), "item/");
        flatItem(itemModels, handled, FarmingRegistrator.BLACK_TEA.get(), "item/");

        blockItemParent(itemModels, handled, FarmingRegistrator.FEEDING_TROUGH.get(), "", "/empty");
        blockItemParent(itemModels, handled, FarmingRegistrator.WATERING_TROUGH.get(), "", "/empty");

        fluidBucketItem(itemModels, handled, FarmingRegistrator.NUTRIENT_WATER.get());
    }

    private void fluidBucketItem(ItemModelGenerators itemModels, Set<Item> handled, Fluid fluid) {
        Item bucket = fluid.getBucket();
        if (!(bucket instanceof BucketItem) || !handled.add(bucket)) return;
        itemModels.itemModelOutput.accept(bucket, new DynamicFluidContainerModel.Unbaked(
                new DynamicFluidContainerModel.Textures(
                        Optional.empty(),
                        Optional.of(new Material(Identifier.withDefaultNamespace("item/bucket"))),
                        Optional.of(new Material(Identifier.fromNamespaceAndPath("neoforge", "item/mask/bucket_fluid_drip"))),
                        Optional.empty()),
                fluid, false, true, true));
    }

    private void seedBagItem(ItemModelGenerators itemModels, Set<Item> handled, Item item) {
        if (item == null || !handled.add(item)) return;
        Identifier bundle = Identifier.withDefaultNamespace("item/bundle");
        itemModels.itemModelOutput.accept(item, ItemModelUtils.composite(
                ItemModelUtils.plainModel(bundle),
                ItemModelUtils.specialModel(bundle, new SeedBagItemRenderer.Unbaked())
        ));
    }

    private void flatItem(ItemModelGenerators itemModels, Set<Item> handled, Item item, String prefix) {
        if (item == null || !handled.add(item)) return;
        Identifier id = BuiltInRegistries.ITEM.getKey(item);
        Identifier texture = Identifier.fromNamespaceAndPath(id.getNamespace(), prefix + id.getPath());
        TextureMapping mapping = new TextureMapping().put(TextureSlot.LAYER0, new Material(texture));
        Identifier model = ModelTemplates.FLAT_ITEM.create(itemModelLocation(item), mapping, itemModels.modelOutput);
        itemModels.itemModelOutput.accept(item, ItemModelUtils.plainModel(model));
    }

    private void twoLayerFlatItem(ItemModelGenerators itemModels, Set<Item> handled, Item item, String prefix, String suffix, String append) {
        if (item == null || !handled.add(item)) return;
        Identifier id = BuiltInRegistries.ITEM.getKey(item);
        Identifier base = Identifier.fromNamespaceAndPath(id.getNamespace(), prefix + id.getPath() + suffix);
        TextureMapping mapping = new TextureMapping()
                .put(TextureSlot.LAYER0, new Material(base))
                .put(TextureSlot.LAYER1, new Material(base.withSuffix(append)));
        Identifier model = ModelTemplates.TWO_LAYERED_ITEM.create(itemModelLocation(item), mapping, itemModels.modelOutput);
        itemModels.itemModelOutput.accept(item, ItemModelUtils.tintedModel(model, ItemModelUtils.constantTint(-1), FlowerItemTintSource.INSTANCE));
    }

    private void flatItemTinted(ItemModelGenerators itemModels, Set<Item> handled, Item item, String prefix, ItemTintSource tint) {
        if (item == null || !handled.add(item)) return;
        Identifier id = BuiltInRegistries.ITEM.getKey(item);
        Identifier texture = Identifier.fromNamespaceAndPath(id.getNamespace(), prefix + id.getPath());
        TextureMapping mapping = new TextureMapping().put(TextureSlot.LAYER0, new Material(texture));
        Identifier model = ModelTemplates.FLAT_ITEM.create(itemModelLocation(item), mapping, itemModels.modelOutput);
        itemModels.itemModelOutput.accept(item, ItemModelUtils.tintedModel(model, tint));
    }

    private void blockItemParent(ItemModelGenerators itemModels, Set<Item> handled, Block block, String prefix, String suffix) {
        Item item = block.asItem();
        if (!(item instanceof BlockItem) || !handled.add(item)) return;
        Identifier blockId = BuiltInRegistries.BLOCK.getKey(block);
        Identifier parent = Identifier.fromNamespaceAndPath(blockId.getNamespace(), "block/" + prefix + blockId.getPath() + suffix);
        itemModels.itemModelOutput.accept(item, ItemModelUtils.plainModel(parent));
    }

    private static Identifier itemModelLocation(Item item) {
        Identifier id = BuiltInRegistries.ITEM.getKey(item);
        return Identifier.fromNamespaceAndPath(id.getNamespace(), "item/" + id.getPath());
    }

    private static MultiVariant plainVariant(Identifier model) {
        return new MultiVariant(WeightedList.of(new Variant(model)));
    }

    private static Identifier rL(String path) {
        return Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, path);
    }
}
