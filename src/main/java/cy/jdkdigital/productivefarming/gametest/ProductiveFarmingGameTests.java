package cy.jdkdigital.productivefarming.gametest;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.common.attachment.CropTraitState;
import cy.jdkdigital.productivefarming.common.block.ProductiveCropBlock;
import cy.jdkdigital.productivefarming.common.block.entity.ColorfulFlowerBlockEntity;
import cy.jdkdigital.productivefarming.common.block.entity.CropBlockEntity;
import cy.jdkdigital.productivefarming.common.block.entity.FarmControllerBlockEntity;
import cy.jdkdigital.productivefarming.common.block.entity.FeedingTroughBlockEntity;
import cy.jdkdigital.productivefarming.common.block.entity.WateringTroughBlockEntity;
import cy.jdkdigital.productivefarming.integrations.agritech.AgriTechStatsHelper;
import cy.jdkdigital.productivefarming.registry.FarmingDataComponents;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import cy.jdkdigital.productivefarming.util.ExternalCropStats;
import cy.jdkdigital.productivefarming.util.FarmUtil;
import cy.jdkdigital.productivefarming.util.TraitsHelper;
import cy.jdkdigital.productivelib.container.ManualSlotItemHandler;
import cy.jdkdigital.productivefarming.util.RecipeHelper;
import cy.jdkdigital.productivelib.event.BeeReleaseEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.animal.fish.AbstractFish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;
import net.neoforged.neoforge.event.level.block.CropGrowEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * In-game tests for the farm multiblock (productivelib {@code MultiBlockDetector} +
 * {@code ValueIOSerializable} multiblock data, the {@code FarmControllerBlockEntity} ResourceHandler
 * inventory, and the tick-driven harvest path).
 *
 * <p>Each body is registered into {@link TestFunctions} at class-init (before {@code Bootstrap}),
 * and backed by the {@code test_instance}/{@code test_environment} JSON emitted by
 * {@link TestEntriesProvider}. Tests run inside the {@code productivefarming:empty_9x9} structure;
 * every block is placed at runtime via {@code helper.setBlock}.
 */
public class ProductiveFarmingGameTests
{
    private static final int DEFAULT_MAX_TICKS = 100;
    public static final Map<String, Integer> MAX_TICKS = new LinkedHashMap<>();

    static {
        register("farm_multiblock_forms", ProductiveFarmingGameTests::testFarmForms);
        register("farm_multiblock_rejects_incomplete_ring", ProductiveFarmingGameTests::testFarmRejectsIncompleteRing);
        register("farm_multiblock_harvests_crop", ProductiveFarmingGameTests::testFarmHarvestsCrop, 650);
        register("crop_stats_increase_on_growth", ProductiveFarmingGameTests::testCropStatsIncrease);
        register("stat_increase_chance_falls_back_to_config", ProductiveFarmingGameTests::testStatIncreaseChanceFallback);
        register("mystical_tier_stat_increase_chance", ProductiveFarmingGameTests::testMysticalTierIncreaseChance);
        register("farm_nutrient_water_bonemeals_crops", ProductiveFarmingGameTests::testNutrientWaterBonemeal);
        register("crop_mutation_via_pollination", ProductiveFarmingGameTests::testCropMutation);
        register("grape_mutation_safe_harvest", ProductiveFarmingGameTests::testGrapeMutationSafeHarvest);
        register("vertical_trellis_harvest_drops_stat_seed", ProductiveFarmingGameTests::testVerticalTrellisStatHarvest);
        register("flower_spread_mixes_colors", ProductiveFarmingGameTests::testFlowerSpreadMixesColors);
        register("flower_spread_from_beehive", ProductiveFarmingGameTests::testBeeFlowerSpread);
        register("external_crop_stat_increase", ProductiveFarmingGameTests::testExternalCropStatIncrease);
        register("external_crop_bonemeal_stat_increase", ProductiveFarmingGameTests::testExternalBonemealStatIncrease);
        register("external_crop_random_tick_stat_increase", ProductiveFarmingGameTests::testExternalRandomTickStatIncrease, 200);
        register("external_crop_growth_speed_boost", ProductiveFarmingGameTests::testExternalCropGrowthSpeed);
        register("external_crop_harvest_applies_yield", ProductiveFarmingGameTests::testExternalCropHarvestYield);
        register("external_crop_mutation_harvest", ProductiveFarmingGameTests::testExternalCropMutationHarvest);
        register("external_crop_getdrops_stamps_stats", ProductiveFarmingGameTests::testExternalCropGetDropsStamps);
        register("external_crop_custom_drops_stamps_stats", ProductiveFarmingGameTests::testExternalCropCustomDropsStamps);
        register("external_crop_stamps_baseline_stats", ProductiveFarmingGameTests::testExternalCropStampsBaseline);
        register("external_bonemeal_takeover_ignores_other_fertilizers", ProductiveFarmingGameTests::testBonemealTakeoverIgnoresOtherFertilizers);
        register("external_bonemeal_takeover_consumes_stack", ProductiveFarmingGameTests::testBonemealTakeoverConsumesStack);
        register("feeding_trough_inventory_roundtrip", ProductiveFarmingGameTests::testFeedingTroughInventory);
        register("feeding_trough_breeds_animals", ProductiveFarmingGameTests::testFeedingTroughBreeds);
        register("watering_trough_reduces_breed_cooldown", ProductiveFarmingGameTests::testWateringTroughReducesCooldown);
        register("farm_controller_bucket_fill", ProductiveFarmingGameTests::testFarmControllerBucketFill);
        register("farm_controller_bucket_empty", ProductiveFarmingGameTests::testFarmControllerBucketEmpty);
        register("fish_farm_produces_nutrient_water", ProductiveFarmingGameTests::testFishFarm);
        register("fish_farm_breeds_fish", ProductiveFarmingGameTests::testFishFarmBreeds);
        register("fish_farm_culls_population", ProductiveFarmingGameTests::testFishFarmCulls);
        register("fish_farm_tops_off_tank", ProductiveFarmingGameTests::testFishFarmTopsOffTank);
        register("fish_farm_produces_with_full_inventory", ProductiveFarmingGameTests::testFishFarmProducesWithFullInventory);
        register("fish_farm_holds_when_inventory_full", ProductiveFarmingGameTests::testFishFarmHoldsWhenInventoryFull);
        register("fish_farm_tank_stays_full_with_bonemealable", ProductiveFarmingGameTests::testFishFarmTankStaysFullWithBonemealable);
        register("farm_does_not_drain_on_ungrowable_block", ProductiveFarmingGameTests::testFarmDoesNotDrainOnUngrowableBlock);
        register("agritech_compat_recipes_present", ProductiveFarmingGameTests::testAgriTechRecipesPresent);
        register("agritech_planter_growth_speed", ProductiveFarmingGameTests::testAgriTechGrowthSpeed);
        register("agritech_planter_yield_and_seed_lineage", ProductiveFarmingGameTests::testAgriTechYieldAndLineage);
        register("agritech_planter_input_stat_increase", ProductiveFarmingGameTests::testAgriTechInputStatIncrease);
        register("agritech_planter_initializes_statless_seed", ProductiveFarmingGameTests::testAgriTechInitializesStatlessSeed);
        register("farm_controller_all_slots_insertable", ProductiveFarmingGameTests::testFarmControllerSlotsInsertable);
    }

    private static Identifier rl(String path) {
        return Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, path);
    }

    private static void register(String name, Consumer<GameTestHelper> body) {
        register(name, body, DEFAULT_MAX_TICKS);
    }

    private static void register(String name, Consumer<GameTestHelper> body, int maxTicks) {
        TestFunctions.register(name, body);
        MAX_TICKS.put(name, maxTicks);
    }

    // A minimal farm: a 3×3 ring of farm-wall blocks (minecraft:bricks ∈ #productivefarming:farm_blocks)
    // at WALL_Y, hollow centre, with the controller embedded in the middle of the -Z wall facing out.
    // Mirrors the in-game guide ("minimum size 3x3, corners optional") + the foundry test pattern.
    private static final int WALL_Y = 2;
    private static final BlockPos CONTROLLER_POS = new BlockPos(1, WALL_Y, 0);
    private static final BlockPos INTERIOR_POS = new BlockPos(1, WALL_Y, 1);

    private static void testFarmForms(GameTestHelper helper) {
        buildFarmRing(helper, true);

        // Right-click the controller with an empty hand: useBlock fires FarmControllerBlock.useWithoutItem,
        // which runs detectMultiblock + setMultiBlockData and flips ATTACHED on success.
        helper.useBlock(CONTROLLER_POS);

        BlockState controllerState = helper.getLevel().getBlockState(helper.absolutePos(CONTROLLER_POS));
        if (!controllerState.getValue(BlockStateProperties.ATTACHED)) {
            helper.fail("Controller is not ATTACHED after right-clicking a valid farm ring — it did not form", CONTROLLER_POS);
            return;
        }

        FarmControllerBlockEntity be = helper.getBlockEntity(CONTROLLER_POS, FarmControllerBlockEntity.class);
        if (be.getMultiblockData() == null) {
            helper.fail("Controller ATTACHED but has no multiblock data", CONTROLLER_POS);
            return;
        }
        helper.succeed();
    }

    private static void testFarmRejectsIncompleteRing(GameTestHelper helper) {
        buildFarmRing(helper, false); // leaves a gap in the ring

        helper.useBlock(CONTROLLER_POS);

        BlockState controllerState = helper.getLevel().getBlockState(helper.absolutePos(CONTROLLER_POS));
        if (controllerState.getValue(BlockStateProperties.ATTACHED)) {
            helper.fail("Controller became ATTACHED with a broken (open) ring — an invalid structure formed", CONTROLLER_POS);
            return;
        }
        FarmControllerBlockEntity be = helper.getBlockEntity(CONTROLLER_POS, FarmControllerBlockEntity.class);
        if (be.getMultiblockData() != null) {
            helper.fail("Controller reports multiblock data for an invalid (open) ring", CONTROLLER_POS);
            return;
        }
        helper.succeed();
    }

    private static void testFarmHarvestsCrop(GameTestHelper helper) {
        buildFarmRing(helper, true);
        helper.useBlock(CONTROLLER_POS);

        FarmControllerBlockEntity be = helper.getBlockEntity(CONTROLLER_POS, FarmControllerBlockEntity.class);
        if (be.getMultiblockData() == null) {
            helper.fail("Farm did not form, cannot test harvest", CONTROLLER_POS);
            return;
        }

        // Plant a fully-grown crop in the harvest layer (one above the ring footprint, over the interior).
        Block arrowroot = BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "arrowroot"));
        BlockPos cropPos = INTERIOR_POS.above(); // (1, WALL_Y+1, 1)
        helper.setBlock(cropPos, arrowroot.defaultBlockState().setValue(ProductiveCropBlock.AGE_6, 6));

        // The controller harvests on its tick cycle and rakes the dropped items into its inventory.
        helper.startSequence()
                .thenWaitUntil(() -> {
                    boolean harvested = false;
                    for (int slot = 0; slot < be.inventoryHandler.size(); slot++) {
                        if (!be.inventoryHandler.getStackInSlot(slot).isEmpty()) {
                            harvested = true;
                            break;
                        }
                    }
                    if (!harvested) {
                        throw helper.assertionException(CONTROLLER_POS, "Controller inventory still empty — crop not harvested yet");
                    }
                })
                .thenSucceed();
    }

    // A crop rolls a random trait increase only when it grows to max age, gated by its stat_increase_chance.
    // Grow a crop to max many times and assert at least one trait climbed above 0.
    private static void testCropStatsIncrease(GameTestHelper helper) {
        BlockPos relPos = new BlockPos(2, 2, 2);
        if (!(BuiltInRegistries.BLOCK.getValue(rl("arrowroot")) instanceof ProductiveCropBlock cropBlock)) {
            helper.fail("arrowroot is not a ProductiveCropBlock", relPos);
            return;
        }
        helper.setBlock(relPos, cropBlock.defaultBlockState().setValue(cropBlock.getAgeProperty(), cropBlock.getMaxAge()));
        CropBlockEntity be = helper.getBlockEntity(relPos, CropBlockEntity.class);

        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(relPos);
        BlockState maxState = level.getBlockState(pos);
        for (int i = 0; i < 600 && totalStats(be) == 0; i++) {
            cropBlock.growCrops(level, pos, maxState);
        }
        if (totalStats(be) == 0) {
            helper.fail("crop traits never increased over 600 grow-to-max cycles", relPos);
            return;
        }
        helper.succeed();
    }

    private static int totalStats(CropBlockEntity be) {
        return be.getGrowth() + be.getYield() + be.getResistance() + be.getMutability();
    }

    private static void testMysticalTierIncreaseChance(GameTestHelper helper) {
        Item tier5 = BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("mysticalagriculture", "diamond_seeds"));
        if (tier5 == Items.AIR) {
            helper.succeed();
            return;
        }
        assertChance(helper, Identifier.fromNamespaceAndPath("mysticalagriculture", "diamond_seeds"), 0.02);
        assertChance(helper, Identifier.fromNamespaceAndPath("mysticalagriculture", "coal_seeds"), 0.05);
        helper.succeed();
    }

    private static void assertChance(GameTestHelper helper, Identifier seedId, double expected) {
        Item seed = BuiltInRegistries.ITEM.getValue(seedId);
        double chance = TraitsHelper.getIncreaseChance(BuiltInRegistries.ITEM.wrapAsHolder(seed));
        if (chance != expected) {
            helper.fail(seedId + " expected stat_increase_chance " + expected + " but got " + chance);
        }
    }

    private static void testStatIncreaseChanceFallback(GameTestHelper helper) {
        assertChance(helper, Identifier.withDefaultNamespace("carrot"), 0.07);
        assertChance(helper, Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "arrowroot"), 0.07);
        assertChance(helper, Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "arugula_seeds"), 0.07);
        assertChance(helper, Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "black_truffle"), 0.07);
        assertChance(helper, Identifier.withDefaultNamespace("stick"), 0.0);
        helper.succeed();
    }

    // Nutrient water in the controller's tank acts as bonemeal: a young crop in the farm grows when the
    // controller ticks with fluid available, and the fluid drains.
    private static void testNutrientWaterBonemeal(GameTestHelper helper) {
        buildFarmRing(helper, true);
        helper.useBlock(CONTROLLER_POS);
        FarmControllerBlockEntity be = helper.getBlockEntity(CONTROLLER_POS, FarmControllerBlockEntity.class);
        if (be.getMultiblockData() == null) {
            helper.fail("farm did not form, cannot test bonemeal", CONTROLLER_POS);
            return;
        }

        ServerLevel level = helper.getLevel();
        try (Transaction tx = Transaction.openRoot()) {
            be.getFluidHandler().insert(FluidResource.of(new FluidStack(FarmingRegistrator.NUTRIENT_WATER.get(), 1000)), 1000, tx);
            tx.commit();
        }
        int fluidBefore = be.getFluidHandler().getAmountAsInt(0);

        ProductiveCropBlock cropBlock = (ProductiveCropBlock) BuiltInRegistries.BLOCK.getValue(rl("arrowroot"));
        BlockPos cropPos = INTERIOR_POS.above();
        helper.setBlock(cropPos, cropBlock.defaultBlockState()); // age 0

        BlockPos controllerPos = helper.absolutePos(CONTROLLER_POS);
        be.tickServer(level, controllerPos, level.getBlockState(controllerPos), be);

        int age = level.getBlockState(helper.absolutePos(cropPos)).getValue(cropBlock.getAgeProperty());
        if (age <= 0) {
            helper.fail("crop was not bonemealed by nutrient water (still age 0)", cropPos);
            return;
        }
        if (be.getFluidHandler().getAmountAsInt(0) >= fluidBefore) {
            helper.fail("nutrient water was not consumed by bonemealing", CONTROLLER_POS);
            return;
        }
        helper.succeed();
    }

    // A pollination recipe (roma_tomato + potato -> beefsteak_tomato) is looked up, applied to the crop's
    // mutation, and the mutated harvest yields the beefsteak_tomato result.
    private static void testCropMutation(GameTestHelper helper) {
        BlockPos relPos = new BlockPos(2, 2, 2);
        ServerLevel level = helper.getLevel();
        Identifier romaId = rl("roma_tomato");
        helper.setBlock(relPos, BuiltInRegistries.BLOCK.getValue(romaId).defaultBlockState());
        CropBlockEntity be = helper.getBlockEntity(relPos, CropBlockEntity.class);

        var recipe = RecipeHelper.getPollinationRecipe(level, romaId, Identifier.withDefaultNamespace("potato"));
        if (recipe == null) {
            helper.fail("no pollination recipe found for roma_tomato + potato (recipe lookup broken)", relPos);
            return;
        }
        Identifier mutation = recipe.value().mutation();
        be.setMutation(mutation);
        if (!mutation.equals(be.getMutation())) {
            helper.fail("mutation was not stored on the crop", relPos);
            return;
        }
        ItemStack mutatedSeed = be.getMutatedSeedStack(mutation);
        if (mutatedSeed.isEmpty() || !BuiltInRegistries.ITEM.getKey(mutatedSeed.getItem()).getPath().contains("beefsteak_tomato")) {
            helper.fail("mutated harvest did not yield beefsteak_tomato (got " + (mutatedSeed.isEmpty() ? "empty" : BuiltInRegistries.ITEM.getKey(mutatedSeed.getItem())) + ")", relPos);
            return;
        }
        helper.succeed();
    }

    // Issue #24: a mutated grape (a FencedPlantLeafBlock, which overrides getHarvestItemStack to return the fruit)
    // safe-harvested with an empty hand should drop the mutation's seed (concord_grape_seeds), not the normal fruit
    // (red_grape). Reproduces the "safe harvest does not return the mutation result" report against 26.1.2.
    private static void testGrapeMutationSafeHarvest(GameTestHelper helper) {
        BlockPos rel = new BlockPos(2, 2, 2);
        ServerLevel level = helper.getLevel();
        Block leaves = BuiltInRegistries.BLOCK.getValue(rl("red_grape_leaves"));
        helper.setBlock(rel, leaves.defaultBlockState().setValue(ProductiveCropBlock.AGE_6, 6));
        CropBlockEntity be = helper.getBlockEntity(rel, CropBlockEntity.class);

        var recipe = RecipeHelper.getPollinationRecipe(level, rl("red_grape"), rl("green_grape"));
        if (recipe == null) {
            helper.fail("no pollination recipe found for red_grape + green_grape (recipe lookup broken)", rel);
            return;
        }
        Identifier mutation = recipe.value().mutation();
        be.setMutation(mutation);
        if (!be.hasMutation()) {
            helper.fail("mutation was not stored on the grape", rel);
            return;
        }

        helper.useBlock(rel);

        BlockPos abs = helper.absolutePos(rel);
        boolean foundMutatedSeed = false;
        boolean foundFruit = false;
        for (ItemEntity itemEntity : level.getEntitiesOfClass(ItemEntity.class, new AABB(abs).inflate(3))) {
            Identifier id = BuiltInRegistries.ITEM.getKey(itemEntity.getItem().getItem());
            if (id.equals(rl("concord_grape_seeds"))) foundMutatedSeed = true;
            if (id.equals(rl("red_grape"))) foundFruit = true;
        }
        if (foundFruit && !foundMutatedSeed) {
            helper.fail("grape safe harvest dropped the normal fruit (red_grape) instead of the mutated seed (concord_grape_seeds) — issue #24", rel);
            return;
        }
        if (!foundMutatedSeed) {
            helper.fail("grape safe harvest did not drop the mutated seed (concord_grape_seeds)", rel);
            return;
        }
        helper.succeed();
    }

    // Issue #25: a VERTICAL_TRELLIS crop (e.g. vanilla) with stored stats, broken at max age, should drop its
    // stat-bearing seed. LootProvider.generate() iterates CROPS/HERBS/BERRIES/TRELLIS/GRAPES/STEMS but omits
    // VERTICAL_TRELLIS, so these blocks have no loot table — break-harvest drops nothing and stats are never
    // stamped (the loot table's cropComponents() is the stamping mechanism). Reproduces "crops not increasing stats".
    private static void testVerticalTrellisStatHarvest(GameTestHelper helper) {
        BlockPos rel = new BlockPos(2, 2, 2);
        ServerLevel level = helper.getLevel();
        Block vanilla = BuiltInRegistries.BLOCK.getValue(rl("vanilla"));
        helper.setBlock(rel, vanilla.defaultBlockState().setValue(ProductiveCropBlock.AGE_6, 6));
        CropBlockEntity be = helper.getBlockEntity(rel, CropBlockEntity.class);
        be.increaseStat(TraitsHelper.GROWTH);

        BlockPos abs = helper.absolutePos(rel);
        level.destroyBlock(abs, true);

        ItemStack seed = ItemStack.EMPTY;
        for (ItemEntity itemEntity : level.getEntitiesOfClass(ItemEntity.class, new AABB(abs).inflate(3))) {
            if (itemEntity.getItem().is(BuiltInRegistries.ITEM.getValue(rl("vanilla_seeds")))) {
                seed = itemEntity.getItem();
                break;
            }
        }
        if (seed.isEmpty()) {
            helper.fail("vertical-trellis crop (vanilla) dropped no vanilla_seeds on harvest — missing loot table, issue #25", rel);
            return;
        }
        if (seed.getOrDefault(FarmingDataComponents.GROWTH, 0) <= 0) {
            helper.fail("harvested vanilla_seeds did not carry the stored growth stat (loot table not stamping components) — issue #25", rel);
            return;
        }
        helper.succeed();
    }

    // Two differently-coloured flowers near each other propagate a new flower whose colour is a blend of
    // the two — so the spread flower's colour differs from both parents.
    private static void testFlowerSpreadMixesColors(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        final int yFloor = 1, yFlower = 2;
        for (int x = 0; x <= 6; x++) {
            for (int z = 0; z <= 6; z++) {
                helper.setBlock(new BlockPos(x, yFloor, z), Blocks.GRASS_BLOCK);
            }
        }
        int colorA = 0xFFFF0000;
        int colorB = 0xFF0000FF;
        BlockPos posA = new BlockPos(1, yFlower, 1);
        BlockPos posB = new BlockPos(5, yFlower, 5);
        helper.setBlock(posA, BuiltInRegistries.BLOCK.getValue(rl("poppy")).defaultBlockState());
        helper.setBlock(posB, BuiltInRegistries.BLOCK.getValue(rl("cornflower")).defaultBlockState());
        helper.getBlockEntity(posA, ColorfulFlowerBlockEntity.class).setColor(colorA);
        helper.getBlockEntity(posB, ColorfulFlowerBlockEntity.class).setColor(colorB);

        BlockPos center = helper.absolutePos(new BlockPos(3, yFlower, 3));
        for (int i = 0; i < 600; i++) {
            FarmUtil.pollinateCrops(level, center, 4, false, new ArrayList<>());
            if (findSpreadFlower(helper, yFlower, posA, posB, colorA, colorB)) {
                helper.succeed();
                return;
            }
        }
        helper.fail("no flower spread with a blended colour after 600 pollination attempts", new BlockPos(3, yFlower, 3));
    }

    private static boolean findSpreadFlower(GameTestHelper helper, int yFlower, BlockPos posA, BlockPos posB, int colorA, int colorB) {
        for (int x = 0; x <= 6; x++) {
            for (int z = 0; z <= 6; z++) {
                BlockPos p = new BlockPos(x, yFlower, z);
                if (p.equals(posA) || p.equals(posB)) {
                    continue;
                }
                if (helper.getLevel().getBlockEntity(helper.absolutePos(p)) instanceof ColorfulFlowerBlockEntity flower) {
                    int c = flower.getColor();
                    if (c != colorA && c != colorB) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // A working beehive delivering honey pollinates nearby flowers: a HONEY_DELIVERED BeeReleaseEvent for a
    // bee whose hive sits among two coloured flowers spreads a new blended-colour flower (productivebees seam).
    private static void testBeeFlowerSpread(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        final int yFloor = 1, yFlower = 2;
        for (int x = 0; x <= 6; x++) {
            for (int z = 0; z <= 6; z++) {
                helper.setBlock(new BlockPos(x, yFloor, z), Blocks.GRASS_BLOCK);
            }
        }
        int colorA = 0xFFFF0000;
        int colorB = 0xFF0000FF;
        BlockPos posA = new BlockPos(1, yFlower, 1);
        BlockPos posB = new BlockPos(5, yFlower, 5);
        helper.setBlock(posA, BuiltInRegistries.BLOCK.getValue(rl("poppy")).defaultBlockState());
        helper.setBlock(posB, BuiltInRegistries.BLOCK.getValue(rl("cornflower")).defaultBlockState());
        helper.getBlockEntity(posA, ColorfulFlowerBlockEntity.class).setColor(colorA);
        helper.getBlockEntity(posB, ColorfulFlowerBlockEntity.class).setColor(colorB);

        BlockPos hiveRel = new BlockPos(0, yFlower + 1, 0);
        helper.setBlock(hiveRel, Blocks.BEEHIVE);
        BeehiveBlockEntity hive = helper.getBlockEntity(hiveRel, BeehiveBlockEntity.class);
        BlockState hiveState = level.getBlockState(helper.absolutePos(hiveRel));

        Bee bee = EntityType.BEE.create(level, EntitySpawnReason.NATURAL);
        if (bee == null) {
            helper.fail("could not create a bee", hiveRel);
            return;
        }
        bee.hivePos = helper.absolutePos(new BlockPos(3, yFlower, 3));

        for (int i = 0; i < 600; i++) {
            NeoForge.EVENT_BUS.post(new BeeReleaseEvent(level, bee, hive, hiveState, BeehiveBlockEntity.BeeReleaseStatus.HONEY_DELIVERED));
            if (findSpreadFlower(helper, yFlower, posA, posB, colorA, colorB)) {
                helper.succeed();
                return;
            }
        }
        helper.fail("no flower spread from bee honey delivery after 600 releases", new BlockPos(3, yFlower, 3));
    }

    // A foreign crop (vanilla wheat) gains stats through events with no block entity. Repeated grow-to-max events
    // eventually roll a stat increase in the per-chunk attachment, mirroring the productivefarming crop path.
    private static void testExternalCropStatIncrease(GameTestHelper helper) {
        BlockPos rel = new BlockPos(2, 2, 2);
        helper.setBlock(rel.below(), Blocks.FARMLAND);
        helper.setBlock(rel, Blocks.WHEAT.defaultBlockState().setValue(BlockStateProperties.AGE_7, 7));
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(rel);
        BlockState state = level.getBlockState(pos);

        for (int i = 0; i < 600 && externalTotalStats(level, pos, state) == 0; i++) {
            NeoForge.EVENT_BUS.post(new CropGrowEvent.Post(level, pos, state, state));
        }
        if (externalTotalStats(level, pos, state) == 0) {
            helper.fail("external wheat traits never increased over 600 grow-to-max events", rel);
            return;
        }
        helper.succeed();
    }

    private static int externalTotalStats(ServerLevel level, BlockPos pos, BlockState state) {
        CropTraitState trait = ExternalCropStats.getTrait(level, pos, state);
        return trait.growth() + trait.yield() + trait.resistance() + trait.mutability();
    }

    // Real random-tick growth (not a posted event) must roll stat increases: drive vanilla wheat from one stage below
    // max to max with actual randomTick calls, so vanilla fires CropGrowEvent.Post itself, and assert a trait climbs.
    // The feeding trough's ResourceHandler inventory must accept and return items.
    private static void testFeedingTroughInventory(GameTestHelper helper) {
        BlockPos rel = new BlockPos(2, 2, 2);
        helper.setBlock(rel, FarmingRegistrator.FEEDING_TROUGH.get());
        FeedingTroughBlockEntity be = helper.getBlockEntity(rel, FeedingTroughBlockEntity.class);
        var inv = be.getItemHandler();

        int inserted;
        try (Transaction tx = Transaction.openRoot()) {
            inserted = inv.insert(ItemResource.of(Items.WHEAT), 10, tx);
            tx.commit();
        }
        if (inserted != 10) {
            helper.fail("feeding trough inventory did not accept 10 wheat (inserted=" + inserted + ")", rel);
            return;
        }
        int total = 0;
        for (int i = 0; i < inv.size(); i++) {
            total += inv.getAmountAsInt(i);
        }
        if (total != 10) {
            helper.fail("feeding trough inventory lost items after insert (total=" + total + ")", rel);
            return;
        }
        int extracted;
        try (Transaction tx = Transaction.openRoot()) {
            extracted = inv.extract(ItemResource.of(Items.WHEAT), 4, tx);
            tx.commit();
        }
        if (extracted != 4) {
            helper.fail("feeding trough inventory did not extract 4 wheat (extracted=" + extracted + ")", rel);
            return;
        }
        helper.succeed();
    }

    // Right-clicking the farm controller with a nutrient water bucket fills its tank and returns an empty bucket.
    private static void testFarmControllerBucketFill(GameTestHelper helper) {
        BlockPos rel = new BlockPos(2, 2, 2);
        helper.setBlock(rel, FarmingRegistrator.FARM_CONTROLLER.get());
        FarmControllerBlockEntity be = helper.getBlockEntity(rel, FarmControllerBlockEntity.class);
        int before = be.getFluidHandler().getAmountAsInt(0);

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(FarmingRegistrator.NUTRIENT_WATER.get().getBucket()));
        helper.useBlock(rel, player);

        if (be.getFluidHandler().getAmountAsInt(0) <= before) {
            helper.fail("farm controller tank was not filled by a nutrient water bucket", rel);
            return;
        }
        helper.succeed();
    }

    // Right-clicking the farm controller with an empty bucket drains 1000 mB of nutrient water and returns a filled bucket.
    private static void testFarmControllerBucketEmpty(GameTestHelper helper) {
        BlockPos rel = new BlockPos(2, 2, 2);
        helper.setBlock(rel, FarmingRegistrator.FARM_CONTROLLER.get());
        FarmControllerBlockEntity be = helper.getBlockEntity(rel, FarmControllerBlockEntity.class);
        try (Transaction tx = Transaction.openRoot()) {
            be.getFluidHandler().insert(FluidResource.of(FarmingRegistrator.NUTRIENT_WATER.get()), 1000, tx);
            tx.commit();
        }
        int before = be.getFluidHandler().getAmountAsInt(0);

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BUCKET));
        helper.useBlock(rel, player);

        if (before - be.getFluidHandler().getAmountAsInt(0) != 1000) {
            helper.fail("farm controller tank was not drained by an empty bucket (before=" + before + ", after=" + be.getFluidHandler().getAmountAsInt(0) + ")", rel);
            return;
        }
        if (!player.getInventory().contains(new ItemStack(FarmingRegistrator.NUTRIENT_WATER.get().getBucket()))) {
            helper.fail("draining the farm controller did not give back a nutrient water bucket", rel);
            return;
        }
        helper.succeed();
    }

    // The watering trough speeds up breeding-cooldown recovery: a nearby adult on cooldown (age > 0) has its age
    // reduced and water is consumed.
    private static void testWateringTroughReducesCooldown(GameTestHelper helper) {
        BlockPos rel = new BlockPos(2, 2, 2);
        helper.setBlock(rel, FarmingRegistrator.WATERING_TROUGH.get());
        WateringTroughBlockEntity be = helper.getBlockEntity(rel, WateringTroughBlockEntity.class);
        try (Transaction tx = Transaction.openRoot()) {
            be.getFluidHandler().insert(FluidResource.of(new FluidStack(Fluids.WATER, 1000)), 1000, tx);
            tx.commit();
        }
        int waterBefore = be.getFluidHandler().getAmountAsInt(0);

        Animal cow = helper.spawn(EntityType.COW, rel.east());
        cow.setAge(6000); // adult on breeding cooldown
        int ageBefore = cow.getAge();

        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(rel);
        be.tickServer(level, pos, level.getBlockState(pos), be);

        if (cow.getAge() >= ageBefore) {
            helper.fail("watering trough did not reduce the cow's breeding cooldown (age " + ageBefore + " -> " + cow.getAge() + ")", rel);
            return;
        }
        if (be.getFluidHandler().getAmountAsInt(0) >= waterBefore) {
            helper.fail("watering trough did not consume water while reducing cooldown", rel);
            return;
        }
        helper.succeed();
    }

    // With food in its inventory, the feeding trough puts nearby adult animals into love mode so they breed.
    private static void testFeedingTroughBreeds(GameTestHelper helper) {
        BlockPos rel = new BlockPos(2, 2, 2);
        helper.setBlock(rel, FarmingRegistrator.FEEDING_TROUGH.get());
        FeedingTroughBlockEntity be = helper.getBlockEntity(rel, FeedingTroughBlockEntity.class);
        try (Transaction tx = Transaction.openRoot()) {
            be.getItemHandler().insert(ItemResource.of(Items.WHEAT), 16, tx);
            tx.commit();
        }

        Animal cowA = helper.spawn(EntityType.COW, rel.east());
        Animal cowB = helper.spawn(EntityType.COW, rel.west());

        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(rel);
        be.tickServer(level, pos, level.getBlockState(pos), be);

        if (!cowA.isInLove() || !cowB.isInLove()) {
            helper.fail("feeding trough did not put both nearby adult cows in love (A=" + cowA.isInLove() + ", B=" + cowB.isInLove() + ")", rel);
            return;
        }
        helper.succeed();
    }

    private static void testExternalRandomTickStatIncrease(GameTestHelper helper) {
        BlockPos rel = new BlockPos(2, 2, 2);
        helper.setBlock(rel.below(), Blocks.FARMLAND);
        helper.setBlock(new BlockPos(3, 2, 2), Blocks.GLOWSTONE); // light so the crop random-tick grows
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(rel);
        boolean everGrewToMax = false;

        for (int cycle = 0; cycle < 1500 && externalTotalStats(level, pos, level.getBlockState(pos)) == 0; cycle++) {
            helper.setBlock(rel, Blocks.WHEAT.defaultBlockState().setValue(BlockStateProperties.AGE_7, 6));
            for (int t = 0; t < 100 && level.getBlockState(pos).getValue(BlockStateProperties.AGE_7) < 7; t++) {
                level.getBlockState(pos).randomTick(level, pos, level.getRandom());
            }
            if (level.getBlockState(pos).getValue(BlockStateProperties.AGE_7) >= 7) {
                everGrewToMax = true;
            }
        }
        if (!everGrewToMax) {
            helper.fail("vanilla wheat never grew to max via randomTick (light/setup issue)", rel);
            return;
        }
        if (externalTotalStats(level, pos, level.getBlockState(pos)) == 0) {
            helper.fail("external wheat traits never increased over real random-tick growth to max", rel);
            return;
        }
        helper.succeed();
    }

    // Mirrors FTB Ultimine's harvest: Block.getDrops directly (no dropResources / BlockDropsEvent). The global loot
    // modifier must still stamp the dropped seeds and apply yield, and the crop's stored stats must remain (replant-safe).
    private static void testExternalCropGetDropsStamps(GameTestHelper helper) {
        BlockPos rel = new BlockPos(2, 2, 2);
        helper.setBlock(rel.below(), Blocks.FARMLAND);
        helper.setBlock(rel, Blocks.WHEAT.defaultBlockState().setValue(BlockStateProperties.AGE_7, 7));
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(rel);
        BlockState state = level.getBlockState(pos);
        ExternalCropStats.setTrait(level, pos, state, new CropTraitState(0, 2, 0, 0, Optional.empty()));

        List<ItemStack> drops = Block.getDrops(state, level, pos, null, null, ItemStack.EMPTY);

        int wheatCount = 0;
        for (ItemStack stack : drops) {
            if (stack.is(Items.WHEAT)) {
                wheatCount = stack.getCount();
            }
        }
        if (wheatCount != 3) {
            helper.fail("getDrops harvest did not apply yield via the loot modifier (expected 3 wheat, got " + wheatCount + ")", rel);
            return;
        }
        // getDrops does not break the block, so the stored stats must survive for the replant.
        if (ExternalCropStats.getTrait(level, pos, state).yield() != 2) {
            helper.fail("getDrops harvest wrongly cleared the crop's stored stats", rel);
            return;
        }
        helper.succeed();
    }

    private static void testExternalCropStampsBaseline(GameTestHelper helper) {
        BlockPos rel = new BlockPos(2, 2, 2);
        helper.setBlock(rel.below(), Blocks.FARMLAND);
        helper.setBlock(rel, Blocks.CARROTS.defaultBlockState().setValue(BlockStateProperties.AGE_7, 7));
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(rel);
        BlockState state = level.getBlockState(pos);

        List<ItemStack> drops = Block.getDrops(state, level, pos, null, null, ItemStack.EMPTY);
        ItemStack carrot = ItemStack.EMPTY;
        for (ItemStack stack : drops) {
            if (stack.is(Items.CARROT)) {
                carrot = stack;
            }
        }
        if (carrot.isEmpty()) {
            helper.fail("statless carrot produced no carrot drop", rel);
            return;
        }
        if (!carrot.has(FarmingDataComponents.GROWTH)) {
            helper.fail("statless external crop seed was not stamped with baseline (0) stat components", rel);
            return;
        }
        helper.succeed();
    }

    private static void testExternalCropCustomDropsStamps(GameTestHelper helper) {
        BlockPos rel = new BlockPos(2, 2, 2);
        Block maCrop = BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath("mysticalagriculture", "inferium_crop"));
        Item maSeed = BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("mysticalagriculture", "inferium_seeds"));
        if (!(maCrop instanceof CropBlock crop) || maSeed == Items.AIR) {
            helper.succeed();
            return;
        }
        helper.setBlock(rel.below(), Blocks.FARMLAND);
        helper.setBlock(rel, crop.defaultBlockState().setValue(crop.getAgeProperty(), crop.getMaxAge()));
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(rel);
        BlockState state = level.getBlockState(pos);
        ExternalCropStats.setTrait(level, pos, state, new CropTraitState(0, 2, 0, 0, Optional.empty()));

        level.destroyBlock(pos, true);

        ItemStack seed = ItemStack.EMPTY;
        for (ItemEntity entity : level.getEntitiesOfClass(ItemEntity.class, new AABB(pos).inflate(3))) {
            if (entity.getItem().is(maSeed)) {
                seed = entity.getItem();
            }
        }
        if (seed.isEmpty()) {
            helper.fail("MA break dropped no inferium_seeds (eligible=" + ExternalCropStats.isEligible(state) + ")", rel);
            return;
        }
        if (!seed.has(FarmingDataComponents.YIELD)) {
            helper.fail("MA break did not stamp the seed via the pseudo mixin", rel);
            return;
        }
        helper.succeed();
    }

    // Bonemeal on a foreign crop must also roll stat increases. Vanilla bonemeal does not fire CropGrowEvent.Post, so
    // this exercises the BonemealEvent takeover: bonemeal a near-max wheat to max repeatedly until a trait climbs.
    private static void testExternalBonemealStatIncrease(GameTestHelper helper) {
        BlockPos rel = new BlockPos(2, 2, 2);
        helper.setBlock(rel.below(), Blocks.FARMLAND);
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(rel);

        for (int i = 0; i < 600 && externalTotalStats(level, pos, level.getBlockState(pos)) == 0; i++) {
            helper.setBlock(rel, Blocks.WHEAT.defaultBlockState().setValue(BlockStateProperties.AGE_7, 6));
            BlockState state = level.getBlockState(pos);
            // A fresh stack each cycle: the takeover now consumes one, like vanilla bone meal.
            NeoForge.EVENT_BUS.post(new BonemealEvent(null, level, pos, state, new ItemStack(Items.BONE_MEAL)));
        }
        if (externalTotalStats(level, pos, level.getBlockState(pos)) == 0) {
            helper.fail("external wheat traits never increased over 600 bonemeal grow-to-max cycles", rel);
            return;
        }
        helper.succeed();
    }

    // When the takeover claims a bonemeal on an external crop it must consume one from the stack, just as vanilla bone
    // meal would have, since cancelling the event skips vanilla's own shrink.
    private static void testBonemealTakeoverConsumesStack(GameTestHelper helper) {
        BlockPos rel = new BlockPos(2, 2, 2);
        helper.setBlock(rel.below(), Blocks.FARMLAND);
        helper.setBlock(rel, Blocks.WHEAT.defaultBlockState().setValue(BlockStateProperties.AGE_7, 3));
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(rel);
        BlockState state = level.getBlockState(pos);

        ItemStack boneMeal = new ItemStack(Items.BONE_MEAL, 3);
        NeoForge.EVENT_BUS.post(new BonemealEvent(null, level, pos, state, boneMeal));
        if (boneMeal.getCount() != 2) {
            helper.fail("bonemeal takeover did not consume one bone meal on an external crop (count=" + boneMeal.getCount() + ")", rel);
            return;
        }
        helper.succeed();
    }

    // The bonemeal takeover must only claim vanilla bone meal. Other mods' fertilizers (Mystical Agriculture's, etc.)
    // fire BonemealEvent too and run their own growth, so the handler must leave those events untouched.
    private static void testBonemealTakeoverIgnoresOtherFertilizers(GameTestHelper helper) {
        BlockPos rel = new BlockPos(2, 2, 2);
        helper.setBlock(rel.below(), Blocks.FARMLAND);
        helper.setBlock(rel, Blocks.WHEAT.defaultBlockState().setValue(BlockStateProperties.AGE_7, 3));
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(rel);
        BlockState state = level.getBlockState(pos);

        // A non-bone-meal item standing in for another mod's fertilizer: the handler must not claim it.
        BonemealEvent other = new BonemealEvent(null, level, pos, state, new ItemStack(Items.STICK));
        NeoForge.EVENT_BUS.post(other);
        if (other.isCanceled() || other.isSuccessful()) {
            helper.fail("bonemeal takeover wrongly claimed a non-bone-meal fertilizer", rel);
            return;
        }

        // Real bone meal must still be taken over (cancelled and marked successful so it is consumed).
        BonemealEvent boneMeal = new BonemealEvent(null, level, pos, state, new ItemStack(Items.BONE_MEAL));
        NeoForge.EVENT_BUS.post(boneMeal);
        if (!boneMeal.isCanceled() || !boneMeal.isSuccessful()) {
            helper.fail("bonemeal takeover did not claim vanilla bone meal", rel);
            return;
        }
        helper.succeed();
    }

    // A high growth stat forces extra growth ticks via CropGrowEvent.Pre, so a growth-3 crop is told to GROW on
    // most ticks it would otherwise skip.
    private static void testExternalCropGrowthSpeed(GameTestHelper helper) {
        BlockPos rel = new BlockPos(2, 2, 2);
        helper.setBlock(rel.below(), Blocks.FARMLAND);
        helper.setBlock(rel, Blocks.WHEAT.defaultBlockState().setValue(BlockStateProperties.AGE_7, 3));
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(rel);
        BlockState state = level.getBlockState(pos);
        ExternalCropStats.setTrait(level, pos, state, new CropTraitState(3, 0, 0, 0, Optional.empty()));

        int forced = 0;
        for (int i = 0; i < 200; i++) {
            CropGrowEvent.Pre pre = new CropGrowEvent.Pre(level, pos, state);
            NeoForge.EVENT_BUS.post(pre);
            if (pre.getResult() == CropGrowEvent.Pre.Result.GROW) {
                forced++;
            }
        }
        if (forced == 0) {
            helper.fail("growth-3 external wheat was never forced to grow over 200 pre-grow events", rel);
            return;
        }
        helper.succeed();
    }

    // Harvesting a foreign crop grows every drop by the yield stat. Vanilla wheat drops exactly one wheat, so a
    // yield of 2 makes the broken block drop three.
    private static void testExternalCropHarvestYield(GameTestHelper helper) {
        BlockPos rel = new BlockPos(2, 2, 2);
        helper.setBlock(rel.below(), Blocks.FARMLAND);
        helper.setBlock(rel, Blocks.WHEAT.defaultBlockState().setValue(BlockStateProperties.AGE_7, 7));
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(rel);
        BlockState state = level.getBlockState(pos);
        ExternalCropStats.setTrait(level, pos, state, new CropTraitState(0, 2, 0, 0, Optional.empty()));

        level.destroyBlock(pos, true);

        int wheatCount = 0;
        for (ItemEntity itemEntity : level.getEntitiesOfClass(ItemEntity.class, new AABB(pos).inflate(3))) {
            if (itemEntity.getItem().is(Items.WHEAT)) {
                wheatCount = itemEntity.getItem().getCount();
            }
        }
        if (wheatCount != 3) {
            helper.fail("expected 3 wheat from a yield-2 harvest (1 base + 2), got " + wheatCount, rel);
            return;
        }
        helper.succeed();
    }

    // A pollinated foreign crop drops the mutation's seed when harvested.
    private static void testExternalCropMutationHarvest(GameTestHelper helper) {
        BlockPos rel = new BlockPos(2, 2, 2);
        helper.setBlock(rel.below(), Blocks.FARMLAND);
        helper.setBlock(rel, Blocks.WHEAT.defaultBlockState().setValue(BlockStateProperties.AGE_7, 7));
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(rel);
        BlockState state = level.getBlockState(pos);
        ExternalCropStats.setMutation(level, pos, state, rl("beefsteak_tomato"));

        level.destroyBlock(pos, true);

        boolean foundMutatedSeed = false;
        for (ItemEntity itemEntity : level.getEntitiesOfClass(ItemEntity.class, new AABB(pos).inflate(3))) {
            if (BuiltInRegistries.ITEM.getKey(itemEntity.getItem().getItem()).getPath().contains("beefsteak_tomato")) {
                foundMutatedSeed = true;
                break;
            }
        }
        if (!foundMutatedSeed) {
            helper.fail("pollinated external wheat did not drop a beefsteak_tomato seed on harvest", rel);
            return;
        }
        helper.succeed();
    }

    // Adding height turns the farm into a fish farm. A cube (3-layer brick box, controller in the top rim) detects
    // aquatic entities inside and produces nutrient water from them (50 mB per fish, before clams).
    private static final BlockPos FISH_CONTROLLER_POS = new BlockPos(2, 5, 0);

    private static void testFishFarm(GameTestHelper helper) {
        buildFishFarmCube(helper);
        helper.useBlock(FISH_CONTROLLER_POS);

        FarmControllerBlockEntity be = helper.getBlockEntity(FISH_CONTROLLER_POS, FarmControllerBlockEntity.class);
        if (be.getMultiblockData() == null) {
            helper.fail("fish farm multiblock did not form", FISH_CONTROLLER_POS);
            return;
        }
        if (be.getMultiblockData().height() <= 1) {
            helper.fail("multiblock formed flat, not as a cube fish farm (height=" + be.getMultiblockData().height() + ")", FISH_CONTROLLER_POS);
            return;
        }

        // Two fish well inside the cube: enough to count as a fish farm and to drive nutrient water production.
        AbstractFish codA = helper.spawn(EntityType.COD, new BlockPos(2, 3, 2));
        AbstractFish codB = helper.spawn(EntityType.COD, new BlockPos(2, 4, 2));

        ServerLevel level = helper.getLevel();
        BlockPos controllerAbs = helper.absolutePos(FISH_CONTROLLER_POS);
        int before = be.getFluidHandler().getAmountAsInt(0);
        be.tickServer(level, controllerAbs, level.getBlockState(controllerAbs), be);
        int produced = be.getFluidHandler().getAmountAsInt(0) - before;

        if (produced != 100) {
            helper.fail("fish farm did not produce nutrient water from 2 fish (expected 100 mB, got " + produced + ")", FISH_CONTROLLER_POS);
            return;
        }
        if (!codA.isPersistenceRequired() || !codB.isPersistenceRequired()) {
            helper.fail("fish farm did not mark the fish persistent, so they could despawn", FISH_CONTROLLER_POS);
            return;
        }
        helper.succeed();
    }

    // An overstocked fish farm culls the population back down. The fish are placed on the cube floor (where they
    // naturally settle) to confirm the controller counts the whole volume, not just the centre.
    private static void testFishFarmCulls(GameTestHelper helper) {
        buildFishFarmCube(helper);
        helper.useBlock(FISH_CONTROLLER_POS);
        FarmControllerBlockEntity be = helper.getBlockEntity(FISH_CONTROLLER_POS, FarmControllerBlockEntity.class);
        if (be.getMultiblockData() == null || be.getMultiblockData().height() <= 1) {
            helper.fail("fish farm did not form as a cube", FISH_CONTROLLER_POS);
            return;
        }

        int spawned = 30;
        for (int i = 0; i < spawned; i++) {
            helper.spawn(EntityType.COD, new BlockPos(1 + i % 3, 2, 1 + (i / 3) % 3));
        }

        ServerLevel level = helper.getLevel();
        BlockPos controllerAbs = helper.absolutePos(FISH_CONTROLLER_POS);
        AABB cube = AABB.encapsulatingFullBlocks(helper.absolutePos(new BlockPos(0, 2, 0)), helper.absolutePos(new BlockPos(4, 5, 4)));
        be.tickServer(level, controllerAbs, level.getBlockState(controllerAbs), be);
        // Culled fish play out a death animation before being removed, so count the ones still alive.
        long alive = level.getEntitiesOfClass(AbstractFish.class, cube).stream().filter(e -> !e.isDeadOrDying()).count();

        if (alive >= spawned) {
            helper.fail("overstocked fish farm did not cull the population (spawned " + spawned + ", " + alive + " still alive)", FISH_CONTROLLER_POS);
            return;
        }
        helper.succeed();
    }

    // When the fish produce more nutrient water than the tank has room for, the tank tops off to its capacity rather
    // than rejecting the whole batch.
    private static void testFishFarmTopsOffTank(GameTestHelper helper) {
        buildFishFarmCube(helper);
        helper.useBlock(FISH_CONTROLLER_POS);
        FarmControllerBlockEntity be = helper.getBlockEntity(FISH_CONTROLLER_POS, FarmControllerBlockEntity.class);
        if (be.getMultiblockData() == null || be.getMultiblockData().height() <= 1) {
            helper.fail("fish farm did not form as a cube", FISH_CONTROLLER_POS);
            return;
        }

        FluidResource nutrientWater = FluidResource.of(FarmingRegistrator.NUTRIENT_WATER.get());
        int capacity = be.getFluidHandler().getCapacityAsInt(0, nutrientWater);
        // Fill to just below capacity, leaving less room than a single tick of fish will produce.
        try (Transaction tx = Transaction.openRoot()) {
            be.getFluidHandler().insert(nutrientWater, capacity - 100, tx);
            tx.commit();
        }
        // Four fish produce 200 mB, more than the 100 mB of remaining room.
        for (int i = 0; i < 4; i++) {
            helper.spawn(EntityType.COD, new BlockPos(2, 3, 2));
        }

        ServerLevel level = helper.getLevel();
        BlockPos controllerAbs = helper.absolutePos(FISH_CONTROLLER_POS);
        be.tickServer(level, controllerAbs, level.getBlockState(controllerAbs), be);

        int after = be.getFluidHandler().getAmountAsInt(0);
        if (after != capacity) {
            helper.fail("fish farm did not top off the tank (capacity " + capacity + ", ended at " + after + ")", FISH_CONTROLLER_POS);
            return;
        }
        helper.succeed();
    }

    // A fish farm's output is the nutrient water in its tank, not the item inventory, so it keeps producing even when
    // the item inventory is full (e.g. clogged with drops from culled fish).
    private static void testFishFarmProducesWithFullInventory(GameTestHelper helper) {
        buildFishFarmCube(helper);
        helper.useBlock(FISH_CONTROLLER_POS);
        FarmControllerBlockEntity be = helper.getBlockEntity(FISH_CONTROLLER_POS, FarmControllerBlockEntity.class);
        if (be.getMultiblockData() == null || be.getMultiblockData().height() <= 1) {
            helper.fail("fish farm did not form as a cube", FISH_CONTROLLER_POS);
            return;
        }

        // Clog every output slot.
        for (int slot = 0; slot < be.inventoryHandler.size(); slot++) {
            try (Transaction tx = Transaction.openRoot()) {
                be.inventoryHandler.insert(slot, ItemResource.of(Items.COBBLESTONE), 64, tx);
                tx.commit();
            }
        }

        helper.spawn(EntityType.COD, new BlockPos(2, 3, 2));

        ServerLevel level = helper.getLevel();
        BlockPos controllerAbs = helper.absolutePos(FISH_CONTROLLER_POS);
        int before = be.getFluidHandler().getAmountAsInt(0);
        be.tickServer(level, controllerAbs, level.getBlockState(controllerAbs), be);

        if (be.getFluidHandler().getAmountAsInt(0) <= before) {
            helper.fail("fish farm stopped producing nutrient water when its item inventory was full", FISH_CONTROLLER_POS);
            return;
        }
        helper.succeed();
    }

    // The cull/breed cycle is gated on inventory space (its fish drops would be voided otherwise), so an overstocked
    // farm with a full inventory must leave the population untouched - no fish killed and none bred - even though
    // nutrient water keeps flowing to the tank.
    private static void testFishFarmHoldsWhenInventoryFull(GameTestHelper helper) {
        buildFishFarmCube(helper);
        helper.useBlock(FISH_CONTROLLER_POS);
        FarmControllerBlockEntity be = helper.getBlockEntity(FISH_CONTROLLER_POS, FarmControllerBlockEntity.class);
        if (be.getMultiblockData() == null || be.getMultiblockData().height() <= 1) {
            helper.fail("fish farm did not form as a cube", FISH_CONTROLLER_POS);
            return;
        }

        // Clog every output slot so there is no room for drops.
        for (int slot = 0; slot < be.inventoryHandler.size(); slot++) {
            try (Transaction tx = Transaction.openRoot()) {
                be.inventoryHandler.insert(slot, ItemResource.of(Items.COBBLESTONE), 64, tx);
                tx.commit();
            }
        }

        // Overstocked: with an empty inventory this many fish would be culled back down.
        int spawned = 30;
        for (int i = 0; i < spawned; i++) {
            helper.spawn(EntityType.COD, new BlockPos(1 + i % 3, 2, 1 + (i / 3) % 3));
        }

        ServerLevel level = helper.getLevel();
        BlockPos controllerAbs = helper.absolutePos(FISH_CONTROLLER_POS);
        AABB cube = AABB.encapsulatingFullBlocks(helper.absolutePos(new BlockPos(0, 2, 0)), helper.absolutePos(new BlockPos(4, 5, 4)));
        be.tickServer(level, controllerAbs, level.getBlockState(controllerAbs), be);

        long alive = level.getEntitiesOfClass(AbstractFish.class, cube).stream().filter(e -> !e.isDeadOrDying()).count();
        if (alive != spawned) {
            helper.fail("fish farm bred or culled fish while its inventory was full (spawned " + spawned + ", " + alive + " alive after tick)", FISH_CONTROLLER_POS);
            return;
        }
        helper.succeed();
    }

    // A bonemealable block in the fish farm's volume draws 100 mB per tick, but the fish produce more than that, so the
    // tank still ends the tick topped off rather than resting one bonemeal draw below capacity.
    private static void testFishFarmTankStaysFullWithBonemealable(GameTestHelper helper) {
        buildFishFarmCube(helper);
        helper.useBlock(FISH_CONTROLLER_POS);
        FarmControllerBlockEntity be = helper.getBlockEntity(FISH_CONTROLLER_POS, FarmControllerBlockEntity.class);
        if (be.getMultiblockData() == null || be.getMultiblockData().height() <= 1) {
            helper.fail("fish farm did not form as a cube", FISH_CONTROLLER_POS);
            return;
        }

        // A bonemealable crop sitting in the farm volume that the controller will try to grow each tick.
        helper.setBlock(new BlockPos(2, 6, 2), Blocks.WHEAT.defaultBlockState());

        FluidResource nutrientWater = FluidResource.of(FarmingRegistrator.NUTRIENT_WATER.get());
        int capacity = be.getFluidHandler().getCapacityAsInt(0, nutrientWater);
        try (Transaction tx = Transaction.openRoot()) {
            be.getFluidHandler().insert(nutrientWater, capacity, tx);
            tx.commit();
        }
        // Four fish produce 200 mB, more than the 100 mB a bonemeal draws.
        for (int i = 0; i < 4; i++) {
            helper.spawn(EntityType.COD, new BlockPos(2, 3, 2));
        }

        ServerLevel level = helper.getLevel();
        BlockPos controllerAbs = helper.absolutePos(FISH_CONTROLLER_POS);
        be.tickServer(level, controllerAbs, level.getBlockState(controllerAbs), be);

        int after = be.getFluidHandler().getAmountAsInt(0);
        if (after != capacity) {
            helper.fail("fish farm tank did not stay topped off (capacity " + capacity + ", ended at " + after + ")", FISH_CONTROLLER_POS);
            return;
        }
        helper.succeed();
    }

    // A fully grown crop is still a BonemealableBlock but cannot be grown further, so the farm must not spend nutrient
    // water trying to bonemeal it.
    private static void testFarmDoesNotDrainOnUngrowableBlock(GameTestHelper helper) {
        buildFishFarmCube(helper);
        helper.useBlock(FISH_CONTROLLER_POS);
        FarmControllerBlockEntity be = helper.getBlockEntity(FISH_CONTROLLER_POS, FarmControllerBlockEntity.class);
        if (be.getMultiblockData() == null || be.getMultiblockData().height() <= 1) {
            helper.fail("fish farm did not form as a cube", FISH_CONTROLLER_POS);
            return;
        }

        // A grass block capped by a solid block in the scan zone: a BonemealableBlock that is not a valid bonemeal
        // target (grass can only spread under open air) and isn't a harvestable crop.
        helper.setBlock(new BlockPos(2, 6, 2), Blocks.GRASS_BLOCK.defaultBlockState());
        helper.setBlock(new BlockPos(2, 7, 2), Blocks.STONE.defaultBlockState());

        FluidResource nutrientWater = FluidResource.of(FarmingRegistrator.NUTRIENT_WATER.get());
        int capacity = be.getFluidHandler().getCapacityAsInt(0, nutrientWater);
        try (Transaction tx = Transaction.openRoot()) {
            be.getFluidHandler().insert(nutrientWater, capacity, tx);
            tx.commit();
        }
        // No fish, so nothing refills the tank: any drain would show up directly.
        ServerLevel level = helper.getLevel();
        BlockPos controllerAbs = helper.absolutePos(FISH_CONTROLLER_POS);
        be.tickServer(level, controllerAbs, level.getBlockState(controllerAbs), be);

        int after = be.getFluidHandler().getAmountAsInt(0);
        if (after != capacity) {
            helper.fail("farm drained nutrient water on a block it could not grow (capacity " + capacity + ", ended at " + after + ")", FISH_CONTROLLER_POS);
            return;
        }
        helper.succeed();
    }

    // With enough fish present, the fish farm breeds more of them: seed the cube with several cod and tick until the
    // population grows beyond the starting count.
    private static void testFishFarmBreeds(GameTestHelper helper) {
        buildFishFarmCube(helper);
        helper.useBlock(FISH_CONTROLLER_POS);
        FarmControllerBlockEntity be = helper.getBlockEntity(FISH_CONTROLLER_POS, FarmControllerBlockEntity.class);
        if (be.getMultiblockData() == null || be.getMultiblockData().height() <= 1) {
            helper.fail("fish farm did not form as a cube", FISH_CONTROLLER_POS);
            return;
        }

        int startCount = 2; // a breeding pair must be enough to multiply
        for (int i = 0; i < startCount; i++) {
            helper.spawn(EntityType.COD, new BlockPos(2, 3, 2));
        }

        ServerLevel level = helper.getLevel();
        BlockPos controllerAbs = helper.absolutePos(FISH_CONTROLLER_POS);
        AABB cube = AABB.encapsulatingFullBlocks(helper.absolutePos(new BlockPos(0, 1, 0)), helper.absolutePos(new BlockPos(4, 6, 4)));

        boolean multiplied = false;
        for (int t = 0; t < 400 && !multiplied; t++) {
            be.tickServer(level, controllerAbs, level.getBlockState(controllerAbs), be);
            if (level.getEntitiesOfClass(AbstractFish.class, cube).size() > startCount) {
                multiplied = true;
            }
        }
        if (!multiplied) {
            helper.fail("fish never multiplied in the fish farm over 400 ticks", FISH_CONTROLLER_POS);
            return;
        }
        helper.succeed();
    }

    // A hollow 5×5 brick box four layers tall (y=2..5) with the controller in the middle of the top -Z edge and three
    // walls below it, giving the multiblock a height of 3 and a roomy interior so it forms as a fish farm.
    private static void buildFishFarmCube(GameTestHelper helper) {
        BlockState wall = Blocks.BRICKS.defaultBlockState();
        BlockState controller = FarmingRegistrator.FARM_CONTROLLER.get().defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH);
        for (int y = 2; y <= 5; y++) {
            for (int x = 0; x <= 4; x++) {
                for (int z = 0; z <= 4; z++) {
                    if (x > 0 && x < 4 && z > 0 && z < 4) {
                        continue; // hollow interior
                    }
                    BlockPos p = new BlockPos(x, y, z);
                    helper.setBlock(p, p.equals(FISH_CONTROLLER_POS) ? controller : wall);
                }
            }
        }
    }

    /**
     * Lays a 3×3 ring of {@code minecraft:bricks} (a farm-wall block) at {@link #WALL_Y} with the
     * controller in the middle of the -Z edge, hollow centre. {@code complete=false} leaves the
     * opposite (+Z) wall-middle open so the ring can't enclose — used for the negative test.
     */
    private static void buildFarmRing(GameTestHelper helper, boolean complete) {
        BlockState wall = Blocks.BRICKS.defaultBlockState();
        BlockState controller = FarmingRegistrator.FARM_CONTROLLER.get().defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH);

        for (int x = 0; x <= 2; x++) {
            for (int z = 0; z <= 2; z++) {
                if (x == 1 && z == 1) {
                    continue; // hollow interior
                }
                if (!complete && x == 1 && z == 2) {
                    continue; // gap in the ring (opposite the controller) for the negative test
                }
                BlockPos p = new BlockPos(x, WALL_Y, z);
                helper.setBlock(p, p.equals(CONTROLLER_POS) ? controller : wall);
            }
        }
    }

    private static void testAgriTechRecipesPresent(GameTestHelper helper) {
        BlockPos rel = new BlockPos(1, 2, 1);
        ServerLevel level = helper.getLevel();
        RecipeHolder<?> twoRecipe = level.recipeAccess().recipeMap().byKey(ResourceKey.create(Registries.RECIPE, rl("agritech/arugula")));
        RecipeHolder<?> evolvedRecipe = level.recipeAccess().recipeMap().byKey(ResourceKey.create(Registries.RECIPE, rl("agritech_evolved/arugula")));
        if (twoRecipe == null) {
            helper.fail("missing agritechtwo crop compat recipe productivefarming:agritech/arugula", rel);
            return;
        }
        if (evolvedRecipe == null) {
            helper.fail("missing agritechevolved crop compat recipe productivefarming:agritech_evolved/arugula", rel);
            return;
        }
        helper.succeed();
    }

    private static void testAgriTechGrowthSpeed(GameTestHelper helper) {
        BlockPos rel = new BlockPos(1, 2, 1);
        Item seedItem = BuiltInRegistries.ITEM.getValue(rl("arugula_seeds"));
        ItemStack growth3 = new ItemStack(seedItem);
        TraitsHelper.applyTraits(growth3, 3, 0, 0, 0);
        int fast = AgriTechStatsHelper.adjustGrowthTime(100, growth3);
        int base = AgriTechStatsHelper.adjustGrowthTime(100, new ItemStack(seedItem));
        if (fast != 25) {
            helper.fail("growth-3 seed should quarter the planter processing time (100 -> 25), got " + fast, rel);
            return;
        }
        if (base != 100) {
            helper.fail("statless seed should not change the planter processing time, got " + base, rel);
            return;
        }
        helper.succeed();
    }

    private static void testAgriTechYieldAndLineage(GameTestHelper helper) {
        BlockPos rel = new BlockPos(1, 2, 1);
        Item seedItem = BuiltInRegistries.ITEM.getValue(rl("arugula_seeds"));
        Item produce = BuiltInRegistries.ITEM.getValue(rl("arugula"));
        ItemStack seed = new ItemStack(seedItem);
        TraitsHelper.applyTraits(seed, 1, 2, 1, 1);
        List<ItemStack> drops = new ArrayList<>(List.of(new ItemStack(produce, 1), new ItemStack(seedItem, 1)));
        AgriTechStatsHelper.applyStats(drops, seed);

        int produceCount = 0;
        int seedYield = -1;
        int seedCount = -1;
        for (ItemStack stack : drops) {
            if (stack.is(produce)) {
                produceCount = stack.getCount();
            }
            if (stack.is(seedItem)) {
                seedYield = stack.getOrDefault(FarmingDataComponents.YIELD, -1);
                seedCount = stack.getCount();
            }
        }
        if (produceCount != 3) {
            helper.fail("yield-2 should grow the produce drop 1 -> 3, got " + produceCount, rel);
            return;
        }
        if (seedYield != 2) {
            helper.fail("harvested seed should inherit the parent yield stat (2), got " + seedYield, rel);
            return;
        }
        if (seedCount != 1) {
            helper.fail("yield must not grow the harvested seed drop (expected count 1, got " + seedCount + ")", rel);
            return;
        }
        helper.succeed();
    }

    private static void testAgriTechInputStatIncrease(GameTestHelper helper) {
        BlockPos rel = new BlockPos(1, 2, 1);
        Item seedItem = BuiltInRegistries.ITEM.getValue(rl("arugula_seeds"));
        ItemStack seed = new ItemStack(seedItem);
        TraitsHelper.applyTraits(seed, 0, 0, 0, 0);
        RandomSource random = helper.getLevel().getRandom();

        boolean increased = false;
        for (int i = 0; i < 3000 && !increased; i++) {
            AgriTechStatsHelper.increaseInputStat(seed, random);
            if (agritechSeedStatTotal(seed) > 0) {
                increased = true;
            }
        }
        if (!increased) {
            helper.fail("input seed stat never increased over 3000 harvest rolls", rel);
            return;
        }
        helper.succeed();
    }

    private static void testAgriTechInitializesStatlessSeed(GameTestHelper helper) {
        BlockPos rel = new BlockPos(1, 2, 1);
        Item seedItem = BuiltInRegistries.ITEM.getValue(Identifier.withDefaultNamespace("wheat_seeds"));
        ItemStack seed = new ItemStack(seedItem);
        RandomSource random = helper.getLevel().getRandom();

        boolean gained = false;
        for (int i = 0; i < 3000 && !gained; i++) {
            AgriTechStatsHelper.increaseInputStat(seed, random);
            if (seed.has(FarmingDataComponents.GROWTH) && agritechSeedStatTotal(seed) > 0) {
                gained = true;
            }
        }
        if (!gained) {
            helper.fail("a statless external seed was not initialized and increased in the planter", rel);
            return;
        }
        helper.succeed();
    }

    private static int agritechSeedStatTotal(ItemStack seed) {
        return seed.getOrDefault(FarmingDataComponents.GROWTH, 0)
                + seed.getOrDefault(FarmingDataComponents.YIELD, 0)
                + seed.getOrDefault(FarmingDataComponents.RESISTANCE, 0)
                + seed.getOrDefault(FarmingDataComponents.MUTABILITY, 0);
    }

    private static void testFarmControllerSlotsInsertable(GameTestHelper helper) {
        BlockPos rel = new BlockPos(1, 2, 1);
        helper.setBlock(rel, FarmingRegistrator.FARM_CONTROLLER.get());
        FarmControllerBlockEntity be = helper.getBlockEntity(rel, FarmControllerBlockEntity.class);
        ItemStack stack = new ItemStack(Items.WHEAT_SEEDS);
        for (int slot : new int[]{0, 4, 11, 20, 26}) {
            ManualSlotItemHandler slotHandler = new ManualSlotItemHandler(be.inventoryHandler, slot, 0, 0);
            if (!slotHandler.mayPlace(stack)) {
                helper.fail("farm controller slot " + slot + " should accept manual insertion", rel);
                return;
            }
        }
        helper.succeed();
    }
}
