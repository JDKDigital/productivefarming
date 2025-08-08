//package cy.jdkdigital.productivefarming.common.block.entity;
//
//import cy.jdkdigital.productivefarming.Config;
//import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
//import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
//import net.minecraft.core.BlockPos;
//import net.minecraft.core.Direction;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.sounds.SoundEvents;
//import net.minecraft.sounds.SoundSource;
//import net.minecraft.tags.FluidTags;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import net.minecraft.world.level.block.state.BlockState;
//import net.minecraft.world.level.storage.loot.BuiltInLootTables;
//import net.minecraft.world.level.storage.loot.LootParams;
//import net.minecraft.world.level.storage.loot.LootTable;
//import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
//import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
//import net.minecraft.world.phys.Vec3;
//import net.neoforged.neoforge.items.IItemHandlerModifiable;
//
//import java.util.List;
//
//public class FishTrapBlockEntity extends TickingBlockEntity
//{
//    ItemStack buffer = ItemStack.EMPTY;
//
//    final public static int SLOT_OUTPUT = 0;
//
//    public final IItemHandlerModifiable inventoryHandler = new InventoryHandlerHelper.BlockEntityItemStackHandler(1, this){
//        @Override
//        public boolean isItemValid(int slot, ItemStack item) {
//            return true;
//        }
//
//        @Override
//        public boolean isInputSlot(int slot) {
//            return false;
//        }
//    };
//
//    public FishTrapBlockEntity(BlockPos pPos, BlockState pBlockState) {
//        super(FarmingRegistrator.FISH_TRAP_BLOCK_ENTITY.get(), pPos, pBlockState);
//    }
//
//    @Override
//    int tickRate() {
//        return Config.SERVER.fishTrapTickRate.get();
//    }
//
//    @Override
//    public void tickServer(ServerLevel level, BlockPos blockPos, BlockState blockState, TickingBlockEntity blockEntity) {
//        if (blockEntity instanceof FishTrapBlockEntity fishTrapBlockEntity && fishTrapBlockEntity.buffer.isEmpty() && isValidFishingSpot(level, blockPos)) {
//            // TODO trash and treasure loot?
//            LootTable table = level.getServer().reloadableRegistries().getLootTable(BuiltInLootTables.FISHING);
//            LootParams lootparams = (new LootParams.Builder(level))
//                    .withParameter(LootContextParams.ORIGIN, Vec3.atLowerCornerOf(blockEntity.getBlockPos()))
//                    .create(LootContextParamSets.EMPTY);
//
//            List<ItemStack> possibleItems = table.getRandomItems(lootparams);
//            var stack = possibleItems.get(level.random.nextInt(possibleItems.size()));
//
//            fishTrapBlockEntity.buffer = stack.copy();
//
//            level.playSound(null, blockPos, SoundEvents.FISHING_BOBBER_SPLASH, SoundSource.BLOCKS, 0.25F, 0.4F);
//        }
//    }
//
//    public static <E extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState blockState, FishTrapBlockEntity blockEntity) {
//        blockEntity.tickHandler(level, blockPos, blockState, blockEntity);
//        // Move from buffer to output
//        if (!level.isClientSide && blockEntity.tickCounter %20 == 0 && !blockEntity.buffer.isEmpty()) {
//            if (blockEntity.inventoryHandler.getStackInSlot(SLOT_OUTPUT).isEmpty()) {
//                blockEntity.inventoryHandler.insertItem(SLOT_OUTPUT, blockEntity.buffer, false);
//                blockEntity.buffer = ItemStack.EMPTY;
//            }
//        }
//    }
//
//    static boolean isValidFishingSpot(Level level, BlockPos blockPos) {
//        var positions = BlockPos.betweenClosedStream(blockPos.relative(Direction.EAST).relative(Direction.SOUTH), blockPos.relative(Direction.WEST).relative(Direction.NORTH)).toList();
//        return positions.stream().filter(p -> level.getBlockState(p).getFluidState().is(FluidTags.WATER)).count() > 5;
//    }
//}
