package cy.jdkdigital.productivefarming.common.block;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.common.block.entity.TrellisLeafBlockEntity;
import cy.jdkdigital.productivefarming.util.CropConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

public class TrellisLeafBlock extends FencedPlantLeafBlock
{
    public TrellisLeafBlock(CropConfig crop, Properties pProperties) {
        super(crop, pProperties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new TrellisLeafBlockEntity(pPos, pState);
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
    }
}
