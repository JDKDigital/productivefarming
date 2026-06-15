package cy.jdkdigital.productivefarming.common.block;

import com.mojang.serialization.MapCodec;
import cy.jdkdigital.productivefarming.common.block.entity.FeedingTroughBlockEntity;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FeedingTroughBlock extends BaseEntityBlock
{
    public static final MapCodec<FeedingTroughBlock> CODEC = simpleCodec(FeedingTroughBlock::new);
    protected static final VoxelShape SHAPE = Block.box(0.0D, 4.0D, 0.0D, 16.0D, 12.0D, 16.0D);
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 2);

    public FeedingTroughBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, 0));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new FeedingTroughBlockEntity(pPos, pState);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(LEVEL);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide() ? null : createTickerHelper(blockEntityType, FarmingRegistrator.FEEDING_TROUGH_BLOCK_ENTITY.get(), FeedingTroughBlockEntity::tick);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof FeedingTroughBlockEntity feedingTroughBlockEntity) {
            player.openMenu(feedingTroughBlockEntity, packetBuffer -> packetBuffer.writeBlockPos(feedingTroughBlockEntity.getBlockPos()));
            return InteractionResult.SUCCESS;
        }

        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        if (level.getBlockEntity(pos) instanceof FeedingTroughBlockEntity feedingTroughBlockEntity) {
            var itemHandler = feedingTroughBlockEntity.getItemHandler();
            for (int i = 0; i < itemHandler.size(); i++) {
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), itemHandler.getResource(i).toStack(itemHandler.getAmountAsInt(i)));
            }
        }
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
    }

}
