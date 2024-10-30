package cy.jdkdigital.productivefarming.util;

import com.mojang.datafixers.util.Pair;
import cy.jdkdigital.productivefarming.common.block.entity.FarmControllerBlockEntity;
import cy.jdkdigital.productivefarming.exception.InvalidStructureException;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import cy.jdkdigital.productivefarming.registry.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Arrays;

public class FarmValidator
{
    public static String FARM_TYPE_CROP = "crop_farm";
    public static String FARM_TYPE_FISH = "fish_farm";

    public static FarmConfig validateFarmStructureBlocks(Level level, BlockPos controllerPos) throws InvalidStructureException {
        if (level.getBlockEntity(controllerPos) instanceof FarmControllerBlockEntity farmController) {
            return validateFarmStructureBlocks(level, farmController);
        }
        return null;
    }

    public static FarmConfig validateFarmStructureBlocks(Level level, BlockEntity controller) throws InvalidStructureException {
        BlockPos initialPosition = controller.getBlockPos();

        Direction dir = controller.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).getCounterClockWise();

        var corners = findCorners(level, dir, controller.getBlockPos());

        String type = FARM_TYPE_CROP;

        // Height
        var maxHeight = 40;
        var pointer = new BlockPos.MutableBlockPos(corners.getSecond().getX(), corners.getSecond().getY(), corners.getSecond().getZ());
        // find the lowest farm structure block
        while (maxHeight-- > 0 && level.getBlockState(pointer.below()).is(ModTags.FARM_BLOCKS)) {
            pointer.move(Direction.DOWN);
        }
        BlockPos lowest = pointer.immutable();

        // validate lower ring
        int farmHeight = corners.getSecond().getY() - lowest.getY();
        if (farmHeight > 0) {
            try {
                findCorners(level, dir, new BlockPos(initialPosition.getX(), lowest.getY(), initialPosition.getZ()));
                type = FARM_TYPE_FISH;
            } catch (InvalidStructureException ise) {
                lowest = corners.getSecond();
            }

            if (type.equals(FARM_TYPE_FISH)) {
                // Validate each column in-between top and bottom
                for (var i = 1; i <= farmHeight; i++) {
                    for (BlockPos pos : Arrays.asList(
                            new BlockPos(corners.getSecond().getX(), corners.getSecond().getY() - i, corners.getSecond().getZ()),
                            new BlockPos(corners.getFirst().getX(), corners.getSecond().getY() - i, corners.getSecond().getZ()),
                            new BlockPos(corners.getSecond().getX(), corners.getSecond().getY() - i, corners.getFirst().getZ()),
                            new BlockPos(corners.getFirst().getX(), corners.getSecond().getY() - i, corners.getFirst().getZ())
                    )) {
                        var state = level.getBlockState(pos);
                        if (level.getBlockState(pos).is(Blocks.STONE_BRICKS)) {
                            level.setBlockAndUpdate(pos, FarmingRegistrator.FARM_BLOCK.get().defaultBlockState());
                        }
                        if (!state.is(ModTags.FARM_BLOCKS)) {
                            throw new InvalidStructureException(pos);
                        }
                    }
                }
            }
        }

        return new FarmConfig(type, corners.getFirst(), lowest);
    }

    private static Pair<BlockPos, BlockPos> findCorners(Level level, Direction dir, BlockPos initialPosition) throws InvalidStructureException {
        BlockPos firstCorner = new BlockPos(initialPosition);
        BlockPos secondCorner = new BlockPos(initialPosition);

        var pointer = new BlockPos.MutableBlockPos(initialPosition.getX(), initialPosition.getY(), initialPosition.getZ());

        int turns = 0, maxSize = 200;
        while (turns <= 4 && maxSize-- > 0 && level.getBlockState(pointer.relative(dir)).is(ModTags.FARM_BLOCKS) && !pointer.relative(dir).equals(initialPosition)) {
            pointer.move(dir);
            if (level.getBlockState(pointer).is(Blocks.STONE_BRICKS)) {
                level.setBlockAndUpdate(new BlockPos(pointer), FarmingRegistrator.FARM_BLOCK.get().defaultBlockState());
            }
            // if next block is not a valid farm structure block, turn and search in new direction
            var nextBlockState = level.getBlockState(pointer.relative(dir));

            if (turns < 4 && (nextBlockState.isAir() || !nextBlockState.is(ModTags.FARM_BLOCKS))) {
                if (turns == 0) {
                    firstCorner = pointer.immutable();
                }
                if (turns == 2) {
                    secondCorner = pointer.immutable();
                }
                dir = dir.getClockWise();
                turns++;
            }
        }

        if (turns == 4 && pointer.move(dir).immutable().equals(initialPosition)) {
            return Pair.of(firstCorner, secondCorner);
        }
        throw new InvalidStructureException(pointer.immutable());
    }
}
