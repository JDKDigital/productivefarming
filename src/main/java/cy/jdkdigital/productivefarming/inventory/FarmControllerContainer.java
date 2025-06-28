package cy.jdkdigital.productivefarming.inventory;

import cy.jdkdigital.productivefarming.common.block.FarmControllerBlock;
import cy.jdkdigital.productivefarming.common.block.entity.FarmControllerBlockEntity;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import cy.jdkdigital.productivelib.container.AbstractContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;
import java.util.Objects;

public class FarmControllerContainer extends AbstractContainer
{
    public final FarmControllerBlockEntity blockEntity;

    private final ContainerLevelAccess canInteractWithCallable;

    public FarmControllerContainer(final int windowId, final Inventory playerInventory, final FriendlyByteBuf data) {
        this(windowId, playerInventory, getTileEntity(playerInventory, data));
    }

    public FarmControllerContainer(final int windowId, final Inventory playerInventory, final FarmControllerBlockEntity blockEntity) {
        super(FarmingRegistrator.FARM_CONTROLLER_MENU.get(), windowId);

        this.blockEntity = blockEntity;
        this.canInteractWithCallable = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());

        addSlotBox(this.blockEntity.inventoryHandler, 0, 62, 19, 3, 18, 3, 18);

        layoutPlayerInventorySlots(playerInventory, 0, 8, 84);
    }

    private static FarmControllerBlockEntity getTileEntity(final Inventory playerInventory, final FriendlyByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
        Objects.requireNonNull(data, "data cannot be null!");
        final BlockEntity tileAtPos = playerInventory.player.level().getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof FarmControllerBlockEntity) {
            return (FarmControllerBlockEntity) tileAtPos;
        }
        throw new IllegalStateException("Block entity is not correct! " + tileAtPos);
    }

    @Override
    public boolean stillValid(@Nonnull final Player player) {
        return canInteractWithCallable.evaluate((world, pos) -> world.getBlockState(pos).getBlock() instanceof FarmControllerBlock && player.distanceToSqr((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D, true);
    }

    @Override
    protected BlockEntity getBlockEntity() {
        return blockEntity;
    }
}
