package cy.jdkdigital.productivefarming.registry;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.client.render.entity.WalleyeRenderer;
import cy.jdkdigital.productivefarming.common.block.*;
import cy.jdkdigital.productivefarming.common.block.FarmBlock;
import cy.jdkdigital.productivefarming.common.block.entity.*;
import cy.jdkdigital.productivefarming.common.entity.*;
import cy.jdkdigital.productivefarming.common.feature.SeaFloorFeature;
import cy.jdkdigital.productivefarming.common.fluid.NutrientWater;
import cy.jdkdigital.productivefarming.common.fluid.type.NutrientWaterType;
import cy.jdkdigital.productivefarming.common.item.CornPipeItem;
import cy.jdkdigital.productivefarming.common.item.SeedBagItem;
import cy.jdkdigital.productivefarming.common.item.StemGrowinSeedItem;
import cy.jdkdigital.productivefarming.inventory.FarmControllerContainer;
import cy.jdkdigital.productivefarming.inventory.FeedingTroughContainer;
import cy.jdkdigital.productivefarming.recipe.CropFruitingRecipe;
import cy.jdkdigital.productivefarming.util.CropConfig;
import cy.jdkdigital.productivefarming.util.FishConfig;
import net.minecraft.client.renderer.entity.CodRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.CountConfiguration;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class FarmingRegistrator
{
    public static List<CropConfig> CROPS = new ArrayList<>()
    {{
        add(new CropConfig("arrowroot", false, Foods.POTATO));
        add(new CropConfig("artichoke", false, Foods.BEETROOT));
        add(new CropConfig("arugula", true, Foods.BEETROOT)); // salad
        add(new CropConfig("asparagus", true, Foods.BEETROOT));
        add(new CropConfig("bell_pepper_green", false, Foods.BEETROOT));
        add(new CropConfig("bell_pepper_orange", false, Foods.BEETROOT));
        add(new CropConfig("bell_pepper_red", false, Foods.BEETROOT));
        add(new CropConfig("bell_pepper_yellow", false, Foods.BEETROOT));
        add(new CropConfig("black_beans", false, null));
        add(new CropConfig("bok_choy", true, Foods.BEETROOT));
        add(new CropConfig("broccoli", true, Foods.BEETROOT));
        add(new CropConfig("brussel_sprouts", true, Foods.BEETROOT));
        add(new CropConfig("burdock_root", false, Foods.BEETROOT)); // see salsify, also will cling to the player when walked through
        add(new CropConfig("butterhead_lettuce", true, Foods.BEETROOT));
        add(new CropConfig("cabbage", true, Foods.BEETROOT));
        add(new CropConfig("cauliflower", true, Foods.BEETROOT));
        add(new CropConfig("celery", true, Foods.BEETROOT));
        add(new CropConfig("chard", false, Foods.BEETROOT));
        add(new CropConfig("chili_pepper", false, Foods.BEETROOT));
        add(new CropConfig("collard", true, Foods.BEETROOT));
        add(new CropConfig("daikon", true, Foods.BEETROOT));
        add(new CropConfig("eddoe", false, Foods.BEETROOT));
        add(new CropConfig("eggplant", true, Foods.BEETROOT));
        add(new CropConfig("endive", true, Foods.BEETROOT));
        add(new CropConfig("garlic", false, Foods.BEETROOT));
        add(new CropConfig("iceberg_lettuce", true, Foods.BEETROOT));
        add(new CropConfig("jalapeno", false, Foods.BEETROOT));
        add(new CropConfig("jute", false, Foods.BEETROOT)); // molokhia
        add(new CropConfig("kale", true, Foods.BEETROOT));
        add(new CropConfig("kidney_beans", false, null));
        add(new CropConfig("kohlrabi", true, Foods.BEETROOT));
        add(new CropConfig("konjac", false, Foods.BEETROOT));
        add(new CropConfig("leek", true, Foods.BEETROOT));
        add(new CropConfig("lima_beans", false, null));
        add(new CropConfig("malanga", false, Foods.BEETROOT));
        add(new CropConfig("okra", false, Foods.BEETROOT));
        add(new CropConfig("onion", true, Foods.BEETROOT));
        add(new CropConfig("parsnip", true, Foods.BEETROOT));
        add(new CropConfig("peas", false, Foods.BEETROOT));
        add(new CropConfig("pineapple", true, Foods.BEETROOT)); // plant the top?
        add(new CropConfig("pinto_beans", false, null));
        add(new CropConfig("prickly_pear", false, Foods.BEETROOT)); // TODO it's a cactus fruit
        add(new CropConfig("radish", true, Foods.BEETROOT));
        add(new CropConfig("rhubarb", false, Foods.BEETROOT));
        add(new CropConfig("romain_lettuce", false, Foods.BEETROOT));
        add(new CropConfig("rutabaga", true, Foods.BEETROOT));
        add(new CropConfig("salsify", false, Foods.BEETROOT)); // maybe a weed you can propagate, not actually farm (shear to get the flower, break to get the root?)
        add(new CropConfig("spinach", true, Foods.BEETROOT));
        add(new CropConfig("strawberry", true, null));
        add(new CropConfig("sugar_beet", true, null)); // missing textures
        add(new CropConfig("tomatillo", true, Foods.BEETROOT));
        add(new CropConfig("tomato", true, Foods.BEETROOT));
        add(new CropConfig("turnip", true, Foods.BEETROOT));
        add(new CropConfig("ulluco", false, Foods.POTATO));
        add(new CropConfig("wasabi", false, null));
        add(new CropConfig("yam", true, Foods.BEETROOT));
        add(new CropConfig("zucchini", true, Foods.BEETROOT));

        add(new CropConfig("peanuts", false, Foods.BEETROOT)); // missing texture
        add(new CropConfig("corn", true, Foods.BEETROOT, ProductiveCropBlock::new));
        add(new CropConfig("ginger", false, Foods.BEETROOT));
        add(new CropConfig("green_bean", true, Foods.BEETROOT));
        add(new CropConfig("green_onion", true, Foods.BEETROOT));
        add(new CropConfig("saguaro", true, Foods.BEETROOT));
        add(new CropConfig("squash", true, Foods.BEETROOT));
        add(new CropConfig("sweet_potato", false, Foods.BEETROOT));
        add(new CropConfig("lentils", false, null));
        add(new CropConfig("chickpeas", false, null));

        add(new CropConfig("mustard", true, null));
        add(new CropConfig("pepper", true, null));
        add(new CropConfig("turmeric", true, null));

        add(new CropConfig("rice", true, null));
        add(new CropConfig("oats", true, null, ProductiveCropBlock::new));
        add(new CropConfig("barley", true, null, ProductiveCropBlock::new));
        add(new CropConfig("rye", true, null, ProductiveCropBlock::new));
        add(new CropConfig("amaranth", true, null, ProductiveCropBlock::new));
        add(new CropConfig("cassava", false, null, ProductiveCropBlock::new));
        // Double block crops
        add(new CropConfig("pitaya", true, Foods.SWEET_BERRIES, DoubleCropBlock::new)); // dragonfruit
        add(new CropConfig("monstera_deliciosa", true, Foods.SWEET_BERRIES, DoubleCropBlock::new));
        add(new CropConfig("tobacco", true, null, DoubleCropBlock::new));
        add(new CropConfig("tea", true, null, DoubleCropBlock::new));
        // Water grown plants
        add(new CropConfig("water_chestnut", true, null, WaterCropBlock::new));
        add(new CropConfig("watercress", false, null, WaterCropBlock::new)); // spawns in rivers
        add(new CropConfig("water_caltrop", true, null, WaterCropBlock::new));
    }};
    public static List<CropConfig> TRELLIS = new ArrayList<>() {{
        add(new CropConfig("kiwi", false, Foods.BEETROOT, TrellisLeafBlock::new));
        add(new CropConfig("hops", false, null, TrellisLeafBlock::new));
        add(new CropConfig("vanilla", true, null, TrellisLeafBlock::new));
        add(new CropConfig("akebia", true, Foods.APPLE, TrellisLeafBlock::new));
        add(new CropConfig("goji_berry", false, BERRY_FOOD, TrellisLeafBlock::new));
    }};
    public static List<CropConfig> VERTICAL_TRELLIS = new ArrayList<>() {{
        add(new CropConfig("butternut_squash", true, Foods.BEETROOT, VerticalTrellisLeafBlock::new));
        add(new CropConfig("spoon_gourd", true, Foods.BEETROOT, VerticalTrellisLeafBlock::new));
        add(new CropConfig("luffa", true, Foods.MELON_SLICE, VerticalTrellisLeafBlock::new));
        add(new CropConfig("cucumber", true, Foods.MELON_SLICE, VerticalTrellisLeafBlock::new));
    }};
    public static List<CropConfig> VINES = new ArrayList<>() {{
        add(new CropConfig("red_grape", true, Foods.SWEET_BERRIES, VineLeafBlock::new));
        add(new CropConfig("concord_grape", true, Foods.SWEET_BERRIES, VineLeafBlock::new)); // TODO breedable variants
        add(new CropConfig("cotton_candy_grape", true, Foods.SWEET_BERRIES, VineLeafBlock::new)); // TODO breedable variants
        add(new CropConfig("green_grape", true, Foods.SWEET_BERRIES, VineLeafBlock::new));
    }};
    public static List<CropConfig> STEMS = new ArrayList<>() {{
        add(new CropConfig("cantaloupe", true, Foods.MELON_SLICE)); // TODO stem plant
        add(new CropConfig("honey_dew_melon", true, Foods.MELON_SLICE)); // TODO stem plant
    }};
    public static List<CropConfig> HERBS = new ArrayList<>()
    {{
        add(new CropConfig("basil", false, null, HerbBlock::new)); // missing texture
        add(new CropConfig("chives", false, null, HerbBlock::new));
        add(new CropConfig("coriander", false, null, HerbBlock::new));
        add(new CropConfig("dill", false, null, HerbBlock::new));
        add(new CropConfig("mint", false, null, HerbBlock::new));
        add(new CropConfig("oregano", false, null, HerbBlock::new));
        add(new CropConfig("parsley", false, null, HerbBlock::new));
        add(new CropConfig("rosemary", false, null, HerbBlock::new));
        add(new CropConfig("sage", false, null, HerbBlock::new));
        add(new CropConfig("fat_hen", false, null, HerbBlock::new));
        add(new CropConfig("ostrich_fiddlehead", false, null, HerbBlock::new)); // worldgen
    }};
    static final FoodProperties BERRY_FOOD = (new FoodProperties.Builder()).alwaysEdible().fast().nutrition(1).saturationModifier(0.1F).build();
    public static List<CropConfig> BERRIES = new ArrayList<>()
    {{
        add(new CropConfig("kadsura", false, BERRY_FOOD, BerryBushBlock::new)); // TODO it's a shrub
        add(new CropConfig("blackberry", false, BERRY_FOOD, BerryBushBlock::new));
        add(new CropConfig("blackcurrant", false, BERRY_FOOD, BerryBushBlock::new));
        add(new CropConfig("blueberry", false, BERRY_FOOD, BerryBushBlock::new));
        add(new CropConfig("boysenberry", false, BERRY_FOOD, BerryBushBlock::new));
        add(new CropConfig("cloudberry", false, BERRY_FOOD, BerryBushBlock::new));
        add(new CropConfig("cranberry", false, BERRY_FOOD, BerryBushBlock::new));
        add(new CropConfig("golden_raspberry", false, BERRY_FOOD, BerryBushBlock::new));
        add(new CropConfig("gooseberry", false, BERRY_FOOD, BerryBushBlock::new));
        add(new CropConfig("huckleberry", false, BERRY_FOOD, BerryBushBlock::new));
        add(new CropConfig("lingoberry", false, BERRY_FOOD, BerryBushBlock::new));
        add(new CropConfig("miracle_berry", false, BERRY_FOOD, BerryBushBlock::new));
        add(new CropConfig("mulberry", false, BERRY_FOOD, BerryBushBlock::new));
        add(new CropConfig("raspberry", false, BERRY_FOOD, BerryBushBlock::new));
        add(new CropConfig("redcurrant", false, BERRY_FOOD, BerryBushBlock::new));
        add(new CropConfig("thimbleberry", false, BERRY_FOOD, BerryBushBlock::new));
    }};
    public static List<FishConfig> FISHIES = new ArrayList<>()
    {{
        add(new FishConfig("anchovy", null, null, false, Foods.SALMON, Foods.COOKED_SALMON));
        add(new FishConfig("eel", Eel::new, CodRenderer::new, false, Foods.SALMON, Foods.COOKED_SALMON));
        add(new FishConfig("crab", Crab::new, CodRenderer::new, false, Foods.SALMON, Foods.COOKED_SALMON));
        add(new FishConfig("lobster", Lobster::new, CodRenderer::new, false, Foods.SALMON, Foods.COOKED_SALMON));
        add(new FishConfig("oyster", null, null, true, Foods.SALMON, Foods.COOKED_SALMON));
        add(new FishConfig("clam", null, null, true, Foods.SALMON, Foods.COOKED_SALMON));
        add(new FishConfig("mussel", null, null, true, Foods.SALMON, Foods.COOKED_SALMON));
        add(new FishConfig("sea_urchin", null, null, true, Foods.SALMON, null));
        add(new FishConfig("carp", Carp::new, CodRenderer::new, false, Foods.SALMON, Foods.COOKED_SALMON));
        add(new FishConfig("koi", Koi::new, CodRenderer::new, false, Foods.SALMON, Foods.COOKED_SALMON));
        add(new FishConfig("shrimp", null, null, false, Foods.SALMON, Foods.COOKED_SALMON));
        add(new FishConfig("walleye", Walleye::new, WalleyeRenderer::new, false, Foods.SALMON, Foods.COOKED_SALMON));
        add(new FishConfig("sturgeon", Tuna::new, CodRenderer::new, false, Foods.SALMON, Foods.COOKED_SALMON));
        add(new FishConfig("tuna", Tuna::new, CodRenderer::new, false, Foods.SALMON, Foods.COOKED_SALMON));
    }};

    public static List<ResourceLocation> CRATED_CROPS = new ArrayList<>()
    {{
        add(ResourceLocation.withDefaultNamespace("potato"));
        add(ResourceLocation.withDefaultNamespace("baked_potato"));
        add(ResourceLocation.withDefaultNamespace("carrot"));
        add(ResourceLocation.withDefaultNamespace("beetroot"));
        add(ResourceLocation.withDefaultNamespace("apple"));
        add(ResourceLocation.withDefaultNamespace("chorus_fruit"));
        add(ResourceLocation.withDefaultNamespace("sweet_berries"));
        add(ResourceLocation.withDefaultNamespace("glow_berries"));
        add(ResourceLocation.withDefaultNamespace("beef"));
        add(ResourceLocation.withDefaultNamespace("cooked_beef"));
        add(ResourceLocation.withDefaultNamespace("chicken"));
        add(ResourceLocation.withDefaultNamespace("cooked_chicken"));
        add(ResourceLocation.withDefaultNamespace("mutton"));
        add(ResourceLocation.withDefaultNamespace("cooked_mutton"));
        add(ResourceLocation.withDefaultNamespace("porkchop"));
        add(ResourceLocation.withDefaultNamespace("cooked_porkchop"));
        add(ResourceLocation.withDefaultNamespace("rabbit"));
        add(ResourceLocation.withDefaultNamespace("cooked_rabbit"));
        add(ResourceLocation.withDefaultNamespace("salmon"));
        add(ResourceLocation.withDefaultNamespace("cooked_salmon"));
        add(ResourceLocation.withDefaultNamespace("cod"));
        add(ResourceLocation.withDefaultNamespace("cooked_cod"));
        add(ResourceLocation.withDefaultNamespace("tropical_fish"));
        add(ResourceLocation.withDefaultNamespace("pufferfish"));
        add(ResourceLocation.withDefaultNamespace("cocoa_beans"));
        add(ResourceLocation.withDefaultNamespace("egg"));
        add(ResourceLocation.withDefaultNamespace("turtle_egg"));
        add(ResourceLocation.withDefaultNamespace("sniffer_egg"));
        add(ResourceLocation.withDefaultNamespace("golden_apple"));
        add(ResourceLocation.withDefaultNamespace("golden_carrot"));
        add(ResourceLocation.withDefaultNamespace("ink_sac"));
    }};
    public static List<ResourceLocation> SEED_BAGS = new ArrayList<>()
    {{
        add(ResourceLocation.withDefaultNamespace("wheat_seeds"));
        add(ResourceLocation.withDefaultNamespace("pumpkin_seeds"));
        add(ResourceLocation.withDefaultNamespace("melon_seeds"));
        add(ResourceLocation.withDefaultNamespace("beetroot_seeds"));
        add(ResourceLocation.withDefaultNamespace("torchflower_seeds"));
        add(ResourceLocation.withDefaultNamespace("pitcher_pod"));
    }};

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<FencedLeafBlockEntity>> FENCED_VERTICAL_CROP_BLOCK_ENTITY;
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<FencedVerticalCropBlockEntity>> FENCED_LEAVES_BLOCK_ENTITY;
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<FencedStemBlockEntity>> FENCED_STEM_BLOCK_ENTITY;

    static Map<String, DeferredHolder<Block, Block>> registeredBlocks = new HashMap<>();
    public static void init() {
        CROPS.forEach(crop -> {
            registeredBlocks.put(crop.name(), registerPlantableCrop(crop, () -> crop.supplier().create(crop, BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT))));
            if (crop.food() != null) {
                CRATED_CROPS.add(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name()));
            }
            if (crop.hasSeed()) {
                SEED_BAGS.add(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name() + "_seeds"));
            }
        });
        TRELLIS.forEach(crop -> {
            registeredBlocks.put(crop.name() + "_leaves", registerBlock(crop.name() + "_leaves", () -> crop.supplier().create(crop, BlockBehaviour.Properties.ofFullCopy(Blocks.CHERRY_LEAVES).offsetType(BlockBehaviour.OffsetType.XYZ).dynamicShape()), false));
            registeredBlocks.put(crop.name(), registerPlantableCrop(crop, () -> new FencedStemBlock(crop, BlockBehaviour.Properties.ofFullCopy(Blocks.MELON_STEM)), StemGrowinSeedItem::new));
            registeredBlocks.put("attached_" + crop.name() + "_stem", registerBlock("attached_" + crop.name() + "_stem", () -> new AttachedFencedStemBlock(crop, BlockBehaviour.Properties.ofFullCopy(Blocks.ATTACHED_MELON_STEM)), false));
            if (crop.food() != null) {
                CRATED_CROPS.add(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name()));
            }
        });
        VERTICAL_TRELLIS.forEach(crop -> {
            registeredBlocks.put(crop.name(), registerPlantableCrop(crop, () -> crop.supplier().create(crop, BlockBehaviour.Properties.ofFullCopy(Blocks.CHERRY_LEAVES).offsetType(BlockBehaviour.OffsetType.XYZ).dynamicShape()), StemGrowinSeedItem::new));
            if (crop.food() != null) {
                CRATED_CROPS.add(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name()));
            }
        });
        VINES.forEach(crop -> {
            registeredBlocks.put(crop.name() + "_leaves", registerBlock(crop.name() + "_leaves", () -> crop.supplier().create(crop, BlockBehaviour.Properties.ofFullCopy(Blocks.CHERRY_LEAVES).offsetType(BlockBehaviour.OffsetType.XYZ).dynamicShape()), false));
            registeredBlocks.put(crop.name(), registerPlantableCrop(crop, () -> new FencedStemBlock(crop, BlockBehaviour.Properties.ofFullCopy(Blocks.MELON_STEM)), StemGrowinSeedItem::new));
            registeredBlocks.put("attached_" + crop.name() + "_stem", registerBlock("attached_" + crop.name() + "_stem", () -> new AttachedFencedStemBlock(crop, BlockBehaviour.Properties.ofFullCopy(Blocks.ATTACHED_MELON_STEM)), false));
            if (crop.food() != null) {
                CRATED_CROPS.add(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name()));
            }
        });
        STEMS.forEach(crop -> {
            registerItem(crop.name() + "_slice", crop.food());
            registerItem(crop.name() + "_seeds", () -> new ItemNameBlockItem(registeredBlocks.get(crop.name() + "_stem").get(), new Item.Properties()));
            registeredBlocks.put(crop.name(), registerBlock(crop.name(), () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.MELON)), true));
            registeredBlocks.put(crop.name() + "_stem", registerBlock(crop.name() + "_stem", () -> new StemBlock(
                    ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name())),
                    ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "attached_" + crop.name() + "_stem")),
                    ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name() + "_seed")),
                    BlockBehaviour.Properties.ofFullCopy(Blocks.MELON_STEM)), false));
            registeredBlocks.put("attached_" + crop.name() + "_stem", registerBlock("attached_" + crop.name() + "_stem", () -> new AttachedStemBlock(
                    ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name() + "_stem")),
                    ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name())),
                    ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name() + "_seed")),
                    BlockBehaviour.Properties.ofFullCopy(Blocks.MELON_STEM)), false));
            CRATED_CROPS.add(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name()));
        });
        HERBS.forEach(crop -> {
            registeredBlocks.put(crop.name(), registerPlantableCrop(crop, () -> crop.supplier().create(crop, BlockBehaviour.Properties.ofFullCopy(Blocks.SHORT_GRASS).dynamicShape())));
            if (crop.food() != null) {
                CRATED_CROPS.add(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name()));
            }
        });
        BERRIES.forEach(crop -> {
            registeredBlocks.put(crop.name(), registerPlantableCrop(crop, () -> crop.supplier().create(crop, BlockBehaviour.Properties.ofFullCopy(Blocks.SWEET_BERRY_BUSH).dynamicShape())));
            if (crop.food() != null) {
                CRATED_CROPS.add(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name()));
            }
        });
        FISHIES.forEach(fish -> {
            if (fish.entitySupplier() != null) {
                registerFishEntity(fish.name(), fish.entitySupplier());
            }
            if (fish.hasBlock()) {
                registeredBlocks.put(fish.name(), registerBlock(fish.name(), () -> new ClamBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BRAIN_CORAL)), true));
                CRATED_CROPS.add(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, fish.name()));
            } else {
                registerItem("raw_" + fish.name());
                CRATED_CROPS.add(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "raw_" + fish.name()));
            }
            registerItem("cooked_" + fish.name());
            CRATED_CROPS.add(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "cooked_" + fish.name()));
        });
        CRATED_CROPS.forEach(crate -> {
            registeredBlocks.put(crate.getPath() + "_crate", registerBlock(crate.getPath() + "_crate", () -> new CrateBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL)), true));
        });
        SEED_BAGS.forEach(seedName -> {
            registerItem(seedName.getPath() + "_bag", () -> new SeedBagItem(seedName, new Item.Properties()));
        });

        FENCED_VERTICAL_CROP_BLOCK_ENTITY = ProductiveFarming.BLOCK_ENTITIES.register("fenced_crop", () -> BlockEntityType.Builder.of(FencedLeafBlockEntity::new,
                VERTICAL_TRELLIS.stream().map(cropConfig -> registeredBlocks.get(cropConfig.name()).get()).toList().toArray(new Block[0])
        ).build(null));
        FENCED_LEAVES_BLOCK_ENTITY = ProductiveFarming.BLOCK_ENTITIES.register("fenced_leaves", () -> BlockEntityType.Builder.of(FencedVerticalCropBlockEntity::new,
                Stream.concat(TRELLIS.stream(), VINES.stream()).map(cropConfig -> registeredBlocks.get(cropConfig.name() + "_leaves").get()).toList().toArray(new Block[0])
        ).build(null));
        FENCED_STEM_BLOCK_ENTITY = ProductiveFarming.BLOCK_ENTITIES.register("fenced_stem", () -> BlockEntityType.Builder.of(FencedStemBlockEntity::new,
                Stream.concat(TRELLIS.stream(), VINES.stream()).map(cropConfig -> List.of(registeredBlocks.get("attached_" + cropConfig.name() + "_stem").get(), registeredBlocks.get(cropConfig.name()).get())).flatMap(List::stream).toList().toArray(new Block[0])
        ).build(null));
    }
    
    // Non-crop items
    public static final DeferredHolder<Item, Item> DRIED_LUFFA = registerItem("dried_luffa");
    public static final DeferredHolder<Item, Item> DRIED_TOBACCO = registerItem("dried_tobacco");
    public static final DeferredHolder<Item, Item> BLACK_TEA = registerItem("black_tea");
    public static final DeferredHolder<Item, Item> CORN_COB_PIPE = registerItem("corn_cob_pipe", () -> new CornPipeItem(new Item.Properties().stacksTo(1).durability(200)));

    // Composter mushroom growth
    public static final DeferredHolder<Block, Block> BROWN_MUSHROOM_GROWTH = registerBlock("brown_mushroom_growth", () -> new MushroomGrowthBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BROWN_MUSHROOM).replaceable().dynamicShape(), ResourceLocation.withDefaultNamespace("brown_mushroom")), false);
    public static final DeferredHolder<Block, Block> RED_MUSHROOM_GROWTH = registerBlock("red_mushroom_growth", () -> new MushroomGrowthBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.RED_MUSHROOM).replaceable().dynamicShape(), ResourceLocation.withDefaultNamespace("red_mushroom")), false);
    public static final DeferredHolder<Block, Block> CRIMSON_FUNGUS_GROWTH = registerBlock("crimson_fungus_growth", () -> new MushroomGrowthBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CRIMSON_FUNGUS).replaceable().dynamicShape(), ResourceLocation.withDefaultNamespace("crimson_fungus")), false);
    public static final DeferredHolder<Block, Block> WARPED_FUNGUS_GROWTH = registerBlock("warped_fungus_growth", () -> new MushroomGrowthBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WARPED_FUNGUS).replaceable().dynamicShape(), ResourceLocation.withDefaultNamespace("warped_fungus")), false);

    // Machines
    public static final DeferredHolder<Block, Block> FISH_TRAP = registerBlock("fish_trap", () -> new FishTrapBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL)), true);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FishTrapBlockEntity>> FISH_TRAP_BLOCK_ENTITY = ProductiveFarming.BLOCK_ENTITIES.register("fish_trap", () -> BlockEntityType.Builder.of(FishTrapBlockEntity::new, FISH_TRAP.get()).build(null));
    public static final DeferredHolder<Block, Block> FARM_BLOCK = registerBlock("farm_block", () -> new FarmBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)), false);
    public static final DeferredHolder<Block, Block> FARM_HATCH = registerBlock("farm_hatch", () -> new FarmHatch(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)), true);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FarmHatchBlockEntity>> FARM_HATCH_BLOCK_ENTITY = ProductiveFarming.BLOCK_ENTITIES.register("farm_hatch", () -> BlockEntityType.Builder.of(FarmHatchBlockEntity::new, FARM_HATCH.get()).build(null));
    public static final DeferredHolder<Block, Block> FARM_CONTROLLER = registerBlock("farm_controller", () -> new FarmController(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS).noOcclusion()), true);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FarmControllerBlockEntity>> FARM_CONTROLLER_BLOCK_ENTITY = ProductiveFarming.BLOCK_ENTITIES.register("farm_controller", () -> BlockEntityType.Builder.of(FarmControllerBlockEntity::new, FARM_CONTROLLER.get()).build(null));
    public static final DeferredHolder<Block, Block> FEEDING_TROUGH = registerBlock("feeding_trough", () -> new FeedingTroughBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON).noOcclusion()), true);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FeedingTroughBlockEntity>> FEEDING_TROUGH_BLOCK_ENTITY = ProductiveFarming.BLOCK_ENTITIES.register("feeding_trough", () -> BlockEntityType.Builder.of(FeedingTroughBlockEntity::new, FEEDING_TROUGH.get()).build(null));
    public static final DeferredHolder<Block, Block> WATERING_TROUGH = registerBlock("watering_trough", () -> new WateringTroughBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON).noOcclusion()), true);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WateringTroughBlockEntity>> WATERING_TROUGH_BLOCK_ENTITY = ProductiveFarming.BLOCK_ENTITIES.register("watering_trough", () -> BlockEntityType.Builder.of(WateringTroughBlockEntity::new, WATERING_TROUGH.get()).build(null));
    public static final DeferredHolder<Block, Block> SALT_LICK = registerBlock("salt_lick", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()), true);
    public static final DeferredHolder<Block, Block> CHILD_SEPARATOR = registerBlock("child_separator", () -> new ChildSeparatorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()), true);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ChildSeparatorBlockEntity>> CHILD_SEPARATOR_BLOCK_ENTITY = ProductiveFarming.BLOCK_ENTITIES.register("child_separator", () -> BlockEntityType.Builder.of(ChildSeparatorBlockEntity::new, CHILD_SEPARATOR.get()).build(null));
    public static final DeferredHolder<Block, Block> SLAUGHTER_STATION = registerBlock("slaughter_station", () -> new SlaughterStationBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()), true);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SlaughterStationBlockEntity>> SLAUGHTER_STATION_BLOCK_ENTITY = ProductiveFarming.BLOCK_ENTITIES.register("slaughter_station", () -> BlockEntityType.Builder.of(SlaughterStationBlockEntity::new, SLAUGHTER_STATION.get()).build(null));
    // TODO drying rack with drying recipe

    public static final DeferredHolder<MenuType<?>, MenuType<FarmControllerContainer>> FARM_CONTROLLER_MENU = ProductiveFarming.CONTAINER_TYPES.register("farm_controller", () ->
            IMenuTypeExtension.create(FarmControllerContainer::new)
    );
    public static final DeferredHolder<MenuType<?>, MenuType<FeedingTroughContainer>> FEEDING_TROUGH_MENU = ProductiveFarming.CONTAINER_TYPES.register("feeding_trough", () ->
            IMenuTypeExtension.create(FeedingTroughContainer::new)
    );

    // POIs
    public static DeferredHolder<PoiType, PoiType> SALT_LICK_POI = ProductiveFarming.POI_TYPES.register("salt_lick", () -> {
        Set<BlockState> blockStates = new HashSet<>(SALT_LICK.get().getStateDefinition().getPossibleStates());
        return new PoiType(blockStates, 1, 1);
    });

    // Recipes
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> CROP_FRUITING = ProductiveFarming.RECIPE_SERIALIZERS.register("crop_fruiting", CropFruitingRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<CropFruitingRecipe>> CROP_FRUITING_TYPE = ProductiveFarming.RECIPE_TYPES.register("crop_fruiting", () -> new RecipeType<>() {});

    // Fluids
    public static final DeferredHolder<FluidType, FluidType> NUTRIENT_WATER_TYPE = ProductiveFarming.FLUID_TYPES.register("nutrient_water", NutrientWaterType::new);
    public static final DeferredHolder<Fluid, BaseFlowingFluid> NUTRIENT_WATER = ProductiveFarming.FLUIDS.register("nutrient_water", NutrientWater.Source::new);
    public static final DeferredHolder<Fluid, BaseFlowingFluid> NUTRIENT_WATER_FLOWING = ProductiveFarming.FLUIDS.register("flowing_nutrient_water", NutrientWater.Flowing::new);
    public static final DeferredHolder<Item, Item> NUTRIENT_WATER_BUCKET = registerItem("nutrient_water_bucket", () -> new BucketItem(NUTRIENT_WATER.get(), new Item.Properties().craftRemainder(Items.BUCKET)));


    // Features
    public static final DeferredHolder<Feature<?>, Feature<CountConfiguration>> CLAM_FEATURE = ProductiveFarming.FEATURES.register("clam", () -> new SeaFloorFeature(CountConfiguration.CODEC, () -> BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "clam"))));
    public static final DeferredHolder<Feature<?>, Feature<CountConfiguration>> OYSTER_FEATURE = ProductiveFarming.FEATURES.register("oyster", () -> new SeaFloorFeature(CountConfiguration.CODEC, () -> BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "oyster"))));
    public static final DeferredHolder<Feature<?>, Feature<CountConfiguration>> MUSSEL_FEATURE = ProductiveFarming.FEATURES.register("mussel", () -> new SeaFloorFeature(CountConfiguration.CODEC, () -> BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "mussel"))));

    public static final ResourceKey<CreativeModeTab> TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, ProductiveFarming.MODID));
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = ProductiveFarming.CREATIVE_MODE_TABS.register(ProductiveFarming.MODID, () -> {
        return CreativeModeTab.builder()
                .icon(() -> new ItemStack(FEEDING_TROUGH.get()))
                .title(Component.translatable("itemGroup." + ProductiveFarming.MODID))
                .build();
    });

    public static DeferredHolder<Block, Block> registerPlantableCrop(CropConfig crop, Supplier<Block> supplier) {
        return registerPlantableCrop(crop, supplier, ItemNameBlockItem::new);
    }

    public static DeferredHolder<Block, Block> registerPlantableCrop(CropConfig crop, Supplier<Block> supplier, CropItemSupplier<BlockItem> item) {
        var cropBlock = registerBlock(crop.name(), supplier, false);
        if (crop.hasSeed()) {
            registerItem(crop.name() + "_seeds", () -> item.create(cropBlock.get(), new Item.Properties()));
            if (crop.food() != null) {
                registerItem(crop.name(), crop.food());
            } else {
                registerItem(crop.name());
            }
        } else {
            if (crop.food() != null) {
                registerItem(crop.name(), () -> item.create(cropBlock.get(), new Item.Properties().food(crop.food())));
            } else {
                registerItem(crop.name(), () -> item.create(cropBlock.get(), new Item.Properties()));
            }
        }
        return cropBlock;
    }

    public static DeferredHolder<Item, Item> registerItem(String name) {
        return registerItem(name, () -> new Item(new Item.Properties()));
    }

    public static DeferredHolder<Item, Item> registerItem(String name, FoodProperties food) {
        return registerItem(name, () -> new Item(new Item.Properties().food(food)));
    }

    public static DeferredHolder<Item, Item> registerItem(String name, Supplier<Item> supplier) {
        return ProductiveFarming.ITEMS.register(name, supplier);
    }

    public static DeferredHolder<Block, Block> registerBlock(String name, Supplier<Block> supplier, boolean hasItem) {
        var block = ProductiveFarming.BLOCKS.register(name, supplier);
        if (hasItem) {
            registerItem(name, () -> new BlockItem(block.get(), new Item.Properties()));
        }
        return block;
    }

    public static <E extends Mob> DeferredHolder<EntityType<?>, EntityType<E>> registerFishEntity(String name, EntityType.EntityFactory<E> supplier) {
        EntityType.Builder<E> builder = EntityType.Builder.of(supplier, MobCategory.WATER_AMBIENT).sized(0.5F, 0.3F).setTrackingRange(4);

        return registerEntity(name, builder);
    }

    public static <E extends Mob> DeferredHolder<EntityType<?>, EntityType<E>> registerEntity(String name, EntityType.Builder<E> builder) {
        var entity = ProductiveFarming.ENTITY_TYPES.register(name, () -> builder.build(ProductiveFarming.MODID + ":" + name));
        registerItem(name + "_spawn_egg", () -> new DeferredSpawnEggItem(entity, 1510515, 1214544, new Item.Properties()));
        registerItem(name + "_bucket", () -> new MobBucketItem(entity.get(), Fluids.WATER, SoundEvents.BUCKET_EMPTY_FISH, new Item.Properties()));
        return entity;
    }

    @FunctionalInterface
    public interface CropBlockSupplier<T extends BushBlock>
    {
        T create(CropConfig crop, BlockBehaviour.Properties properties);
    }

    @FunctionalInterface
    public interface CropItemSupplier<T extends Item>
    {
        T create(Block block, Item.Properties properties);
    }
}
