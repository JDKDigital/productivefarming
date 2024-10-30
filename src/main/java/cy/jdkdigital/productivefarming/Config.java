package cy.jdkdigital.productivefarming;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config
{
    private static final ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SERVER_CONFIG;
    public static final Server SERVER = new Server(SERVER_BUILDER);

    static {
        SERVER_CONFIG = SERVER_BUILDER.build();
    }

    public static class Server
    {
        public final ModConfigSpec.IntValue feedingTroughTickRate;
        public final ModConfigSpec.IntValue wateringTroughTickRate;
        public final ModConfigSpec.IntValue fishTrapTickRate;
        public final ModConfigSpec.IntValue childSeparatorTickRate;

        public Server(ModConfigSpec.Builder builder) {
            builder.push("General");

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

            builder.pop();
        }
    }
}