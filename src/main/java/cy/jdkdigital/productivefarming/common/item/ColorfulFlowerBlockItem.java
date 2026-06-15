package cy.jdkdigital.productivefarming.common.item;

import cy.jdkdigital.productivefarming.common.block.IColorfulFlowerBlock;
import cy.jdkdigital.productivefarming.registry.FarmingDataComponents;
import cy.jdkdigital.productivefarming.util.FarmUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class ColorfulFlowerBlockItem extends BlockItem
{
    public ColorfulFlowerBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        appendDyeTooltip(this, stack, tooltipComponents);
        super.appendHoverText(stack, context, display, tooltipComponents, tooltipFlag);
    }

    public static void appendDyeTooltip(BlockItem item, ItemStack stack, Consumer<Component> tooltipComponents) {
        int defaultColor = item.getBlock() instanceof IColorfulFlowerBlock flower ? flower.getDefaultColor() : -1;
        int color = stack.getOrDefault(FarmingDataComponents.COLOR, defaultColor);
        ItemStack dye = FarmUtil.getDyeFromColor(color);
        if (!dye.isEmpty()) {
            tooltipComponents.accept(Component.translatable(dye.getItem().getDescriptionId()).withColor(color).withStyle(ChatFormatting.ITALIC));
        }
    }
}
