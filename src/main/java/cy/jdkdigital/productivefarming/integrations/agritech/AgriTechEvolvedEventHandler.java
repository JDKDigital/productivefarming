package cy.jdkdigital.productivefarming.integrations.agritech;

import com.misterd.agritechevolved.integration.PlanterPostHarvestEvent;
import com.misterd.agritechevolved.integration.PlanterPreHarvestEvent;
import com.misterd.agritechevolved.integration.PlanterProcessingTimeEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;

public final class AgriTechEvolvedEventHandler
{
    private AgriTechEvolvedEventHandler() {}

    public static void register() {
        NeoForge.EVENT_BUS.addListener(AgriTechEvolvedEventHandler::onProcessingTime);
        NeoForge.EVENT_BUS.addListener(AgriTechEvolvedEventHandler::onPreHarvest);
        NeoForge.EVENT_BUS.addListener(AgriTechEvolvedEventHandler::onPostHarvest);
    }

    private static void onProcessingTime(PlanterProcessingTimeEvent event) {
        event.setProcessingTime(AgriTechStatsHelper.adjustGrowthTime(event.getProcessingTime(), event.getSeed()));
    }

    private static void onPreHarvest(PlanterPreHarvestEvent event) {
        Level level = event.getPlanter().getLevel();
        if (level == null) {
            return;
        }
        ItemStack seed = event.getSeed();
        AgriTechStatsHelper.increaseInputStat(seed, level.getRandom());
        event.setSeed(seed);
    }

    private static void onPostHarvest(PlanterPostHarvestEvent event) {
        AgriTechStatsHelper.applyStats(event.getDrops(), event.getSeed());
    }
}
