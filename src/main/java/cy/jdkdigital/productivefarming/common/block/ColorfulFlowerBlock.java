package cy.jdkdigital.productivefarming.common.block;

import cy.jdkdigital.productivefarming.common.block.entity.ColorfulFlowerBlockEntity;
import cy.jdkdigital.productivefarming.registry.FarmingDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.TallFlowerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

public class ColorfulFlowerBlock extends FlowerBlock implements EntityBlock
{
    private final int defaultColor;

    public ColorfulFlowerBlock(Properties properties, int defaultColor) {
        super(MobEffects.NIGHT_VISION, 5.0f, properties);
        this.defaultColor = defaultColor;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ColorfulFlowerBlockEntity(pos, state, this.defaultColor);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        var stack = new ItemStack(this);
        if (level.getBlockEntity(pos) instanceof ColorfulFlowerBlockEntity flowerBlockEntity) {
            stack.set(FarmingDataComponents.COLOR, flowerBlockEntity.getColor());
        }
        return stack;
    }
}
