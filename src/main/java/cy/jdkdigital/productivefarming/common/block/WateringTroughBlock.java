package cy.jdkdigital.productivefarming.common.block;

import com.mojang.serialization.MapCodec;
import cy.jdkdigital.productivefarming.common.block.entity.WateringTroughBlockEntity;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.fluids.FluidUtil;
import org.jetbrains.annotations.Nullable;

public class WateringTroughBlock extends FeedingTroughBlock
{
    public static final MapCodec<WateringTroughBlock> CODEC = simpleCodec(WateringTroughBlock::new);

    public WateringTroughBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new WateringTroughBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, FarmingRegistrator.WATERING_TROUGH_BLOCK_ENTITY.get(), WateringTroughBlockEntity::tick);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(Items.WATER_BUCKET) && level.getBlockEntity(pos) instanceof WateringTroughBlockEntity wateringTroughBlockEntity) {
            FluidUtil.tryEmptyContainer(stack, wateringTroughBlockEntity.getFluidHandler(), 1000, player, true);
            if (state.hasProperty(LEVEL) && level instanceof ServerLevel serverLevel) {
                var fluidAmount = wateringTroughBlockEntity.getFluidHandler().getFluidAmount();
                var currentLevel = state.getValue(LEVEL);
                int newLevel = fluidAmount > 5000 ? 2 : fluidAmount > 0 ? 1 : 0;
                if (currentLevel != newLevel) {
                    serverLevel.setBlockAndUpdate(pos, state.setValue(LEVEL, newLevel));
                }
            }
            return ItemInteractionResult.CONSUME;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

//    @Override
//    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
//        if (level.getBlockEntity(pos) instanceof WateringTroughBlockEntity wateringTroughBlockEntity) {
//            player.openMenu(wateringTroughBlockEntity, packetBuffer -> packetBuffer.writeBlockPos(wateringTroughBlockEntity.getBlockPos()));
//            return InteractionResult.SUCCESS_NO_ITEM_USED;
//        }
//
//        return super.useWithoutItem(state, level, pos, player, hitResult);
//    }
}
