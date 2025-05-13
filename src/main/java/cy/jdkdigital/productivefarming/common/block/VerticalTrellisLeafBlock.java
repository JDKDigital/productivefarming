package cy.jdkdigital.productivefarming.common.block;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.common.block.entity.FencedVerticalCropBlockEntity;
import cy.jdkdigital.productivefarming.util.CropConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.TriState;
import org.jetbrains.annotations.Nullable;

public class VerticalTrellisLeafBlock extends FencedPlantLeafBlock
{
    public VerticalTrellisLeafBlock(CropConfig crop, Properties pProperties) {
        super(crop, pProperties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new FencedVerticalCropBlockEntity(pPos, pState);
    }

    @Override
    Direction[] validGrowthDirections(Level level, BlockPos pos) {
        return new Direction[]{Direction.UP};
    }

    @Override
    boolean isValidPropagationLeaf(ServerLevel level, BlockPos pos, Direction dir) {
        BlockState cropState = level.getBlockState(pos);
        return canSurvive(cropState, level, pos) && cropState.getValue(getAgeProperty()) > 2;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        var stateBelow = level.getBlockState(pos.below());
        return stateBelow.is(this) || super.canSurvive(state, level, pos);
    }

    @Override
    protected boolean canGrow(ServerLevel level, BlockPos pos, BlockState state) {
        return !level.getBlockState(pos.above()).isAir();
    }
}
