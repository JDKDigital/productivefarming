package cy.jdkdigital.productivefarming.inventory;

import cy.jdkdigital.productivefarming.common.block.FeedingTroughBlock;
import cy.jdkdigital.productivefarming.common.block.entity.FeedingTroughBlockEntity;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import cy.jdkdigital.productivelib.container.AbstractContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;
import java.util.Objects;

public class FeedingTroughContainer extends AbstractContainer
{
    public final FeedingTroughBlockEntity blockEntity;

    private final ContainerLevelAccess canInteractWithCallable;

    public FeedingTroughContainer(final int windowId, final Inventory playerInventory, final FriendlyByteBuf data) {
        this(windowId, playerInventory, getTileEntity(playerInventory, data));
    }

    public FeedingTroughContainer(final int windowId, final Inventory playerInventory, final FeedingTroughBlockEntity blockEntity) {
        super(FarmingRegistrator.FEEDING_TROUGH_MENU.get(), windowId);

        this.blockEntity = blockEntity;
        this.canInteractWithCallable = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());

        addSlotBox(this.blockEntity.getItemHandler(), 0, 62, 19, 3, 18, 3, 18);

        layoutPlayerInventorySlots(playerInventory, 0, 8, 84);
    }

    private static FeedingTroughBlockEntity getTileEntity(final Inventory playerInventory, final FriendlyByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
        Objects.requireNonNull(data, "data cannot be null!");
        final BlockEntity tileAtPos = playerInventory.player.level().getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof FeedingTroughBlockEntity) {
            return (FeedingTroughBlockEntity) tileAtPos;
        }
        throw new IllegalStateException("Block entity is not correct! " + tileAtPos);
    }

    //TODO move to lib
    @Override
    public boolean stillValid(@Nonnull final Player player) {
        return canInteractWithCallable.evaluate((world, pos) -> world.getBlockState(pos).getBlock() instanceof FeedingTroughBlock && player.distanceToSqr((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D, true);
    }

    @Override
    protected BlockEntity getBlockEntity() {
        return blockEntity;
    }
}
