package cy.jdkdigital.productivefarming.common.block;

import cy.jdkdigital.productivefarming.common.block.entity.CropBlockEntity;
import cy.jdkdigital.productivefarming.util.CropConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import javax.annotation.Nullable;
import java.util.List;

public class DoubleCropBlock extends ProductiveCropBlock
{
    public DoubleCropBlock(CropConfig crop, Properties pProperties) {
        super(crop, pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(this.getAgeProperty(), 0).setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    protected void postHarvest(BlockState state, Level level, BlockPos pos, Player player) {
        // Set the other half to the same post harvest age
        BlockPos otherPos = state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF).equals(DoubleBlockHalf.LOWER) ? pos.above() : pos.below();
        level.setBlock(otherPos, level.getBlockState(otherPos).setValue(this.getAgeProperty(), getHarvestedAge()), Block.UPDATE_CLIENTS);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // verify that the block space on top is available
        return context.getClickedPos().getY() < context.getLevel().getMaxBuildHeight() - 1 && context.getLevel().getBlockState(context.getClickedPos().above()).canBeReplaced(context) ? super.getStateForPlacement(context) : null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        level.setBlock(pos.above(), this.defaultBlockState().setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER), 3);
        if (level.getBlockEntity(pos) instanceof CropBlockEntity lowerCropBlockEntity && level.getBlockEntity(pos.above()) instanceof CropBlockEntity upperCropBlockEntity) {
            upperCropBlockEntity.copyTraitsFromOtherCrop(lowerCropBlockEntity);
        }
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF).equals(DoubleBlockHalf.LOWER)) {
            if (state.getValue(getAgeProperty()) > 0) {
                BlockState aboveState = level.getBlockState(pos.above());
                return aboveState.is(this) && aboveState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF).equals(DoubleBlockHalf.UPPER) && super.canSurvive(state, level, pos);
            }
            return super.canSurvive(state, level, pos);
        } else {
            BlockState belowState = level.getBlockState(pos.below());
            if (state.getBlock() != this) return super.canSurvive(state, level, pos); //Forge: This function is called during world gen and placement, before this block is set, so if we are not 'here' then assume it's the pre-check.
            return belowState.is(this) && belowState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF).equals(DoubleBlockHalf.LOWER);
        }
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        if (state.is(this)) {
            if (state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF).equals(DoubleBlockHalf.LOWER)) {
                this.growCrops(level, pos, state);
                net.neoforged.neoforge.common.CommonHooks.fireCropGrowPost(level, pos, state);
            } else {
                performBonemeal(level, random, pos.below(), level.getBlockState(pos.below()));
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.DOUBLE_BLOCK_HALF);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return super.isRandomlyTicking(state) && state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF).equals(DoubleBlockHalf.LOWER);
    }

    @Override
    public int getHarvestedAge() {
        return 2;
    }
}
