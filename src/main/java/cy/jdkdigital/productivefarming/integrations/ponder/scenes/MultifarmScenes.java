package cy.jdkdigital.productivefarming.integrations.ponder.scenes;

import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.ArrayList;
import java.util.List;

public class MultifarmScenes
{
    public static void building(SceneBuilder scene, SceneBuildingUtil util) {

        scene.title("farm_building", "Building the Multiblock Farm");

        for (int x = 1; x < 6; x++) {
            for (int z = 1; z < 6; z++) {
                scene.world().showSection(util.select().position(x, 0, z), Direction.DOWN);
                scene.idle(1);
            }

            if(x == 1) {
                scene.overlay()
                        .showText(80)
                        .text("The minimum size for the farm is 3x3x2, as it requires at least a single farm block within the structure. Corners are optional");
            }
        }

        scene.idle(0);

        scene.overlay()
                .showText(50)
                .pointAt(util.vector().of(3.5, 1, 3.5))
                .text("The bottom of the foundry has to be made up of Heating Coils")
                .placeNearTarget();

//        for (int x = 2; x < 5; x++) {
//            for (int z = 2; z < 5; z++) {
//                scene.world().replaceBlocks(util.select().position(x, 0, z), MetalworksRegistrator.LIQUID_HEATING_COIL.get().defaultBlockState(), true);
//                scene.idle(1);
//            }
//        }

        scene.idle(60);
        scene.addKeyframe();

        for(BlockPos pos : generateRingCoordinates(new BlockPos(1, 1, 1), new BlockPos(5, 1, 5))) {
            scene.world().showSection(util.select().position(pos), Direction.DOWN);
            scene.idle(1);
        }

        scene.idle(10);

        scene.overlay()
                .showText(40)
                .pointAt(util.vector().of(3, 2, 1))
                .text("Additionally, the farm multiblock must have a Farm Controller...")
                .placeNearTarget();

        scene.idle(5);

        scene.world().replaceBlocks(util.select().position(3,1,1), FarmingRegistrator.FARM_CONTROLLER.get().defaultBlockState(), true);

        scene.idle(50);

        scene.world().modifyBlock(util.grid().at(3,1,1), state -> state.setValue(BlockStateProperties.ATTACHED, true), false);

//        for (int x = 2; x < 5; x++) {
//            for (int z = 2; z < 5; z++) {
//                scene.world().modifyBlock(util.grid().at(x, 0, z), state -> state.setValue(BlockStateProperties.ATTACHED, true), false);
//            }
//        }
    }

    private static List<BlockPos> generateRingCoordinates(BlockPos bottomLeft, BlockPos topRight) {
        List<BlockPos> coords = new ArrayList<>();

        int x0 = bottomLeft.getX(),  z0 = bottomLeft.getZ();
        int x1 = topRight.getX(),    z1 = topRight.getZ();
        int y  = bottomLeft.getY();

        for (int z = z0; z <= z1; z++) {
            coords.add(new BlockPos(x0, y, z));
        }

        for (int x = x0 + 1; x <= x1; x++) {
            coords.add(new BlockPos(x, y, z1));
        }

        for (int z = z1 - 1; z >= z0; z--) {
            coords.add(new BlockPos(x1, y, z));
        }

        for (int x = x1 - 1; x > x0; x--) {
            coords.add(new BlockPos(x, y, z0));
        }

        return coords;
    }
}
