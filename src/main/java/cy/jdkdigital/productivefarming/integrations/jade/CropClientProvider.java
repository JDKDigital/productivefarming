package cy.jdkdigital.productivefarming.integrations.jade;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.util.TraitsHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class CropClientProvider implements IBlockComponentProvider
{
    public static final CropClientProvider INSTANCE = new CropClientProvider();

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CropServerProvider.Data data = CropServerProvider.INSTANCE.decodeFromData(accessor).orElse(null);
        if (data == null) {
            return;
        }
        if (data.hasMutation()) {
            tooltip.add(Component.translatable("jade." + ProductiveFarming.MODID + ".mutation", Component.translatable("block." + ProductiveFarming.MODID + "." + data.mutation().getPath()).withStyle(ChatFormatting.GREEN)));
            tooltip.add(Component.translatable("jade." + ProductiveFarming.MODID + ".mutation_harvest"));
        }
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().player.isShiftKeyDown()) {
            tooltip.add(Component.translatable(ProductiveFarming.MODID + ".trait." + TraitsHelper.GROWTH, TraitsHelper.getValueName(TraitsHelper.GROWTH, data.growth())));
            tooltip.add(Component.translatable(ProductiveFarming.MODID + ".trait." + TraitsHelper.YIELD, TraitsHelper.getValueName(TraitsHelper.YIELD, data.yield())));
            tooltip.add(Component.translatable(ProductiveFarming.MODID + ".trait." + TraitsHelper.RESISTANCE, TraitsHelper.getValueName(TraitsHelper.RESISTANCE, data.resistance())));
            tooltip.add(Component.translatable(ProductiveFarming.MODID + ".trait." + TraitsHelper.MUTABILITY, TraitsHelper.getValueName(TraitsHelper.MUTABILITY, data.mutability())));
        } else {
            tooltip.add(Component.translatable(ProductiveFarming.MODID + ".tooltip.extend").withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    @Override
    public Identifier getUid() {
        return CropServerProvider.UID;
    }
}
