package cy.jdkdigital.productivefarming.common.block;

import com.mojang.serialization.MapCodec;
import cy.jdkdigital.productivefarming.common.block.entity.WateringTroughBlockEntity;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
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
        return level.isClientSide() ? null : createTickerHelper(blockEntityType, FarmingRegistrator.WATERING_TROUGH_BLOCK_ENTITY.get(), WateringTroughBlockEntity::tick);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(Items.WATER_BUCKET) && level.getBlockEntity(pos) instanceof WateringTroughBlockEntity wateringTroughBlockEntity) {
            ResourceHandler<FluidResource> fluidHandler = wateringTroughBlockEntity.getFluidHandler();
            FluidResource water = FluidResource.of(Fluids.WATER);
            int inserted;
            try (Transaction tx = Transaction.openRoot()) {
                inserted = fluidHandler.insert(water, 1000, tx);
                tx.commit();
            }
            if (inserted > 0) {
                if (!player.hasInfiniteMaterials()) {
                    stack.shrink(1);
                    if (!player.addItem(new ItemStack(Items.BUCKET))) {
                        player.drop(new ItemStack(Items.BUCKET), false);
                    }
                }
                if (state.hasProperty(LEVEL) && level instanceof ServerLevel serverLevel) {
                    int fluidAmount = fluidHandler.getAmountAsInt(0);
                    var currentLevel = state.getValue(LEVEL);
                    int newLevel = fluidAmount > 5000 ? 2 : fluidAmount > 0 ? 1 : 0;
                    if (currentLevel != newLevel) {
                        serverLevel.setBlockAndUpdate(pos, state.setValue(LEVEL, newLevel));
                    }
                }
            }
            return InteractionResult.CONSUME;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
}
