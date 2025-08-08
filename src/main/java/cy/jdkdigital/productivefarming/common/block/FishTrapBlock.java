//package cy.jdkdigital.productivefarming.common.block;
//
//import com.mojang.serialization.MapCodec;
//import cy.jdkdigital.productivefarming.common.block.entity.FishTrapBlockEntity;
//import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
//import cy.jdkdigital.productivelib.common.block.CapabilityContainerBlock;
//import net.minecraft.core.BlockPos;
//import net.minecraft.world.InteractionResult;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.BaseEntityBlock;
//import net.minecraft.world.level.block.Block;
//import net.minecraft.world.level.block.RenderShape;
//import net.minecraft.world.level.block.SimpleWaterloggedBlock;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import net.minecraft.world.level.block.entity.BlockEntityTicker;
//import net.minecraft.world.level.block.entity.BlockEntityType;
//import net.minecraft.world.level.block.state.BlockState;
//import net.minecraft.world.level.block.state.StateDefinition;
//import net.minecraft.world.level.block.state.properties.BlockStateProperties;
//import net.minecraft.world.phys.BlockHitResult;
//import org.jetbrains.annotations.Nullable;
//
//public class FishTrapBlock extends CapabilityContainerBlock implements SimpleWaterloggedBlock
//{
//    public static final MapCodec<FishTrapBlock> CODEC = simpleCodec(FishTrapBlock::new);
//
//    public FishTrapBlock(Properties pProperties) {
//        super(pProperties);
//        this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.OPEN, false));
//    }
//
//    @Override
//    protected MapCodec<? extends BaseEntityBlock> codec() {
//        return CODEC;
//    }
//
//    @Nullable
//    @Override
//    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
//        return new FishTrapBlockEntity(pPos, pState);
//    }
//
//    @Override
//    public RenderShape getRenderShape(BlockState pState) {
//        return RenderShape.MODEL;
//    }
//
//    @Override
//    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
//        pBuilder.add(BlockStateProperties.OPEN);
//    }
//
//    @Nullable
//    @Override
//    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
//        return level.isClientSide ? null : createTickerHelper(blockEntityType, FarmingRegistrator.FISH_TRAP_BLOCK_ENTITY.get(), FishTrapBlockEntity::tick);
//    }
//
//    @Override
//    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
//        var blockEntity = level.getBlockEntity(pos);
//        if (blockEntity instanceof FishTrapBlockEntity fishTrapBlockEntity) {
//            var outStack = fishTrapBlockEntity.inventoryHandler.getStackInSlot(FishTrapBlockEntity.SLOT_OUTPUT);
//            if (!outStack.isEmpty()) {
//                var heldItem = player.getUseItem();
//                if (heldItem.isEmpty()) {
//                    player.setItemInHand(player.getUsedItemHand(), outStack.copy());
//                } else if (heldItem.is(outStack.getItem())) {
//                    heldItem.setCount(heldItem.getCount() + outStack.getCount());
//                } else {
//                    popResource(level, pos, outStack.copy());
//                }
//                // TODO play some pickup sound
//                outStack.shrink(outStack.getCount());
//                return InteractionResult.sidedSuccess(level.isClientSide);
//            }
//            return InteractionResult.PASS;
//        }
//        return InteractionResult.PASS;
//    }
//}
