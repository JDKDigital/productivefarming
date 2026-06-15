package cy.jdkdigital.productivefarming.integrations.ponder.scenes;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.common.block.ProductiveCropBlock;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.ArrayList;
import java.util.List;

public class MultifarmScenes
{
    public static void building(SceneBuilder scene, SceneBuildingUtil util) {

        scene.title("farm_building", "Building the Multiblock Farm");

        for (int x = 1; x < 8; x++) {
            for (int z = 1; z < 6; z++) {
                scene.world().showSection(util.select().position(x, 0, z), Direction.DOWN);
                scene.idle(1);
            }

            if(x == 1) {
                scene.overlay()
                        .showText(60)
                        .text("The minimum size for the farm is 3x3, corners are optional");
            }
        }

        scene.idle(0);

        scene.overlay()
                .showText(60)
                .pointAt(util.vector().of(3.5, 1, 3.5))
                .text("The inside of the farm can be any block you need for your crops")
                .placeNearTarget();

        scene.idle(70);
        scene.addKeyframe();

        scene.overlay()
                .showText(60)
                .pointAt(util.vector().of(3, 2, 1))
                .text("Additionally, the farm multiblock must have a Farm Controller...")
                .placeNearTarget();

        scene.idle(5);

        scene.world().replaceBlocks(util.select().position(3,0,1), FarmingRegistrator.FARM_CONTROLLER.get().defaultBlockState(), true);

        scene.idle(50);

        scene.world().modifyBlock(util.grid().at(3,0,1), state -> state.setValue(BlockStateProperties.ATTACHED, true), false);
    }

    public static void farming(SceneBuilder scene, SceneBuildingUtil util) {

        scene.title("farm_farming", "Farming the Multiblock Farm");

        for (int x = 1; x < 8; x++) {
            for (int z = 1; z < 6; z++) {
                scene.world().showSection(util.select().position(x, 0, z), Direction.DOWN);
            }
        }
        scene.world().setBlock(util.grid().at(4, 0, 3), Blocks.WATER.defaultBlockState(), false);

        scene.idle(0);
        scene.world().replaceBlocks(util.select().position(3,0,1), FarmingRegistrator.FARM_CONTROLLER.get().defaultBlockState().setValue(BlockStateProperties.ATTACHED, true), false);

        scene.idle(10);
        BlockState watercress = BuiltInRegistries.BLOCK.get(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "watercress")).defaultBlockState().setValue(ProductiveCropBlock.AGE_6, 6).setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER).setValue(BlockStateProperties.WATERLOGGED, true);
        scene.world().setBlock(util.grid().at(4, 0, 3), watercress, false);
        for (int x = 2; x < 7; x++) {
            for (int z = 2; z < 5; z++) {
                scene.world().showSection(util.select().position(x, 1, z), Direction.DOWN);
            }
        }
        scene.overlay()
                .showText(60)
                .pointAt(util.vector().of(3.5, 2, 2.5))
                .text("Crops planted on the farm will be automatically harvested")
                .placeNearTarget();

        scene.idle(20);
        scene.addKeyframe();

        for (int x = 2; x < 7; x++) {
            for (int z = 2; z < 5; z++) {
                scene.world().modifyBlock(util.grid().at(x, 1, z), state -> state.setValue(ProductiveCropBlock.AGE_6, 1), false);
                scene.idle(1);
            }
        }
        scene.idle(30);

        scene.overlay()
                .showText(60)
                .pointAt(util.vector().of(3.5, 2, 2.5))
                .text("When crops grow they have a small chance to improve their traits")
                .placeNearTarget();
        scene.idle(20);
        for (int x = 2; x < 7; x++) {
            for (int z = 2; z < 5; z++) {
                scene.world().modifyBlock(util.grid().at(x, 1, z), state -> state.setValue(ProductiveCropBlock.AGE_6, 6), false);
                scene.idle(1);
            }
        }
        scene.idle(40);
    }

    public static void specialCrops(SceneBuilder scene, SceneBuildingUtil util) {

        scene.title("farm_special_crops", "Special Crops");

    }

    public static void fishFarming(SceneBuilder scene, SceneBuildingUtil util) {

        scene.title("fish_farm", "Fish Farms");

        for (int x = 1; x < 8; x++) {
            for (int z = 1; z < 6; z++) {
                for (int y = 0; y < 5; y++) {
                    scene.world().showSection(util.select().position(x, y, z), Direction.DOWN);
                }
            }
        }

        scene.idle(0);
        scene.world().replaceBlocks(util.select().position(3,3,1), FarmingRegistrator.FARM_CONTROLLER.get().defaultBlockState().setValue(BlockStateProperties.ATTACHED, true), false);

        scene.overlay()
                .showText(60)
                .pointAt(util.vector().of(3.5, 4, 2.5))
                .text("If you make your farm taller you can use it as a fish farm")
                .placeNearTarget();
        scene.idle(70);
        scene.overlay()
                .showText(60)
                .pointAt(util.vector().of(3.5, 4, 2.5))
                .text("Creatures inside the farm boundaries will be automatically bred and slaughtered")
                .placeNearTarget();
        scene.idle(70);
        scene.overlay()
                .showText(60)
                .pointAt(util.vector().of(3.5, 4, 2.5))
                .text("Fish will produce nutrient rich water which can be pumped to a crop farm for increased growth")
                .placeNearTarget();
        scene.idle(70);
//
        scene.addKeyframe();

        scene.idle(10);

        for (int x = 2; x < 7; x++) {
            for (int z = 2; z < 5; z++) {
                if (x != 4 || z != 3) {
                    scene.world().setBlock(util.grid().at(x, 3, z), Blocks.FARMLAND.defaultBlockState().setValue(BlockStateProperties.MOISTURE, 7), true);
                    scene.idle(1);
                    scene.world().setBlock(util.grid().at(x, 4, z), Blocks.POTATOES.defaultBlockState().setValue(BlockStateProperties.AGE_7, 7), true);
                    scene.idle(1);
                }
            }
        }

        scene.idle(10);

        scene.overlay()
                .showText(60)
                .pointAt(util.vector().of(3.5, 4, 2.5))
                .text("You can also combine the two farms and have an aquaponics system")
                .placeNearTarget();

        scene.idle(40);
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
