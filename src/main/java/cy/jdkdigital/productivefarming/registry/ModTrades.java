package cy.jdkdigital.productivefarming.registry;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.util.CropConfig;
import cy.jdkdigital.productivefarming.util.RecipeHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.trading.TradeCost;
import net.minecraft.world.item.trading.VillagerTrade;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ModTrades
{
    private ModTrades() {}

    private static final int SEED_COUNT = 4;

    private static List<SeedTrade> level1;
    private static List<SeedTrade> level2;
    private static List<SeedTrade> level3;
    private static List<SeedTrade> level4;

    public record SeedTrade(ResourceKey<VillagerTrade> key, Item seed, int emeraldCost, int xp) {}

    private static synchronized void build() {
        if (level1 != null) return;
        level1 = collect(1, FarmingRegistrator.CROPS, 1, 2);
        List<SeedTrade> l2 = new ArrayList<>();
        l2.addAll(collect(2, FarmingRegistrator.HERBS, 1, 3));
        l2.addAll(collect(2, FarmingRegistrator.BERRIES, 1, 4));
        level2 = List.copyOf(l2);
        List<SeedTrade> l3 = new ArrayList<>();
        l3.addAll(collect(3, FarmingRegistrator.SHROOMS, 2, 4));
        l3.addAll(collect(3, FarmingRegistrator.GRAPES, 2, 4));
        l3.addAll(collect(3, FarmingRegistrator.STEMS, 2, 4));
        level3 = List.copyOf(l3);
        List<SeedTrade> l4 = new ArrayList<>();
        l4.addAll(collect(4, FarmingRegistrator.TRELLIS, 2, 5));
        l4.addAll(collect(4, FarmingRegistrator.VERTICAL_TRELLIS, 2, 5));
        level4 = List.copyOf(l4);
    }

    private static List<SeedTrade> collect(int level, List<CropConfig> crops, int emeraldCost, int xp) {
        List<SeedTrade> trades = new ArrayList<>();
        for (CropConfig crop : crops) {
            if (RecipeHelper.isMutatedCrop(crop)) continue;
            String seedName = crop.name() + (crop.hasSeed() ? "_seeds" : "");
            ResourceKey<Item> seedKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, seedName));
            Optional<Item> seed = BuiltInRegistries.ITEM.getOptional(seedKey);
            if (seed.isEmpty()) continue;
            trades.add(new SeedTrade(tradeKey("level_" + level + "/" + seedName), seed.get(), emeraldCost, xp));
        }
        return trades;
    }

    public static List<SeedTrade> level1() {
        build();
        return level1;
    }

    public static List<SeedTrade> level2() {
        build();
        return level2;
    }

    public static List<SeedTrade> level3() {
        build();
        return level3;
    }

    public static List<SeedTrade> level4() {
        build();
        return level4;
    }

    public static void bootstrapTrades(BootstrapContext<VillagerTrade> context) {
        registerAll(context, level1());
        registerAll(context, level2());
        registerAll(context, level3());
        registerAll(context, level4());
    }

    private static void registerAll(BootstrapContext<VillagerTrade> context, List<SeedTrade> trades) {
        for (SeedTrade trade : trades) {
            context.register(trade.key(), new VillagerTrade(
                    new TradeCost(Items.EMERALD, trade.emeraldCost()),
                    new ItemStackTemplate(trade.seed(), SEED_COUNT),
                    16,
                    trade.xp(),
                    0.05F,
                    Optional.empty(),
                    List.of()));
        }
    }

    private static ResourceKey<VillagerTrade> tradeKey(String path) {
        return ResourceKey.create(Registries.VILLAGER_TRADE,
                Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "farmer/" + path));
    }
}
