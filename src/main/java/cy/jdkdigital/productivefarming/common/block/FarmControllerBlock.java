package cy.jdkdigital.productivefarming.common.block;

import com.mojang.serialization.MapCodec;
import cy.jdkdigital.productivefarming.Config;
import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.common.block.entity.FarmControllerBlockEntity;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import cy.jdkdigital.productivefarming.registry.ModTags;
import cy.jdkdigital.productivelib.common.block.CapabilityContainerBlock;
import cy.jdkdigital.productivelib.common.block.IMultiBlockController;
import cy.jdkdigital.productivelib.exception.InvalidStructureException;
import cy.jdkdigital.productivelib.util.MultiBlockDetector;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class FarmControllerBlock extends CapabilityContainerBlock implements IMultiBlockController
{
    public static final MapCodec<FarmControllerBlock> CODEC = simpleCodec(FarmControllerBlock::new);

    public FarmControllerBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.ATTACHED, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateBuilder) {
        blockStateBuilder.add(BlockStateProperties.ATTACHED).add(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new FarmControllerBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide() ? null : createTickerHelper(blockEntityType, FarmingRegistrator.FARM_CONTROLLER_BLOCK_ENTITY.get(), FarmControllerBlockEntity::tick);
    }

    // TODO Configurations:
    //  harvest trees (on/off) option, when on it will cut down logs, leaves and big shrooms (leaves first), when off it will try to harvest fruits
    //  always harvests crops, bushes, gourds, sugar cane, cactus and cocoa beans


    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        if (level instanceof ServerLevel serverLevel && serverLevel.getBlockEntity(pos) instanceof FarmControllerBlockEntity blockEntity) {
            try {
                blockEntity.setMultiBlockData(FarmControllerBlock.detectMultiblock(serverLevel, pos));
            } catch (InvalidStructureException ise) {
            }
        }
        return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof FarmControllerBlockEntity blockEntity) {
            FluidResource nutrientWater = FluidResource.of(FarmingRegistrator.NUTRIENT_WATER.get());

            if (stack.is(FarmingRegistrator.NUTRIENT_WATER.get().getBucket())) {
                if (!level.isClientSide()) {
                    ResourceHandler<FluidResource> tank = blockEntity.getFluidHandler();
                    try (Transaction tx = Transaction.openRoot()) {
                        if (tank.insert(nutrientWater, 1000, tx) >= 1000) {
                            tx.commit();
                            exchangeBucket(player, stack, new ItemStack(Items.BUCKET));
                            level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                        }
                    }
                }
                return InteractionResult.SUCCESS;
            }

            if (stack.is(Items.BUCKET)) {
                if (!level.isClientSide()) {
                    ResourceHandler<FluidResource> tank = blockEntity.getFluidHandler();
                    try (Transaction tx = Transaction.openRoot()) {
                        if (tank.extract(nutrientWater, 1000, tx) >= 1000) {
                            tx.commit();
                            exchangeBucket(player, stack, new ItemStack(FarmingRegistrator.NUTRIENT_WATER.get().getBucket()));
                            level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                        }
                    }
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    private static void exchangeBucket(Player player, ItemStack used, ItemStack result) {
        if (player.hasInfiniteMaterials()) {
            return;
        }
        used.shrink(1);
        if (!player.addItem(result)) {
            player.drop(result, false);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof FarmControllerBlockEntity blockEntity) {
            try {
                blockEntity.setMultiBlockData(detectMultiblock(level, pos));
                level.setBlockAndUpdate(pos, blockEntity.getBlockState().setValue(BlockStateProperties.ATTACHED, true));
                player.sendSystemMessage(Component.translatable(ProductiveFarming.MODID + ".message.farm_formed", blockEntity.getMultiblockData().height() + 1));
                if (player instanceof ServerPlayer serverPlayer) {
                    openGui(serverPlayer, blockEntity);
                }
            } catch (InvalidStructureException ise) {
                level.setBlockAndUpdate(pos, blockEntity.getBlockState().setValue(BlockStateProperties.ATTACHED, false));
                player.sendSystemMessage(Component.translatable(ProductiveFarming.MODID + ".message.farm_invalid", ise.getMessage(), "" + level.getBlockState(ise.getPos())));
            }
        }
        return InteractionResult.SUCCESS;
    }

    public void openGui(ServerPlayer player, FarmControllerBlockEntity blockEntity) {
        player.openMenu(blockEntity, packetBuffer -> packetBuffer.writeBlockPos(blockEntity.getBlockPos()));
    }

    public static MultiBlockDetector.MultiBlockData detectMultiblock(Level level, BlockPos pos) throws InvalidStructureException {
        return MultiBlockDetector.detectStructure(level, pos, ModTags.Blocks.FARM_BLOCKS, null, false, true, Config.SERVER.farmMaxVolume.get(), Config.SERVER.farmMaxCircumference.get(), Config.SERVER.farmMaxHeight.get());
    }
}
