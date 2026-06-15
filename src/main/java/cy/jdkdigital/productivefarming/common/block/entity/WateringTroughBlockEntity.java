package cy.jdkdigital.productivefarming.common.block.entity;

import cy.jdkdigital.productivefarming.Config;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import cy.jdkdigital.productivelib.common.block.entity.IUpgradeableBlockEntity;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivelib.registry.LibItems;
import cy.jdkdigital.productivefarming.util.ModFluidTank;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;

import java.util.List;

public class WateringTroughBlockEntity extends TickingBlockEntity implements IUpgradeableBlockEntity
{
    protected ModFluidTank fluidHandler = new ModFluidTank(10000)
    {
        @Override
        public boolean isFluidValid(FluidStack fluidStack) {
            return fluidStack.is(Tags.Fluids.WATER);
        }
    };

    protected InventoryHandlerHelper.UpgradeHandler upgradeHandler = new InventoryHandlerHelper.UpgradeHandler(4, this, List.of(
            LibItems.UPGRADE_RANGE.get(),
            LibItems.UPGRADE_TIME.get()
    ));

    public WateringTroughBlockEntity(BlockPos pos, BlockState state) {
        super(FarmingRegistrator.WATERING_TROUGH_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public ResourceHandler<FluidResource> getFluidHandler() {
        return fluidHandler;
    }

    @Override
    public ResourceHandler<ItemResource> getUpgradeHandler() {
        return upgradeHandler;
    }

    @Override
    int tickRate() {
        int speed = Config.SERVER.wateringTroughTickRate.get();
        int speedUpgrades = getUpgradeCount(LibItems.UPGRADE_TIME.get());
        return speed - (speed/20*speedUpgrades);
    }

    @Override
    public void tickServer(ServerLevel level, BlockPos blockPos, BlockState blockState, TickingBlockEntity blockEntity) {
        if (blockEntity instanceof WateringTroughBlockEntity wateringTroughBlockEntity && wateringTroughBlockEntity.fluidHandler.getFluidAmount() > 100) {
            var range = 4d + (getUpgradeCount(LibItems.UPGRADE_RANGE.get()));
            List<Animal> entities = level.getEntitiesOfClass(Animal.class, (new AABB(blockPos).inflate(range, range - 3d, range))).stream().filter(animal -> !animal.isBaby() && animal.getAge() > 0).toList();
            if (!entities.isEmpty()) {
                entities.forEach(animal -> {
                    if (wateringTroughBlockEntity.fluidHandler.getFluidAmount() > 100) {
                        animal.setAge(Math.max(animal.getAge() - 200, 0));
                        wateringTroughBlockEntity.fluidHandler.drain(100, true);
                    }
                });
            }
        }
    }

//    @Nullable
//    @Override
//    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
//        return new WateringTroughContainer(pContainerId, pPlayerInventory, this);
//    }
}
