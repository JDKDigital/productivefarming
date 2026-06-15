package cy.jdkdigital.productivefarming.integrations.agritech;

import cy.jdkdigital.productivefarming.common.attachment.CropTraitState;
import cy.jdkdigital.productivefarming.common.item.CropBlockItem;
import cy.jdkdigital.productivefarming.registry.FarmingDataComponents;
import cy.jdkdigital.productivefarming.registry.ModTags;
import cy.jdkdigital.productivefarming.util.ExternalCropStats;
import cy.jdkdigital.productivefarming.util.TraitsHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

import java.util.List;

public class AgriTechStatsHelper
{
    private AgriTechStatsHelper() {}

    public static int adjustGrowthTime(int baseTime, ItemStack seed) {
        int growth = seed.getOrDefault(FarmingDataComponents.GROWTH, 0);
        if (growth <= 0) {
            return baseTime;
        }
        return Math.max(1, Math.round(baseTime / (growth + 1f)));
    }

    public static List<ItemStack> applyStats(List<ItemStack> drops, ItemStack seed) {
        if (drops.isEmpty() || seed.isEmpty() || !hasTraits(seed)) {
            return drops;
        }
        int yield = seed.getOrDefault(FarmingDataComponents.YIELD, 0);
        for (ItemStack drop : drops) {
            if (yield > 0) {
                drop.grow(yield);
            }
            if (drop.is(seed.getItem())) {
                TraitsHelper.copyTraitsToStack(seed, drop);
            }
        }
        return drops;
    }

    public static void increaseInputStat(ItemStacksResourceHandler inventory, ItemStack seed, RandomSource random) {
        if (seed.isEmpty() || !isStatSeed(seed)) {
            return;
        }
        boolean initialized = false;
        if (!hasTraits(seed)) {
            TraitsHelper.applyTraits(seed, 0, 0, 0, 0);
            initialized = true;
        }
        String stat = TraitsHelper.rollIncreasedStat(random, seed.typeHolder());
        if (stat != null) {
            CropTraitState increased = ExternalCropStats.fromSeedStack(seed).increase(stat);
            TraitsHelper.applyTraits(seed, increased.growth(), increased.yield(), increased.resistance(), increased.mutability());
        } else if (!initialized) {
            return;
        }
        inventory.set(0, ItemResource.of(seed), seed.getCount());
    }

    private static boolean isStatSeed(ItemStack seed) {
        if (seed.getItem() instanceof CropBlockItem) {
            return true;
        }
        return ExternalCropStats.isEnabled() && seed.is(ModTags.Items.EXTERNAL_SEEDS);
    }

    private static boolean hasTraits(ItemStack seed) {
        return seed.has(FarmingDataComponents.GROWTH)
                || seed.has(FarmingDataComponents.YIELD)
                || seed.has(FarmingDataComponents.RESISTANCE)
                || seed.has(FarmingDataComponents.MUTABILITY);
    }
}
