//package cy.jdkdigital.productivefarming.common.block.entity;
//
//import cy.jdkdigital.productivefarming.Config;
//import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
//import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
//import cy.jdkdigital.productivelib.common.block.entity.UpgradeableBlockEntity;
//import cy.jdkdigital.productivelib.registry.LibItems;
//import net.minecraft.core.BlockPos;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.world.entity.animal.Animal;
//import net.minecraft.world.level.block.state.BlockState;
//import net.minecraft.world.level.block.state.properties.BlockStateProperties;
//import net.minecraft.world.phys.AABB;
//import net.neoforged.neoforge.items.IItemHandlerModifiable;
//
//import java.util.List;
//
//public class ChildSeparatorBlockEntity extends TickingBlockEntity implements UpgradeableBlockEntity
//{
//    final public static int SLOT_OUTPUT = 0;
//
//    protected IItemHandlerModifiable upgradeHandler = new InventoryHandlerHelper.UpgradeHandler(4, this, List.of(
//            LibItems.UPGRADE_RANGE.get(),
//            LibItems.UPGRADE_ADULT.get(),
//            LibItems.UPGRADE_TIME.get()
//    ));
//
//    public ChildSeparatorBlockEntity(BlockPos pPos, BlockState pBlockState) {
//        super(FarmingRegistrator.CHILD_SEPARATOR_BLOCK_ENTITY.get(), pPos, pBlockState);
//    }
//
//    @Override
//    int tickRate() {
//        int speed = Config.SERVER.childSeparatorTickRate.get();
//        int speedUpgrades = getUpgradeCount(LibItems.UPGRADE_TIME.get());
//        return speed - (speed/30*speedUpgrades);
//    }
//
//    @Override
//    public void tickServer(ServerLevel level, BlockPos blockPos, BlockState blockState, TickingBlockEntity blockEntity) {
//        if (blockEntity instanceof ChildSeparatorBlockEntity childSeparatorBlockEntity) {
//            var range = 2 + (getUpgradeCount(LibItems.UPGRADE_RANGE.get()));
//            var hasAdultUpgrade = getUpgradeCount(LibItems.UPGRADE_ADULT.get()) > 0;
//            var direction = blockState.getValue(BlockStateProperties.FACING);
//            List<Animal> entities = level.getEntitiesOfClass(Animal.class, (new AABB(blockPos.relative(direction.getOpposite(), range + 1)).inflate(range, range, range))).stream().filter(animal -> hasAdultUpgrade != animal.isBaby()).limit(12).toList();
//            if (!entities.isEmpty()) {
//                var frontPos = blockPos.relative(direction).getCenter();
//                entities.forEach(animal -> {
//                    animal.moveTo(frontPos);
//                });
//            }
//        }
//    }
//
//    @Override
//    public IItemHandlerModifiable getUpgradeHandler() {
//        return upgradeHandler;
//    }
//
////    @Nullable
////    @Override
////    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
////        return new FeedingTroughContainer(containerId, playerInventory, this); // TODO
////    }
//}
