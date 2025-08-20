package cy.jdkdigital.productivefarming.integrations.ponder;

import cy.jdkdigital.productivefarming.integrations.ponder.scenes.MultifarmScenes;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;

public class FarmingPonderScenes
{
    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderSceneRegistrationHelper<DeferredHolder<Block, Block>> HELPER = helper.withKeyFunction(DeferredHolder::getId);

//        addBuilding(HELPER, FarmingRegistrator.FARM_CONTROLLER);
    }

    private static void addBuilding(PonderSceneRegistrationHelper<DeferredHolder<Block, Block>> HELPER, DeferredHolder<Block, Block> value) {
        HELPER.forComponents(value).addStoryBoard(
                "multifarm",
                MultifarmScenes::building,
                FarmingPonderTags.FARM_CONTROLLER_BLOCKS,
                FarmingPonderTags.FARM_BUILDING_BLOCKS,
                FarmingPonderTags.FARM_FARMLAND
        );
    }
}
