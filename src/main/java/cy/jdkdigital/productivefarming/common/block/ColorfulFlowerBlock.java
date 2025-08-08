package cy.jdkdigital.productivefarming.common.block;

import cy.jdkdigital.productivefarming.common.block.entity.ColorfulFlowerBlockEntity;
import cy.jdkdigital.productivefarming.registry.FarmingDataComponents;
import cy.jdkdigital.productivefarming.util.FarmUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ColorfulFlowerBlock extends FlowerBlock implements EntityBlock, IColorfulFlowerBlock
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

    public int getDefaultColor() {
        return defaultColor;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        var stack = new ItemStack(this);
        if (level.getBlockEntity(pos) instanceof ColorfulFlowerBlockEntity flowerBlockEntity) {
            stack.set(FarmingDataComponents.COLOR, flowerBlockEntity.getColor());
        }
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable(FarmUtil.getDyeFromColor(stack.getOrDefault(FarmingDataComponents.COLOR, this.defaultColor)).getDescriptionId()).withColor(stack.getOrDefault(FarmingDataComponents.COLOR, this.defaultColor)).withStyle(ChatFormatting.ITALIC));
    }
}
