package cy.jdkdigital.productivefarming.integrations.productivebees;

import cy.jdkdigital.productivebees.common.block.entity.AdvancedBeehiveBlockEntity;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivefarming.Config;
import cy.jdkdigital.productivefarming.util.FarmUtil;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivelib.event.BeeReleaseEvent;
import cy.jdkdigital.productivelib.event.CollectValidUpgradesEvent;
import cy.jdkdigital.productivelib.registry.LibItems;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;

import java.util.ArrayList;
import java.util.List;

public class CompatHandler
{
    public static void collectValidUpgrades(CollectValidUpgradesEvent event) {
        if (event.getBlockEntity() instanceof AdvancedBeehiveBlockEntity) {
            event.addValidUpgrade(LibItems.UPGRADE_POLLEN_SIEVE.get());
        }
    }

    public static void beeRelease(BeeReleaseEvent event) {
        if (event.getLevel() instanceof ServerLevel level && event.getBeeState().equals(BeehiveBlockEntity.BeeReleaseStatus.HONEY_DELIVERED) && event.getBlockEntity() instanceof BeehiveBlockEntity beehiveBlockEntity && event.getBee().getHivePos() != null) {
            // Scan for flower and crop blocks around the hive, 4 block radius + 2 per range upgrade
            int distance = 4 + (beehiveBlockEntity instanceof AdvancedBeehiveBlockEntity advancedBeehiveBlockEntity ? (2 * advancedBeehiveBlockEntity.getUpgradeCount(LibItems.UPGRADE_RANGE.get())) : 0);
            boolean isSpecialPollinator = event.getBee() instanceof ProductiveBee pBee && pBee.getBeeName().equals("crop_duster");
            List<Identifier> uniqueCrops = new ArrayList<>();
            FarmUtil.pollinateCrops(level, event.getBee().getHivePos(), distance, isSpecialPollinator, uniqueCrops);
            if (!uniqueCrops.isEmpty()) {
                // Check for pollen sieve upgrade and collect pollen from nearby leaf
                if (beehiveBlockEntity instanceof AdvancedBeehiveBlockEntity advancedBeehiveBlockEntity) {
                    int sieveUpgrades = advancedBeehiveBlockEntity.getUpgradeCount(LibItems.UPGRADE_POLLEN_SIEVE.get());
                    if (sieveUpgrades > 0 && level.getRandom().nextInt(100) < (Config.SERVER.pollenChanceFromSieve.get() * (isSpecialPollinator ? 5 : 1))) {
                        Identifier pollenCrop = uniqueCrops.get(level.getRandom().nextInt(uniqueCrops.size()));
                        var pollenStack = FarmUtil.getPollen(pollenCrop);
                        ((InventoryHandlerHelper.BlockEntityItemStackHandler) advancedBeehiveBlockEntity.inventoryHandler).addOutput(pollenStack);
                    }
                }
            }
        }
    }
}
