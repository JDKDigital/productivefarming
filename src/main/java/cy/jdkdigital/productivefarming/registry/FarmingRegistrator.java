package cy.jdkdigital.productivefarming.registry;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.common.block.*;
import cy.jdkdigital.productivefarming.common.block.entity.*;
import cy.jdkdigital.productivefarming.common.datamap.CropTrait;
import cy.jdkdigital.productivefarming.common.item.*;
import cy.jdkdigital.productivefarming.inventory.FarmControllerContainer;
import cy.jdkdigital.productivefarming.inventory.FeedingTroughContainer;
import cy.jdkdigital.productivefarming.recipe.CropFruitingRecipe;
import cy.jdkdigital.productivefarming.recipe.CropMutationRecipe;
import cy.jdkdigital.productivefarming.recipe.FlowerDyeCraftingRecipe;
import cy.jdkdigital.productivefarming.util.CropConfig;
import cy.jdkdigital.productivefarming.util.FishConfig;
import cy.jdkdigital.productivefarming.util.FlowerConfig;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FarmingRegistrator
{
    public static final FoodProperties LEAFY = new FoodProperties.Builder().nutrition(1).build();

    public static List<CropConfig> VANILLA_CROPS = new ArrayList<>() {{
        add(new CropConfig("wheat", true, null, GrainCropBlock::new));
        add(new CropConfig("potato", false, Foods.POTATO, GrainCropBlock::new));
        add(new CropConfig("carrot", false, Foods.CARROT, GrainCropBlock::new));
        add(new CropConfig("beetroot", true, Foods.BEETROOT, GrainCropBlock::new));
    }};
    public static List<CropConfig> CROPS = new ArrayList<>()
    {{
        add(new CropConfig("arrowroot", false, Foods.POTATO));
        add(new CropConfig("arugula", true, LEAFY)); // salad
        add(new CropConfig("green_bell_pepper", false, Foods.BEETROOT));
        add(new CropConfig("yellow_bell_pepper", false, Foods.BEETROOT));
        add(new CropConfig("orange_bell_pepper", false, Foods.BEETROOT));
        add(new CropConfig("red_bell_pepper", false, Foods.BEETROOT));
        add(new CropConfig("black_bell_pepper", false, Foods.BEETROOT));
        add(new CropConfig("purple_bell_pepper", false, Foods.BEETROOT));
        add(new CropConfig("white_bell_pepper", false, Foods.BEETROOT));
        add(new CropConfig("black_beans", false, null));
        add(new CropConfig("bok_choy", true, LEAFY));
        add(new CropConfig("broccoli", true, LEAFY));
        add(new CropConfig("brussels_sprout", true, LEAFY));
        add(new CropConfig("burdock_root", false, Foods.BEETROOT)); // see salsify, TODO will cling to the player when walked through
        add(new CropConfig("butterhead_lettuce", true, LEAFY));
        add(new CropConfig("cabbage", true, LEAFY));
        add(new CropConfig("catnip", true, null));
        add(new CropConfig("cauliflower", true, Foods.BEETROOT));
        add(new CropConfig("caraway", false, null));
        add(new CropConfig("celery", true, LEAFY));
        add(new CropConfig("chard", true, LEAFY));
        add(new CropConfig("chamomile", true, null));
        add(new CropConfig("chili_pepper", false, Foods.BEETROOT));
        add(new CropConfig("collard", true, LEAFY));
        add(new CropConfig("daikon", true, LEAFY));
        add(new CropConfig("eddoe", false, Foods.BEETROOT));
        add(new CropConfig("eggplant", false, Foods.BEETROOT));
        add(new CropConfig("endive", true, LEAFY));
        add(new CropConfig("flax", true, null));
        add(new CropConfig("garlic", false, Foods.BEETROOT));
        add(new CropConfig("iceberg_lettuce", true, LEAFY));
        add(new CropConfig("jalapeno", false, Foods.BEETROOT));
        add(new CropConfig("kale", true, LEAFY));
        add(new CropConfig("kidney_beans", false, null));
        add(new CropConfig("kohlrabi", true, Foods.BEETROOT));
        add(new CropConfig("leek", true, Foods.BEETROOT));
        add(new CropConfig("lima_beans", false, null));
        add(new CropConfig("onion", true, Foods.BEETROOT));
        add(new CropConfig("parsnip", true, Foods.BEETROOT));
        add(new CropConfig("peas", false, Foods.BEETROOT));
        add(new CropConfig("pinto_beans", false, null));
        add(new CropConfig("pineapple", false, Foods.BEETROOT)); // plant the top?
        add(new CropConfig("radish", true, Foods.BEETROOT));
        add(new CropConfig("rhubarb", false, Foods.BEETROOT));
        add(new CropConfig("romain_lettuce", true, LEAFY));
        add(new CropConfig("rutabaga", true, Foods.BEETROOT));
        add(new CropConfig("salsify", true, Foods.BEETROOT)); // maybe a weed you can propagate, not actually farm (shear to get the flower, break to get the root?)
        add(new CropConfig("soy_bean", false, Foods.BEETROOT));
        add(new CropConfig("spinach", true, LEAFY));
        add(new CropConfig("strawberry", false, null));
        add(new CropConfig("sugar_beet", true, null));
        add(new CropConfig("sweet_marjoram", true, null));
        add(new CropConfig("turnip", true, Foods.BEETROOT));
        add(new CropConfig("ulluco", false, Foods.POTATO));
        add(new CropConfig("wasabi", true, null));
        add(new CropConfig("blue_borage", true, null));

        add(new CropConfig("peanuts", false, Foods.BEETROOT));
        add(new CropConfig("ginger", false, Foods.BEETROOT));
        add(new CropConfig("green_bean", false, Foods.BEETROOT));
        add(new CropConfig("spring_onion", true, LEAFY));
//        add(new CropConfig("saguaro", false, Foods.BEETROOT)); // it's a cactus
        add(new CropConfig("sweet_potato", false, Foods.BEETROOT));
        add(new CropConfig("lentils", false, null));
        add(new CropConfig("chickpeas", false, null));
        add(new CropConfig("mustard", false, null));
        add(new CropConfig("tea", true, null));
        add(new CropConfig("wintergreen", false, BERRY_FOOD));
        add(new CropConfig("hops", false, null));
        // Grains
        add(new CropConfig("rice", true, null, GrainCropBlock::new));
        add(new CropConfig("oats", true, null, GrainCropBlock::new));
        add(new CropConfig("barley", true, null, GrainCropBlock::new));
        add(new CropConfig("rye", true, null, GrainCropBlock::new));
        add(new CropConfig("teff", true, null, GrainCropBlock::new));
        add(new CropConfig("amaranth", true, null, DoubleCropBlock::new));
        add(new CropConfig("sorghum", true, null, DoubleCropBlock::new));
        add(new CropConfig("quinoa", true, null, DoubleCropBlock::new));
        // Double block crops
        add(new CropConfig("agave", false, Foods.SWEET_BERRIES, DoubleCropBlock::new));
        add(new CropConfig("ferula", true, Foods.SWEET_BERRIES, DoubleCropBlock::new)); // Asafoetida
        add(new CropConfig("cardamon", false, Foods.SWEET_BERRIES, DoubleCropBlock::new));
        add(new CropConfig("cassava", false, null, DoubleCropBlock::new));
        add(new CropConfig("pitaya", false, Foods.SWEET_BERRIES, DoubleCropBlock::new)); // dragonfruit
        add(new CropConfig("monstera_deliciosa", false, Foods.SWEET_BERRIES, DoubleCropBlock::new));
        add(new CropConfig("tobacco", true, null, DoubleCropBlock::new));
        add(new CropConfig("black_pepper", false, null, DoubleCropBlock::new));
        add(new CropConfig("jute", true, Foods.BEETROOT, DoubleCropBlock::new)); // molokhia
        add(new CropConfig("cotton", true, null, DoubleCropBlock::new));
        add(new CropConfig("turmeric", false, null, DoubleCropBlock::new));
        add(new CropConfig("black_aztec_corn", false, Foods.BEETROOT, DoubleCropBlock::new, 0xff191929));
        add(new CropConfig("blue_jade_corn", false, Foods.BEETROOT, DoubleCropBlock::new, 0xff91ace4));
        add(new CropConfig("rainbow_corn", false, Foods.BEETROOT, DoubleCropBlock::new, 0xffae4145));
        add(new CropConfig("sugar_pearl_corn", false, Foods.BEETROOT, DoubleCropBlock::new, 0xffecece6));
        add(new CropConfig("yellow_dent_corn", false, Foods.BEETROOT, DoubleCropBlock::new, 0xffeac237));
        add(new CropConfig("tomatillo", false, Foods.BEETROOT, DoubleCropBlock::new));
        add(new CropConfig("huckleberry", false, BERRY_FOOD, DoubleCropBlock::new));
        add(new CropConfig("roma_tomato", false, Foods.BEETROOT, DoubleCropBlock::new));
        add(new CropConfig("beefsteak_tomato", false, Foods.BEETROOT, DoubleCropBlock::new));
        add(new CropConfig("black_beauty_tomato", false, Foods.BEETROOT, DoubleCropBlock::new));
        add(new CropConfig("blue_beauty_tomato", false, Foods.BEETROOT, DoubleCropBlock::new));
        add(new CropConfig("white_wonder_tomato", false, Foods.BEETROOT, DoubleCropBlock::new));
        add(new CropConfig("cherry_tomato", false, Foods.BEETROOT, DoubleCropBlock::new));
        add(new CropConfig("chocolate_pear_tomato", false, Foods.BEETROOT, DoubleCropBlock::new));
        add(new CropConfig("yellow_pear_tomato", false, Foods.BEETROOT, DoubleCropBlock::new));
        add(new CropConfig("sungold_tomato", false, Foods.BEETROOT, DoubleCropBlock::new));
        add(new CropConfig("sea_buckthorn", false, Foods.BEETROOT, DoubleCropBlock::new));
        add(new CropConfig("konjac", false, Foods.BEETROOT, DoubleCropBlock::new));
        add(new CropConfig("okra", false, Foods.BEETROOT, DoubleCropBlock::new));
        add(new CropConfig("asparagus", false, Foods.BEETROOT, DoubleCropBlock::new));
        add(new CropConfig("artichoke", false, Foods.BEETROOT, DoubleCropBlock::new));
        add(new CropConfig("malanga", false, Foods.BEETROOT, DoubleCropBlock::new));
        add(new CropConfig("yam", false, Foods.BEETROOT, DoubleCropBlock::new));
        // Water grown plants
        add(new CropConfig("water_chestnut", false, null, WaterCropBlock::new));
        add(new CropConfig("watercress", false, null, WaterCropBlock::new)); // spawns in rivers
        add(new CropConfig("water_caltrop", false, null, WaterCropBlock::new));

//        add(new CropConfig("prickly_pear", false, Foods.BEETROOT)); // TODO it's a cactus fruit, remove from crop list
    }};
    public static List<CropConfig> TRELLIS = new ArrayList<>() {{
        add(new CropConfig("kiwi", false, Foods.BEETROOT, TrellisLeafBlock::new));
        add(new CropConfig("goji_berry", false, BERRY_FOOD, TrellisLeafBlock::new));
    }};
    public static List<CropConfig> VERTICAL_TRELLIS = new ArrayList<>() {{
        add(new CropConfig("butternut_squash", false, Foods.BEETROOT, VerticalTrellisLeafBlock::new));
        add(new CropConfig("spoon_gourd", false, Foods.BEETROOT, VerticalTrellisLeafBlock::new));
        add(new CropConfig("luffa", false, Foods.MELON_SLICE, VerticalTrellisLeafBlock::new));
        add(new CropConfig("cucumber", false, Foods.MELON_SLICE, VerticalTrellisLeafBlock::new));
        add(new CropConfig("vanilla", true, null, VerticalTrellisLeafBlock::new));
        add(new CropConfig("zucchini", false, Foods.MELON_SLICE, VerticalTrellisLeafBlock::new));
        add(new CropConfig("akebia", false, Foods.APPLE, VerticalTrellisLeafBlock::new));
        add(new CropConfig("sarsaparilla", true, null, VerticalTrellisLeafBlock::new));
    }};
    public static List<CropConfig> GRAPES = new ArrayList<>() {{
        add(new CropConfig("red_grape", true, Foods.SWEET_BERRIES, VineLeafBlock::new));
        add(new CropConfig("green_grape", true, Foods.SWEET_BERRIES, VineLeafBlock::new));
        add(new CropConfig("concord_grape", true, Foods.SWEET_BERRIES, VineLeafBlock::new));
        add(new CropConfig("cotton_candy_grape", true, Foods.SWEET_BERRIES, VineLeafBlock::new));
    }};
    public static List<CropConfig> STEMS = new ArrayList<>() {{
        add(new CropConfig("cantaloupe", true, Foods.MELON_SLICE));
        add(new CropConfig("honeydew_melon", true, Foods.MELON_SLICE));
    }};
    public static List<CropConfig> HERBS = new ArrayList<>()
    {{
        add(new CropConfig("basil", true, null, HerbBlock::new)); // missing texture
        add(new CropConfig("chives", true, null, HerbBlock::new));
        add(new CropConfig("coriander", false, null, HerbBlock::new));
        add(new CropConfig("cumin", false, null, HerbBlock::new));
        add(new CropConfig("dill", true, null, HerbBlock::new));
        add(new CropConfig("fat_hen", true, null, HerbBlock::new));
        add(new CropConfig("fenugreek", true, null, HerbBlock::new));
        add(new CropConfig("spearmint", true, null, HerbBlock::new));
        add(new CropConfig("peppermint", true, null, HerbBlock::new));
        add(new CropConfig("watermint", true, null, HerbBlock::new));
        add(new CropConfig("lemon_balm", true, null, HerbBlock::new));
        add(new CropConfig("lemongrass", true, null, HerbBlock::new));
        add(new CropConfig("oregano", true, null, HerbBlock::new));
        add(new CropConfig("parsley", true, null, HerbBlock::new));
        add(new CropConfig("rosemary", true, null, HerbBlock::new));
        add(new CropConfig("sage", true, null, HerbBlock::new));
        add(new CropConfig("thyme", true, null, HerbBlock::new));
        add(new CropConfig("ostrich_fiddlehead", true, null, HerbBlock::new)); // TODO worldgen
    }};
    static final FoodProperties BERRY_FOOD = (new FoodProperties.Builder()).alwaysEdible().fast().nutrition(1).saturationModifier(0.1F).build();
    public static List<CropConfig> BERRIES = new ArrayList<>()
    {{
        add(new CropConfig("kadsura", false, BERRY_FOOD, BerryBushBlock::new));
        add(new CropConfig("blackberry", false, BERRY_FOOD, BerryBushBlock::new));
        add(new CropConfig("blackcurrant", false, BERRY_FOOD, BerryBushBlock::new));
        add(new CropConfig("blueberry", false, BERRY_FOOD, BerryBushBlock::new));
        add(new CropConfig("boysenberry", false, BERRY_FOOD, BerryBushBlock::new)); // breed between raspberry and blackberry
        add(new CropConfig("cloudberry", false, BERRY_FOOD, BerryBushBlock::new));
        add(new CropConfig("cranberry", false, BERRY_FOOD, BerryBushBlock::new));
        add(new CropConfig("golden_raspberry", false, BERRY_FOOD, BerryBushBlock::new));
        add(new CropConfig("gooseberry", false, BERRY_FOOD, BerryBushBlock::new));
        add(new CropConfig("lingoberry", false, BERRY_FOOD, BerryBushBlock::new));
        add(new CropConfig("miracle_berry", false, BERRY_FOOD, BerryBushBlock::new));
        add(new CropConfig("raspberry", false, BERRY_FOOD, BerryBushBlock::new));
        add(new CropConfig("redcurrant", false, BERRY_FOOD, BerryBushBlock::new));
    }};

    public static final DeferredHolder<Block, Block> BROWN_MUSHROOM_GROWTH = registerBlock("brown_mushroom_growth", () -> new MushroomGrowthBlock(new CropConfig("brown_mushroom", false, null), BlockBehaviour.Properties.ofFullCopy(Blocks.BROWN_MUSHROOM).replaceable().dynamicShape()), false);
    public static final DeferredHolder<Block, Block> RED_MUSHROOM_GROWTH = registerBlock("red_mushroom_growth", () -> new MushroomGrowthBlock(new CropConfig("red_mushroom", false, null), BlockBehaviour.Properties.ofFullCopy(Blocks.RED_MUSHROOM).replaceable().dynamicShape()), false);
    public static final DeferredHolder<Block, Block> CRIMSON_FUNGUS_GROWTH = registerBlock("crimson_fungus_growth", () -> new MushroomGrowthBlock(new CropConfig("crimson_fungus", false, null), BlockBehaviour.Properties.ofFullCopy(Blocks.CRIMSON_FUNGUS).replaceable().dynamicShape()), false);
    public static final DeferredHolder<Block, Block> WARPED_FUNGUS_GROWTH = registerBlock("warped_fungus_growth", () -> new MushroomGrowthBlock(new CropConfig("warped_fungus", false, null), BlockBehaviour.Properties.ofFullCopy(Blocks.WARPED_FUNGUS).replaceable().dynamicShape()), false);
    public static List<CropConfig> SHROOMS = new ArrayList<>()
    {{
        add(new CropConfig("black_truffle", false, null, MushroomGrowthBlock::new));
        add(new CropConfig("chanterelle", false, null, MushroomGrowthBlock::new));
        add(new CropConfig("laetiporus", false, null, MushroomGrowthBlock::new));
        add(new CropConfig("lions_mane", false, null, MushroomGrowthBlock::new));
        add(new CropConfig("morel", false, null, MushroomGrowthBlock::new));
        add(new CropConfig("oyster_mushroom", false, null, MushroomGrowthBlock::new));
        add(new CropConfig("porcini", false, null, MushroomGrowthBlock::new));
        add(new CropConfig("portobello", false, null, MushroomGrowthBlock::new));
        add(new CropConfig("shiitake", false, null, MushroomGrowthBlock::new));
    }};
    public static List<FishConfig> FISHIES = new ArrayList<>()
    {{
//        add(new FishConfig("anchovy", null, null, false, Foods.SALMON, Foods.COOKED_SALMON));
//        add(new FishConfig("eel", Eel::new, CodRenderer::new, false, Foods.SALMON, Foods.COOKED_SALMON));
//        add(new FishConfig("crab", Crab::new, CodRenderer::new, false, Foods.SALMON, Foods.COOKED_SALMON));
//        add(new FishConfig("lobster", Lobster::new, CodRenderer::new, false, Foods.SALMON, Foods.COOKED_SALMON));
//        add(new FishConfig("oyster", null, null, true, Foods.SALMON, Foods.COOKED_SALMON));
//        add(new FishConfig("clam", null, null, true, Foods.SALMON, Foods.COOKED_SALMON));
//        add(new FishConfig("mussel", null, null, true, Foods.SALMON, Foods.COOKED_SALMON));
//        add(new FishConfig("sea_urchin", null, null, true, Foods.SALMON, null));
//        add(new FishConfig("carp", Carp::new, CodRenderer::new, false, Foods.SALMON, Foods.COOKED_SALMON));
//        add(new FishConfig("koi", Koi::new, CodRenderer::new, false, Foods.SALMON, Foods.COOKED_SALMON));
//        add(new FishConfig("shrimp", null, null, false, Foods.SALMON, Foods.COOKED_SALMON));
//        add(new FishConfig("walleye", Walleye::new, WalleyeRenderer::new, false, Foods.SALMON, Foods.COOKED_SALMON));
//        add(new FishConfig("sturgeon", Tuna::new, CodRenderer::new, false, Foods.SALMON, Foods.COOKED_SALMON));
//        add(new FishConfig("tuna", Tuna::new, CodRenderer::new, false, Foods.SALMON, Foods.COOKED_SALMON));
    }};

    public static List<ResourceLocation> CRATED_CROPS = new ArrayList<>()
    {{
//        add(ResourceLocation.withDefaultNamespace("baked_potato"));
//        add(ResourceLocation.withDefaultNamespace("apple"));
//        add(ResourceLocation.withDefaultNamespace("chorus_fruit"));
//        add(ResourceLocation.withDefaultNamespace("sweet_berries"));
//        add(ResourceLocation.withDefaultNamespace("glow_berries"));
//        add(ResourceLocation.withDefaultNamespace("beef"));
//        add(ResourceLocation.withDefaultNamespace("cooked_beef"));
//        add(ResourceLocation.withDefaultNamespace("chicken"));
//        add(ResourceLocation.withDefaultNamespace("cooked_chicken"));
//        add(ResourceLocation.withDefaultNamespace("mutton"));
//        add(ResourceLocation.withDefaultNamespace("cooked_mutton"));
//        add(ResourceLocation.withDefaultNamespace("porkchop"));
//        add(ResourceLocation.withDefaultNamespace("cooked_porkchop"));
//        add(ResourceLocation.withDefaultNamespace("rabbit"));
//        add(ResourceLocation.withDefaultNamespace("cooked_rabbit"));
//        add(ResourceLocation.withDefaultNamespace("salmon"));
//        add(ResourceLocation.withDefaultNamespace("cooked_salmon"));
//        add(ResourceLocation.withDefaultNamespace("cod"));
//        add(ResourceLocation.withDefaultNamespace("cooked_cod"));
//        add(ResourceLocation.withDefaultNamespace("tropical_fish"));
//        add(ResourceLocation.withDefaultNamespace("pufferfish"));
//        add(ResourceLocation.withDefaultNamespace("cocoa_beans"));
//        add(ResourceLocation.withDefaultNamespace("egg"));
//        add(ResourceLocation.withDefaultNamespace("turtle_egg"));
//        add(ResourceLocation.withDefaultNamespace("sniffer_egg"));
//        add(ResourceLocation.withDefaultNamespace("golden_apple"));
//        add(ResourceLocation.withDefaultNamespace("golden_carrot"));
//        add(ResourceLocation.withDefaultNamespace("ink_sac"));
    }};
    public static List<ResourceLocation> SEED_BAGS = new ArrayList<>()
    {{
        add(ResourceLocation.withDefaultNamespace("pumpkin_seeds"));
        add(ResourceLocation.withDefaultNamespace("melon_seeds"));
        add(ResourceLocation.withDefaultNamespace("torchflower_seeds"));
        add(ResourceLocation.withDefaultNamespace("pitcher_pod"));
    }};
    public static List<FlowerConfig> FLOWERS = new ArrayList<>() {{
        // vanilla copies
        add(new FlowerConfig("rose_bush", 0xffff4540, true, false));
        add(new FlowerConfig("lilac", 0xffde93f1, true, false));
        add(new FlowerConfig("peony", 0xffde93f1, true, false));
        add(new FlowerConfig("poppy", 0xffff4540, false, false));
        add(new FlowerConfig("cornflower", 0xff728ff1, false, false));
        add(new FlowerConfig("alium", 0xffe8cffe, false, false));
        add(new FlowerConfig("azure_bluet", 0xfff7f7f7, false, false));
        add(new FlowerConfig("blue_orchid", 0xff2fcefd, false, false));
        add(new FlowerConfig("dandelion", 0xffffec4f, false, false));
        add(new FlowerConfig("lily_of_the_valley", 0xffffffff, false, false));
        add(new FlowerConfig("oxeye_daisy", 0xffffffff, false, false));
        add(new FlowerConfig("tulip", 0xffffffff, false, false));
        add(new FlowerConfig("torchflower", 0xfffce257, false, false));
        add(new FlowerConfig("wither_rose", 0xff42352e, false, false));
        // sunflower, spore blossom, pitcher crop

        // new flowers
        add(new FlowerConfig("amaryllis", 0xffa02c1a, false, false));
        add(new FlowerConfig("anemone", 0xfff0f0ea, false, false));
        add(new FlowerConfig("balloon_flower", 0xff6a5fc4, false, false));
        add(new FlowerConfig("black_bearded_iris", 0xff3c4246, false, false));
        add(new FlowerConfig("cattail", 0xff765c3d, true, false));
        add(new FlowerConfig("cape_leadwort", 0xff9ec9e3, true, true));
        add(new FlowerConfig("chrysanthemum", 0xffdf42a0, false, false));

        add(new FlowerConfig("dahlia", 0xff2e2e2e, false, false));
        add(new FlowerConfig("great_lobelia", 0xff566ddb, false, false));
        add(new FlowerConfig("hellebore", 0xff3e5252, false, false));
        add(new FlowerConfig("himalayan_poppy", 0xff5cd4ee, true, false));
        add(new FlowerConfig("hydrangea", 0xfffd9db6, true, true));
        add(new FlowerConfig("motherwort", 0xffbd6c79, true, false));

        add(new FlowerConfig("sea_holly", 0xff4055c5, false, false));
        add(new FlowerConfig("skullcap", 0xff7c4a9f, false, false));
        add(new FlowerConfig("stinging_nettle", 0xff4d752f, true, false));
        add(new FlowerConfig("valerian_root", 0xfffbecf5, true, false));

        add(new FlowerConfig("zinnia", 0xffcaa000, true, false));

        // when adding new flowers add to ModTags.Blocks.CAN_SPAWN_FROM_BONEMEAL
    }};
    public static List<FlowerConfig> VINES = new ArrayList<>() {{
        add(new FlowerConfig("blue_jade_vine", 0xff75dbd0, false, false));
        add(new FlowerConfig("butterfly_pea", 0xff4259ce, false, false));
        add(new FlowerConfig("morning_glory", 0xff7a3493, false, false));
    }};

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<FencedVerticalCropBlockEntity>> FENCED_VERTICAL_CROP_BLOCK_ENTITY;
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<FencedLeafBlockEntity>> FENCED_LEAVES_BLOCK_ENTITY;
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<FencedStemBlockEntity>> FENCED_STEM_BLOCK_ENTITY;
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<SimpleCropBlockEntity>> CROP_BLOCK_ENTITY;
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<ColorfulFlowerBlockEntity>> FLOWER_BLOCK_ENTITY;
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<ColorfulFlowerPotBlockEntity>> FLOWER_POT_BLOCK_ENTITY;
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<MushroomGrowthCropBlockEntity>> MUSHROOM_GROWTH_BLOCK_ENTITY;

    static Map<String, DeferredHolder<Block, Block>> registeredBlocks = new HashMap<>();
    public static void init() {
        VANILLA_CROPS.forEach(crop -> {
            var cropBlock = registerBlock(crop.name(), () -> crop.supplier().create(crop, BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT)), false);
            registeredBlocks.put(crop.name(), cropBlock);
            if (crop.food() != null) {
                CRATED_CROPS.add(ResourceLocation.withDefaultNamespace(crop.name()));
            }
            if (crop.hasSeed()) {
                SEED_BAGS.add(ResourceLocation.withDefaultNamespace(crop.name() + "_seeds"));
            }
        });
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
        GRAPES.forEach(crop -> {
            registeredBlocks.put(crop.name() + "_leaves", registerBlock(crop.name() + "_leaves", () -> crop.supplier().create(crop, BlockBehaviour.Properties.ofFullCopy(Blocks.CHERRY_LEAVES).offsetType(BlockBehaviour.OffsetType.XYZ).dynamicShape()), false));
            registeredBlocks.put(crop.name(), registerPlantableCrop(crop, () -> new FencedStemBlock(crop, BlockBehaviour.Properties.ofFullCopy(Blocks.MELON_STEM)), StemGrowinSeedItem::new));
            registeredBlocks.put("attached_" + crop.name() + "_stem", registerBlock("attached_" + crop.name() + "_stem", () -> new AttachedFencedStemBlock(crop, BlockBehaviour.Properties.ofFullCopy(Blocks.ATTACHED_MELON_STEM)), false));
            if (crop.food() != null) {
                CRATED_CROPS.add(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name()));
            }
        });
        STEMS.forEach(crop -> {
            registerItem(crop.name() + "_slice", crop.food());
            registerItem(crop.name() + "_seeds", () -> new CropBlockItem(registeredBlocks.get(crop.name() + "_stem").get(), new Item.Properties()));
            registeredBlocks.put(crop.name(), registerBlock(crop.name(), () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.MELON)), true));
            registeredBlocks.put(crop.name() + "_stem", registerBlock(crop.name() + "_stem", () -> new StemBlock(
                    ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name())),
                    ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "attached_" + crop.name() + "_stem")),
                    ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name() + "_seeds")),
                    BlockBehaviour.Properties.ofFullCopy(Blocks.MELON_STEM)), false));
            registeredBlocks.put("attached_" + crop.name() + "_stem", registerBlock("attached_" + crop.name() + "_stem", () -> new AttachedStemBlock(
                    ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name() + "_stem")),
                    ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name())),
                    ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name() + "_seeds")),
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
        SHROOMS.forEach(crop -> {
            registerItem(crop.name(), () -> new CropBlockItem(registeredBlocks.get(crop.name() + "_growth").get(), new Item.Properties()));
//            registeredBlocks.put(crop.name(), registerBlock(crop.name(), () -> crop.supplier().create(crop, BlockBehaviour.Properties.ofFullCopy(Blocks.BROWN_MUSHROOM).replaceable().dynamicShape()), false));
            registeredBlocks.put(crop.name() + "_growth", registerBlock(crop.name() + "_growth", () -> crop.supplier().create(crop, BlockBehaviour.Properties.ofFullCopy(Blocks.BROWN_MUSHROOM).replaceable().dynamicShape()), false));

//            if (crop.food() != null) {
//                CRATED_CROPS.add(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name()));
//            } TODO crated shrooms
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
        FLOWERS.forEach(flowerConfig -> {
            var flower = registerBlock(flowerConfig.name(), () -> flowerConfig.isDouble() ? new ColorfulTallFlowerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.ROSE_BUSH), flowerConfig.baseColor()) : new ColorfulFlowerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY), flowerConfig.baseColor()), true);
            registeredBlocks.put(flowerConfig.name(), flower);
            if (!flowerConfig.isDouble()) {
                var pottedFlower = registerBlock("potted_" + flowerConfig.name(), () -> new ColorfulFlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, flower, BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_OAK_SAPLING)), false);
                registeredBlocks.put("potted_" + flowerConfig.name(), pottedFlower);
                ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, flowerConfig.name()), pottedFlower);
            }
        });
        VINES.forEach(flowerConfig -> {
            var flower = registerBlock(flowerConfig.name(), () -> new ColorfulVineBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.VINE), flowerConfig.baseColor()), true);
            registeredBlocks.put(flowerConfig.name(), flower);
        });
        FENCED_VERTICAL_CROP_BLOCK_ENTITY = ProductiveFarming.BLOCK_ENTITIES.register("fenced_crop", () -> BlockEntityType.Builder.of(FencedVerticalCropBlockEntity::new,
                VERTICAL_TRELLIS.stream().map(cropConfig -> registeredBlocks.get(cropConfig.name()).get()).toList().toArray(new Block[0])
        ).build(null));
        FENCED_LEAVES_BLOCK_ENTITY = ProductiveFarming.BLOCK_ENTITIES.register("fenced_leaves", () -> BlockEntityType.Builder.of(FencedLeafBlockEntity::new,
                Stream.concat(TRELLIS.stream(), GRAPES.stream()).map(cropConfig -> registeredBlocks.get(cropConfig.name() + "_leaves").get()).toList().toArray(new Block[0])
        ).build(null));
        FENCED_STEM_BLOCK_ENTITY = ProductiveFarming.BLOCK_ENTITIES.register("fenced_stem", () -> BlockEntityType.Builder.of(FencedStemBlockEntity::new,
                Stream.concat(TRELLIS.stream(), GRAPES.stream()).map(cropConfig -> List.of(registeredBlocks.get("attached_" + cropConfig.name() + "_stem").get(), registeredBlocks.get(cropConfig.name()).get())).flatMap(List::stream).toList().toArray(new Block[0])
        ).build(null));

        CROP_BLOCK_ENTITY = ProductiveFarming.BLOCK_ENTITIES.register("crop", () -> BlockEntityType.Builder.of(SimpleCropBlockEntity::new,
               Stream.concat(CROPS.stream(), Stream.concat(VANILLA_CROPS.stream(), Stream.concat(HERBS.stream(), BERRIES.stream()))).map(cropConfig -> List.of(registeredBlocks.get(cropConfig.name()).get(), registeredBlocks.get(cropConfig.name()).get())).flatMap(List::stream).toList().toArray(new Block[0])
        ).build(null));
        FLOWER_BLOCK_ENTITY = ProductiveFarming.BLOCK_ENTITIES.register("flower", () -> BlockEntityType.Builder.of(ColorfulFlowerBlockEntity::new,
                getFlowers()
        ).build(null));
        FLOWER_POT_BLOCK_ENTITY = ProductiveFarming.BLOCK_ENTITIES.register("potted_flower", () -> BlockEntityType.Builder.of(ColorfulFlowerPotBlockEntity::new,
                getFlowerPots()
        ).build(null));
        MUSHROOM_GROWTH_BLOCK_ENTITY = ProductiveFarming.BLOCK_ENTITIES.register("mushroom_growth", () -> BlockEntityType.Builder.of(MushroomGrowthCropBlockEntity::new,
                getShrooms()
        ).build(null));
    }

    public static Block[] getAllCrops() {
        return Stream.concat(GRAPES.stream(), Stream.concat(VERTICAL_TRELLIS.stream(), Stream.concat(TRELLIS.stream(), Stream.concat(BERRIES.stream(), Stream.concat(CROPS.stream(), Stream.concat(VANILLA_CROPS.stream(), HERBS.stream()))))))
                .map(cropConfig -> List.of(registeredBlocks.get(cropConfig.name()).get(), registeredBlocks.get(cropConfig.name()).get())).flatMap(List::stream).distinct().toList().toArray(new Block[0]);
    }

    public static Block[] getShrooms() {
        var list = SHROOMS.stream().map(cropConfig -> registeredBlocks.get(cropConfig.name() + "_growth").get()).collect(Collectors.toCollection(ArrayList::new));
        list.addAll(List.of(BROWN_MUSHROOM_GROWTH.get(), RED_MUSHROOM_GROWTH.get(), CRIMSON_FUNGUS_GROWTH.get(), WARPED_FUNGUS_GROWTH.get()));
        return list.toArray(new Block[0]);
    }

    public static Block[] getFlowers() {
        return Stream.concat(FLOWERS.stream(), VINES.stream())
                .map(cropConfig -> registeredBlocks.get(cropConfig.name()).get()).toList().toArray(new Block[0]);
    }

    public static Block[] getFlowerPots() {
        return FLOWERS.stream().filter(flowerConfig -> !flowerConfig.isDouble())
                .map(cropConfig -> registeredBlocks.get("potted_" + cropConfig.name()).get()).toList().toArray(new Block[0]);
    }

    public static final DataMapType<Item, CropTrait> CROP_TRAITS = DataMapType.builder(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "crop_traits"), Registries.ITEM, CropTrait.CODEC).synced(CropTrait.CODEC, false).build();

    // Non-crop items
    public static final DeferredHolder<Item, Item> POLLEN = registerItem("pollen", () -> new PollenItem(new Item.Properties()));
    public static final DeferredHolder<Item, Item> DRIED_LUFFA = registerItem("dried_luffa");
    public static final DeferredHolder<Item, Item> DRIED_TOBACCO = registerItem("dried_tobacco");
    public static final DeferredHolder<Item, Item> BLACK_TEA = registerItem("black_tea");
    public static final DeferredHolder<Item, Item> CORN_COB_PIPE = registerItem("corn_cob_pipe", () -> new CornPipeItem(new Item.Properties().stacksTo(1).durability(200)));
//    public static final DeferredHolder<Item, Item> HOTDOG_ARMOR = registerItem("hotdog_armor", () -> new AnimalArmorItem(
//            ArmorMaterials.ARMADILLO, AnimalArmorItem.BodyType.CANINE, true, new Item.Properties().durability(ArmorItem.Type.BODY.getDurability(4))
//    ));

    // Machines
//    public static final DeferredHolder<Block, Block> FISH_TRAP = registerBlock("fish_trap", () -> new FishTrapBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL)), true);
//    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FishTrapBlockEntity>> FISH_TRAP_BLOCK_ENTITY = ProductiveFarming.BLOCK_ENTITIES.register("fish_trap", () -> BlockEntityType.Builder.of(FishTrapBlockEntity::new, FISH_TRAP.get()).build(null));
    public static final DeferredHolder<Block, Block> FARM_HATCH = registerBlock("farm_hatch", () -> new FarmHatch(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)), true);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FarmHatchBlockEntity>> FARM_HATCH_BLOCK_ENTITY = ProductiveFarming.BLOCK_ENTITIES.register("farm_hatch", () -> BlockEntityType.Builder.of(FarmHatchBlockEntity::new, FARM_HATCH.get()).build(null));
    public static final DeferredHolder<Block, Block> FARM_CONTROLLER = registerBlock("farm_controller", () -> new FarmControllerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS).noOcclusion()), true);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FarmControllerBlockEntity>> FARM_CONTROLLER_BLOCK_ENTITY = ProductiveFarming.BLOCK_ENTITIES.register("farm_controller", () -> BlockEntityType.Builder.of(FarmControllerBlockEntity::new, FARM_CONTROLLER.get()).build(null));
    public static final DeferredHolder<Block, Block> FEEDING_TROUGH = registerBlock("feeding_trough", () -> new FeedingTroughBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON).noOcclusion()), true);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FeedingTroughBlockEntity>> FEEDING_TROUGH_BLOCK_ENTITY = ProductiveFarming.BLOCK_ENTITIES.register("feeding_trough", () -> BlockEntityType.Builder.of(FeedingTroughBlockEntity::new, FEEDING_TROUGH.get()).build(null));
    public static final DeferredHolder<Block, Block> WATERING_TROUGH = registerBlock("watering_trough", () -> new WateringTroughBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON).noOcclusion()), true);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WateringTroughBlockEntity>> WATERING_TROUGH_BLOCK_ENTITY = ProductiveFarming.BLOCK_ENTITIES.register("watering_trough", () -> BlockEntityType.Builder.of(WateringTroughBlockEntity::new, WATERING_TROUGH.get()).build(null));
//    public static final DeferredHolder<Block, Block> SALT_LICK = registerBlock("salt_lick", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()), true);
//    public static final DeferredHolder<Block, Block> CHILD_SEPARATOR = registerBlock("child_separator", () -> new ChildSeparatorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()), true);
//    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ChildSeparatorBlockEntity>> CHILD_SEPARATOR_BLOCK_ENTITY = ProductiveFarming.BLOCK_ENTITIES.register("child_separator", () -> BlockEntityType.Builder.of(ChildSeparatorBlockEntity::new, CHILD_SEPARATOR.get()).build(null));
//    public static final DeferredHolder<Block, Block> SLAUGHTER_STATION = registerBlock("slaughter_station", () -> new SlaughterStationBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()), true);
//    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SlaughterStationBlockEntity>> SLAUGHTER_STATION_BLOCK_ENTITY = ProductiveFarming.BLOCK_ENTITIES.register("slaughter_station", () -> BlockEntityType.Builder.of(SlaughterStationBlockEntity::new, SLAUGHTER_STATION.get()).build(null));
    // TODO drying rack with drying recipe

    public static final DeferredHolder<MenuType<?>, MenuType<FarmControllerContainer>> FARM_CONTROLLER_MENU = ProductiveFarming.CONTAINER_TYPES.register("farm_controller", () ->
            IMenuTypeExtension.create(FarmControllerContainer::new)
    );
    public static final DeferredHolder<MenuType<?>, MenuType<FeedingTroughContainer>> FEEDING_TROUGH_MENU = ProductiveFarming.CONTAINER_TYPES.register("feeding_trough", () ->
            IMenuTypeExtension.create(FeedingTroughContainer::new)
    );

    // POIs
//    public static DeferredHolder<PoiType, PoiType> SALT_LICK_POI = ProductiveFarming.POI_TYPES.register("salt_lick", () -> {
//        Set<BlockState> blockStates = new HashSet<>(SALT_LICK.get().getStateDefinition().getPossibleStates());
//        return new PoiType(blockStates, 1, 1);
//    });

    // Recipes
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> CROP_FRUITING = ProductiveFarming.RECIPE_SERIALIZERS.register("crop_fruiting", CropFruitingRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<CropFruitingRecipe>> CROP_FRUITING_TYPE = ProductiveFarming.RECIPE_TYPES.register("crop_fruiting", () -> new RecipeType<>() {});
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> CROP_MUTATION = ProductiveFarming.RECIPE_SERIALIZERS.register("crop_mutation", CropMutationRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<CropMutationRecipe>> CROP_MUTATION_TYPE = ProductiveFarming.RECIPE_TYPES.register("crop_mutation", () -> new RecipeType<>() {});
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> FLOWER_DYE_CRAFTING = ProductiveFarming.RECIPE_SERIALIZERS.register("flower_dye_crafting", FlowerDyeCraftingRecipe.Serializer::new);

    // Fluids
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> NUTRIENT_WATER = registerFluid("nutrient_water");

    // Features
//    public static final DeferredHolder<Feature<?>, Feature<CountConfiguration>> CLAM_FEATURE = ProductiveFarming.FEATURES.register("clam", () -> new SeaFloorFeature(CountConfiguration.CODEC, () -> BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "clam"))));
//    public static final DeferredHolder<Feature<?>, Feature<CountConfiguration>> OYSTER_FEATURE = ProductiveFarming.FEATURES.register("oyster", () -> new SeaFloorFeature(CountConfiguration.CODEC, () -> BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "oyster"))));
//    public static final DeferredHolder<Feature<?>, Feature<CountConfiguration>> MUSSEL_FEATURE = ProductiveFarming.FEATURES.register("mussel", () -> new SeaFloorFeature(CountConfiguration.CODEC, () -> BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "mussel"))));

    public static final ResourceKey<CreativeModeTab> TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, ProductiveFarming.MODID));
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = ProductiveFarming.CREATIVE_MODE_TABS.register(ProductiveFarming.MODID, () -> {
        return CreativeModeTab.builder()
                .icon(() -> new ItemStack(FEEDING_TROUGH.get()))
                .title(Component.translatable("itemGroup." + ProductiveFarming.MODID))
                .build();
    });

    public static DeferredHolder<Block, Block> registerPlantableCrop(CropConfig crop, Supplier<Block> supplier) {
        return registerPlantableCrop(crop, supplier, CropBlockItem::new);
    }

    public static DeferredHolder<Block, Block> registerPlantableCrop(CropConfig crop, Supplier<Block> supplier, CropItemSupplier<BlockItem> item) {
        var cropBlock = registerBlock(crop.name(), supplier, false);
        if (crop.hasSeed()) {
            registerItem(crop.name() + "_seeds", () -> item.create(cropBlock.get(), new Item.Properties()));
            if (crop.food() != null) {
                registerCropItem(crop.name(), crop.food());
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

    public static DeferredHolder<Item, Item> registerCropItem(String name, FoodProperties food) {
        return registerItem(name, () -> new CropItem(new Item.Properties().food(food)));
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

    public static DeferredHolder<Fluid, BaseFlowingFluid.Source> registerFluid(String name) {
        // fluid type
        var TYPE = ProductiveFarming.FLUID_TYPES.register(name, () -> new FluidType(FluidType.Properties.create()
                .descriptionId("block." + ProductiveFarming.MODID + "." + name)
                .fallDistanceModifier(0F)
                .canExtinguish(true)
                .canConvertToSource(false)
                .supportsBoating(true)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)
                .canHydrate(true)));
        // fluid
        var FLUID = ProductiveFarming.FLUIDS.register(name, () -> new BaseFlowingFluid.Source(makeFluidProperties(TYPE, name)));
        // flowing fluid
        ProductiveFarming.FLUIDS.register(String.format("flowing_%s", name), () -> new BaseFlowingFluid.Flowing(makeFluidProperties(TYPE, name)));
        // fluid bucket
        registerItem(String.format("%s_bucket", name), () -> new BucketItem(FLUID.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
        // fluid block
        registerBlock(name, () -> new LiquidBlock(FLUID.get(), Block.Properties.of()
                .strength(100.0F)
                .noCollission()
                .liquid()
                .replaceable()
                .pushReaction(PushReaction.DESTROY)
                .noLootTable()
                .sound(SoundType.EMPTY)
        ), false);

        return FLUID;
    }

    static private BaseFlowingFluid.Properties makeFluidProperties(Supplier<? extends FluidType> fluidType, String name) {
        return new BaseFlowingFluid.Properties(
                fluidType,
                DeferredHolder.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, name)),
                DeferredHolder.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, String.format("flowing_%s", name)))
        )
                .bucket(DeferredHolder.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, String.format("%s_bucket", name))))
                .block(DeferredHolder.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, name)))
                .tickRate(30)
                .slopeFindDistance(4)
                .levelDecreasePerBlock(2);
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
