package cy.jdkdigital.productivefarming.common.block.entity;

import com.mojang.authlib.GameProfile;
import cy.jdkdigital.productivefarming.Config;
import cy.jdkdigital.productivefarming.common.block.FeedingTroughBlock;
import cy.jdkdigital.productivefarming.inventory.FeedingTroughContainer;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import cy.jdkdigital.productivelib.common.block.entity.IUpgradeableBlockEntity;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivelib.registry.LibItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FeedingTroughBlockEntity extends TickingBlockEntity implements MenuProvider, IUpgradeableBlockEntity
{
    static final UUID PLAYER_UUID = UUID.nameUUIDFromBytes("feeding_trough".getBytes(StandardCharsets.UTF_8));

    protected final InventoryHandlerHelper.BlockEntityItemStackHandler inventoryHandler = new InventoryHandlerHelper.BlockEntityItemStackHandler(9, this)
    {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack, boolean fromAutomation) {
            return true;
        }

        @Override
        public boolean isInsertableSlot(int slot) {
            return true;
        }

        @Override
        public boolean isInputSlot(int slot) {
            return false;
        }

        @Override
        public boolean isInputSlotItem(int slot, ItemStack item) {
            return true;
        }

        @Override
        public int[] getOutputSlots() {
            return new int[]{0,1,2,3,4,5,6,7,8};
        }

        @Override
        protected void onContentsChanged(int slot, ItemStack previousContents) {
            super.onContentsChanged(slot, previousContents);
            int itemCount = 0;
            for (int i = 0; i < size(); i++) {
                var stack = getStackInSlot(i);
                if (!stack.isEmpty()) {
                    itemCount += stack.getCount();
                }
            }
            int level = itemCount >= 160 ? 2 : (itemCount > 0 ? 1 : 0);
            if (this.blockEntity.getBlockState().hasProperty(FeedingTroughBlock.LEVEL) && this.blockEntity.getLevel() instanceof ServerLevel serverLevel) {
                var currentLevel = this.blockEntity.getBlockState().getValue(FeedingTroughBlock.LEVEL);
                if (currentLevel != level) {
                    serverLevel.setBlockAndUpdate(this.blockEntity.getBlockPos(), this.blockEntity.getBlockState().setValue(FeedingTroughBlock.LEVEL, level));
                }
            }
        }
    };

    protected InventoryHandlerHelper.UpgradeHandler upgradeHandler = new InventoryHandlerHelper.UpgradeHandler(4, this, List.of(
            LibItems.UPGRADE_RANGE.get(),
            LibItems.UPGRADE_CHILD.get(),
            LibItems.UPGRADE_TIME.get()
    ));

    public FeedingTroughBlockEntity(BlockPos pos, BlockState state) {
        super(FarmingRegistrator.FEEDING_TROUGH_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public ResourceHandler<ItemResource> getItemHandler() {
        return inventoryHandler;
    }

    @Override
    public ResourceHandler<ItemResource> getUpgradeHandler() {
        return upgradeHandler;
    }

    @Override
    int tickRate() {
        int speed = Config.SERVER.feedingTroughTickRate.get();
        int speedUpgrades = getUpgradeCount(LibItems.UPGRADE_TIME.get());
        return speed - (speed/20*speedUpgrades);
    }

    @Override
    public void tickServer(ServerLevel level, BlockPos blockPos, BlockState blockState, TickingBlockEntity blockEntity) {
        List<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < inventoryHandler.size(); i++) {
            var stack = inventoryHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                stacks.add(stack);
            }
        }
        if (!stacks.isEmpty()) {
            var range = 4d + (getUpgradeCount(LibItems.UPGRADE_RANGE.get()));
            var hasChildUpgrade = getUpgradeCount(LibItems.UPGRADE_CHILD.get()) > 0;
            List<Animal> entities = level.getEntitiesOfClass(Animal.class, (new AABB(blockPos).inflate(range, range - 3d, range))).stream().toList();
            if (!entities.isEmpty()) {
                Player fakePlayer = FakePlayerFactory.get(level, new GameProfile(PLAYER_UUID, "feeding_trough"));
                entities.forEach(animal -> {
                    for (ItemStack stack : stacks) {
                        if (!stack.isEmpty() && animal.isFood(stack) && (hasChildUpgrade || !animal.isBaby())) {
                            fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, stack);
                            if (animal.mobInteract(fakePlayer, InteractionHand.MAIN_HAND).consumesAction()) {
                                break;
                            }
                        }
                    }
                });
            }
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new FeedingTroughContainer(pContainerId, pPlayerInventory, this);
    }
}
