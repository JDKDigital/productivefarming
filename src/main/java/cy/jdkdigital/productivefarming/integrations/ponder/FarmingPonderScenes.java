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

        addBuilding(HELPER, FarmingRegistrator.FARM_CONTROLLER);
        addFarming(HELPER, FarmingRegistrator.FARM_CONTROLLER);
//        addSpecialCrops(HELPER, FarmingRegistrator.FARM_CONTROLLER);
        addFishFarming(HELPER, FarmingRegistrator.FARM_CONTROLLER);
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

    private static void addFarming(PonderSceneRegistrationHelper<DeferredHolder<Block, Block>> HELPER, DeferredHolder<Block, Block> value) {
        HELPER.forComponents(value).addStoryBoard(
                "farming",
                MultifarmScenes::farming,
                FarmingPonderTags.FARM_CONTROLLER_BLOCKS,
                FarmingPonderTags.FARM_BUILDING_BLOCKS,
                FarmingPonderTags.FARM_FARMLAND
        );
    }

    private static void addSpecialCrops(PonderSceneRegistrationHelper<DeferredHolder<Block, Block>> HELPER, DeferredHolder<Block, Block> value) {
        HELPER.forComponents(value).addStoryBoard(
                "special_crops",
                MultifarmScenes::specialCrops,
                FarmingPonderTags.FARM_CONTROLLER_BLOCKS,
                FarmingPonderTags.FARM_BUILDING_BLOCKS,
                FarmingPonderTags.FARM_FARMLAND
        );
    }

    private static void addFishFarming(PonderSceneRegistrationHelper<DeferredHolder<Block, Block>> HELPER, DeferredHolder<Block, Block> value) {
        HELPER.forComponents(value).addStoryBoard(
                "aquaponics",
                MultifarmScenes::fishFarming,
                FarmingPonderTags.FARM_CONTROLLER_BLOCKS,
                FarmingPonderTags.FARM_BUILDING_BLOCKS,
                FarmingPonderTags.FARM_FARMLAND
        );
    }
}
