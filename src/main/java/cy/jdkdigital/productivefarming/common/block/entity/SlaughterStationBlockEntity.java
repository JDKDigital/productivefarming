//package cy.jdkdigital.productivefarming.common.block.entity;
//
//import cy.jdkdigital.productivefarming.Config;
//import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
//import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
//import cy.jdkdigital.productivelib.common.block.entity.UpgradeableBlockEntity;
//import cy.jdkdigital.productivelib.registry.LibItems;
//import net.minecraft.core.BlockPos;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.world.entity.LivingEntity;
//import net.minecraft.world.entity.animal.Animal;
//import net.minecraft.world.level.block.state.BlockState;
//import net.minecraft.world.level.block.state.properties.BlockStateProperties;
//import net.minecraft.world.phys.AABB;
//import net.neoforged.neoforge.items.IItemHandlerModifiable;
//
//import java.util.List;
//
//public class SlaughterStationBlockEntity extends TickingBlockEntity implements UpgradeableBlockEntity
//{
//    final public static int SLOT_OUTPUT = 0;
//
//    protected IItemHandlerModifiable upgradeHandler = new InventoryHandlerHelper.UpgradeHandler(4, this, List.of(
//            LibItems.UPGRADE_RANGE.get(),
//            LibItems.UPGRADE_CHILD.get(),
//            LibItems.UPGRADE_TIME.get()
//    ));
//
//    public SlaughterStationBlockEntity(BlockPos pPos, BlockState pBlockState) {
//        super(FarmingRegistrator.SLAUGHTER_STATION_BLOCK_ENTITY.get(), pPos, pBlockState);
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
//        if (blockEntity instanceof SlaughterStationBlockEntity) {
//            var range = 4 + (getUpgradeCount(LibItems.UPGRADE_RANGE.get()));
//            var hasChildUpgrade = getUpgradeCount(LibItems.UPGRADE_CHILD.get()) > 0;
//            var direction = blockState.getValue(BlockStateProperties.FACING);
//            List<Animal> entities = level.getEntitiesOfClass(Animal.class, (new AABB(blockPos.relative(direction, range + 1)).inflate(range, range, range))).stream().filter(animal -> hasChildUpgrade == animal.isBaby()).toList();
//            if (!entities.isEmpty()) {
//                entities.forEach(LivingEntity::kill);
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
