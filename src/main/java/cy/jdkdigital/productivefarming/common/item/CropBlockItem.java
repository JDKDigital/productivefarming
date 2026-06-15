package cy.jdkdigital.productivefarming.common.item;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.common.block.DoubleCropBlock;
import cy.jdkdigital.productivefarming.registry.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;

import java.util.function.Consumer;

public class CropBlockItem extends BlockItem
{
    public CropBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (stack.is(ModTags.Items.FENCEPOST_CROP)) {
            tooltipComponents.accept(Component.translatable(ProductiveFarming.MODID + ".tooltip.fencepost_crop").withStyle(ChatFormatting.AQUA));
        }
        if (stack.is(Tags.Items.MUSHROOMS)) {
            tooltipComponents.accept(Component.translatable(ProductiveFarming.MODID + ".tooltip.mushroom_crop").withStyle(ChatFormatting.AQUA));
        }
        if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof DoubleCropBlock) {
            tooltipComponents.accept(Component.translatable(ProductiveFarming.MODID + ".tooltip.double_crop").withStyle(ChatFormatting.AQUA));
        }
        super.appendHoverText(stack, context, display, tooltipComponents, tooltipFlag);
    }
}
