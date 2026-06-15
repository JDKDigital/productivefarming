package cy.jdkdigital.productivefarming;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config
{
    private static final ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();
    private static final ModConfigSpec.Builder STARTUP_BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SERVER_CONFIG;
    public static final ModConfigSpec STARTUP_CONFIG;
    public static final Server SERVER = new Server(SERVER_BUILDER);
    public static final Startup STARTUP = new Startup(STARTUP_BUILDER);

    static {
        SERVER_CONFIG = SERVER_BUILDER.build();
        STARTUP_CONFIG = STARTUP_BUILDER.build();
    }

    public static class Startup
    {
        public final ModConfigSpec.BooleanValue villagersTradeSeeds;

        public Startup(ModConfigSpec.Builder builder) {
            builder.push("General");

            villagersTradeSeeds = builder
                    .comment("Seeds can be obtained from farmer villager trades")
                    .define("villagersTradeSeeds", true);

            builder.pop();
        }
    }

    public static class Server
    {
        public final ModConfigSpec.IntValue farmMaxVolume;
        public final ModConfigSpec.IntValue farmMaxCircumference;
        public final ModConfigSpec.IntValue farmMaxHeight;
        public final ModConfigSpec.IntValue feedingTroughTickRate;
        public final ModConfigSpec.IntValue wateringTroughTickRate;
        public final ModConfigSpec.IntValue fishTrapTickRate;
        public final ModConfigSpec.IntValue childSeparatorTickRate;

        public final ModConfigSpec.DoubleValue clamSpreadChance;
        public final ModConfigSpec.DoubleValue flowerPropagationChance;
        public final ModConfigSpec.DoubleValue speedUpgradeModifier;
        public final ModConfigSpec.IntValue pollenChanceFromSieve; // TODO 1.22 change to double

        public final ModConfigSpec.BooleanValue statsOnExternalCrops;
        public final ModConfigSpec.BooleanValue spawnFlowersWithBonemeal;

        public Server(ModConfigSpec.Builder builder) {
            builder.push("General");

            farmMaxVolume = builder
                    .comment("Max internal volume of the farm multiblock")
                    .defineInRange("farmMaxVolume", 1024, 1, Integer.MAX_VALUE);

            farmMaxCircumference = builder
                    .comment("Max circumference of the farm multiblock")
                    .defineInRange("farmMaxCircumference", 200, 1, Integer.MAX_VALUE);

            farmMaxHeight = builder
                    .comment("Max height of the farm multiblock")
                    .defineInRange("farmMaxHeight", 20, 1, Integer.MAX_VALUE);

            feedingTroughTickRate = builder
                    .comment("Tickrate for Feeding Troughs")
                    .defineInRange("feedingTroughTickRate", 1200, 1, Integer.MAX_VALUE);

            wateringTroughTickRate = builder
                    .comment("Tickrate for Watering Troughs")
                    .defineInRange("wateringTroughTickRate", 1200, 1, Integer.MAX_VALUE);

            fishTrapTickRate = builder
                    .comment("Tickrate for Fishing Traps")
                    .defineInRange("fishTrapTickRate", 6000, 1, Integer.MAX_VALUE);

            childSeparatorTickRate = builder
                    .comment("Tickrate for Child Separator")
                    .defineInRange("childSeparatorTickRate", 600, 1, Integer.MAX_VALUE);

            clamSpreadChance = builder
                    .comment("Chance for clams to propagate in a fish farm")
                    .defineInRange("clamSpreadChance", 0.1, 0, 1);

            flowerPropagationChance = builder
                    .comment("Chance for flowers to propagate near hives")
                    .defineInRange("flowerPropagationChance", 0.15, 0, 1);

            speedUpgradeModifier = builder
                    .comment("Tick speed bonus from using speed upgrades in the farm")
                    .defineInRange("speedUpgradeModifier", 0.12, 0, 1);

            pollenChanceFromSieve = builder
                    .comment("Chance to get a pollen when using sieve upgrades in hives")
                    .defineInRange("pollenChanceFromSieve", 2, 1, 100);

            statsOnExternalCrops = builder
                    .comment("Add crop stats to crops from outside productivefarming (vanilla and other mods) that are tagged productivefarming:external_stat_crops")
                    .define("statsOnExternalCrops", true);

            spawnFlowersWithBonemeal = builder
                    .comment("Spawn productive farming flowers when using bonemeal on grass blocks")
                    .define("spawnFlowersWithBonemeal", false);

            builder.pop();
        }
    }
}