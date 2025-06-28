package cy.jdkdigital.productivefarming.common.block;

import cy.jdkdigital.productivefarming.util.CropConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

public class WaterCropBlock extends DoubleCropBlock implements SimpleWaterloggedBlock
{
    public WaterCropBlock(CropConfig crop, Properties pProperties) {
        super(crop, pProperties);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(this.getAgeProperty(), 0)
                        .setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER)
                        .setValue(BlockStateProperties.WATERLOGGED, false)
        );
    }

    @Override
    protected BlockState getStateForAge(BlockState state, Level level, BlockPos pos, int age) {
        return super.getStateForAge(age).setValue(BlockStateProperties.WATERLOGGED, state.getValue(BlockStateProperties.WATERLOGGED));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        var state = super.getStateForPlacement(context);
        if (state != null && state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF).equals(DoubleBlockHalf.LOWER)) {
            FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
            state = state.setValue(BlockStateProperties.WATERLOGGED, fluidstate.getType() == Fluids.WATER);
        }
        return state;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF).equals(DoubleBlockHalf.LOWER)) {
            if (state.getValue(getAgeProperty()) > 0) {
                BlockState aboveState = level.getBlockState(pos.above());
                return aboveState.is(this) && aboveState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF).equals(DoubleBlockHalf.UPPER) && state.getValue(BlockStateProperties.WATERLOGGED);
            }
            return state.getValue(BlockStateProperties.WATERLOGGED);
        } else {
            BlockState belowState = level.getBlockState(pos.below());
            if (state.getBlock() != this) return !state.getValue(BlockStateProperties.WATERLOGGED); //Forge: This function is called during world gen and placement, before this block is set, so if we are not 'here' then assume it's the pre-check.
            return belowState.is(this) && belowState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF).equals(DoubleBlockHalf.LOWER) && !state.getValue(BlockStateProperties.WATERLOGGED);
        }
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        if (state.getValue(BlockStateProperties.WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.WATERLOGGED);
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        super.performBonemeal(level, random, pos, state);
    }
}
