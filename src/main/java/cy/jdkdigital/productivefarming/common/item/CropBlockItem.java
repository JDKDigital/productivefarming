package cy.jdkdigital.productivefarming.common.item;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.registry.FarmingDataComponents;
import cy.jdkdigital.productivefarming.util.FarmUtil;
import cy.jdkdigital.productivefarming.util.TraitsHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class CropBlockItem extends ItemNameBlockItem
{
    public CropBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable(FarmUtil.getLatinTranslationKey(this.getBlock().asItem())).withStyle(ChatFormatting.DARK_GREEN).withStyle(ChatFormatting.ITALIC));
        if (tooltipFlag.hasShiftDown()) {
            tooltipComponents.add(Component.translatable(ProductiveFarming.MODID + ".trait." + TraitsHelper.GROWTH, TraitsHelper.getValueName(TraitsHelper.GROWTH, stack.getOrDefault(FarmingDataComponents.GROWTH, 0))).withStyle(ChatFormatting.GRAY));
            tooltipComponents.add(Component.translatable(ProductiveFarming.MODID + ".trait." + TraitsHelper.YIELD, TraitsHelper.getValueName(TraitsHelper.YIELD, stack.getOrDefault(FarmingDataComponents.YIELD, 0))).withStyle(ChatFormatting.GRAY));
            tooltipComponents.add(Component.translatable(ProductiveFarming.MODID + ".trait." + TraitsHelper.RESISTANCE, TraitsHelper.getValueName(TraitsHelper.RESISTANCE, stack.getOrDefault(FarmingDataComponents.RESISTANCE, 0))).withStyle(ChatFormatting.GRAY));
            tooltipComponents.add(Component.translatable(ProductiveFarming.MODID + ".trait." + TraitsHelper.MUTABILITY, TraitsHelper.getValueName(TraitsHelper.MUTABILITY, stack.getOrDefault(FarmingDataComponents.MUTABILITY, 0))).withStyle(ChatFormatting.GRAY));
        } else {
            tooltipComponents.add(Component.translatable(ProductiveFarming.MODID + ".tooltip.extend").withStyle(ChatFormatting.DARK_GRAY));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
