package cy.jdkdigital.productivefarming.common.item;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.common.block.ProductiveCropBlock;
import cy.jdkdigital.productivefarming.registry.FarmingDataComponents;
import cy.jdkdigital.productivefarming.util.FarmUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class PollenItem extends Item
{
    public PollenItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
        if (pStack.has(FarmingDataComponents.POLLEN_BLOCK_COMPONENT)) {
            Block leaf = BuiltInRegistries.BLOCK.get(pStack.get(FarmingDataComponents.POLLEN_BLOCK_COMPONENT));
            pTooltipComponents.add(Component.translatable(ProductiveFarming.MODID + ".pollen.name", Component.translatable(leaf.getDescriptionId()).withStyle(ChatFormatting.GREEN)).withStyle(ChatFormatting.DARK_BLUE));
        }
        pTooltipComponents.add(Component.translatable(ProductiveFarming.MODID + ".information.pollen").withStyle(ChatFormatting.GOLD));
    }

    public static int getColor(ItemStack stack) {
        if (stack.has(FarmingDataComponents.POLLEN_BLOCK_COMPONENT)) {
            if (BuiltInRegistries.BLOCK.get(stack.get(FarmingDataComponents.POLLEN_BLOCK_COMPONENT)) instanceof ProductiveCropBlock cropBlock) {
                return cropBlock.getCropConfig().getCropColor();
            }
            if (FarmUtil.VANILLA_FLOWER_COLORS.containsKey(stack.get(FarmingDataComponents.POLLEN_BLOCK_COMPONENT))) {
                return FarmUtil.VANILLA_FLOWER_COLORS.get(stack.get(FarmingDataComponents.POLLEN_BLOCK_COMPONENT));
            }
        }
        return FoliageColor.getDefaultColor();
    }
}
