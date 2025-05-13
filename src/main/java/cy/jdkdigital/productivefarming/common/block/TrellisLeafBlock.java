package cy.jdkdigital.productivefarming.common.block;

import cy.jdkdigital.productivefarming.util.CropConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class TrellisLeafBlock extends FencedPlantLeafBlock
{
    public TrellisLeafBlock(CropConfig crop, Properties pProperties) {
        super(crop, pProperties);
    }

    @Override
    Direction[] validGrowthDirections(Level level, BlockPos pos) {
        var growthFromState = level.getBlockState(pos);
        if (growthFromState.is(this) && growthFromState.getValue(BlockStateProperties.DISTANCE) >= 3) {
            return new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
        }
        return new Direction[]{Direction.UP};
    }

    @Override
    boolean isValidPropagationLeaf(ServerLevel level, BlockPos pos, Direction dir) {
        var stateBelow = level.getBlockState(pos.below());
        var attachedState = level.getBlockState(pos);
        return stateBelow.is(this.stem) ||
                (stateBelow.is(this) && stateBelow.getValue(BlockStateProperties.DISTANCE) < 2) ||
                (dir.getAxis().isHorizontal() && attachedState.is(this) && attachedState.getValue(BlockStateProperties.DISTANCE) < 5);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        var attachedState = level.getBlockState(pos.relative(state.getValue(BlockStateProperties.FACING)));
        return hasSufficientLight(level, pos) && (attachedState.is(this.stem) || attachedState.is(this));
    }
}
