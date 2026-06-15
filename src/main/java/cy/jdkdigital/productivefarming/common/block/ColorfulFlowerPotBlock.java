package cy.jdkdigital.productivefarming.common.block;

import cy.jdkdigital.productivefarming.common.block.entity.ColorfulFlowerPotBlockEntity;
import cy.jdkdigital.productivefarming.registry.FarmingDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class ColorfulFlowerPotBlock extends FlowerPotBlock implements EntityBlock
{
    private final Supplier<? extends Block> flower;

    public ColorfulFlowerPotBlock(@Nullable Supplier<FlowerPotBlock> emptyPot, Supplier<? extends Block> flower, Properties properties) {
        super(emptyPot, flower, properties);
        this.flower = flower;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ColorfulFlowerPotBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (this.getPotted() == Blocks.AIR) {
            return InteractionResult.CONSUME;
        } else {
            ItemStack itemstack = new ItemStack(this.getPotted());
            if (level.getBlockEntity(pos) instanceof ColorfulFlowerPotBlockEntity blockEntity) {
                itemstack.set(FarmingDataComponents.COLOR, blockEntity.getColor());
            }
            if (!player.addItem(itemstack)) {
                player.drop(itemstack, false);
            }

            level.setBlock(pos, this.getEmptyPot().defaultBlockState(), 3);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            return InteractionResult.SUCCESS;
        }
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData, Player player) {
        var stack = new ItemStack(flower.get());
        if (level.getBlockEntity(pos) instanceof ColorfulFlowerPotBlockEntity flowerBlockEntity) {
            stack.set(FarmingDataComponents.COLOR, flowerBlockEntity.getColor());
        }
        return stack;
    }
}
