package cy.jdkdigital.productivefarming.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class ColorfulDoubleFlowerBlockItem extends DoubleHighBlockItem
{
    public ColorfulDoubleFlowerBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        ColorfulFlowerBlockItem.appendDyeTooltip(this, stack, tooltipComponents);
        super.appendHoverText(stack, context, display, tooltipComponents, tooltipFlag);
    }
}
