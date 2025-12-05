package cy.jdkdigital.productivefarming.integrations.jade;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.common.block.entity.CropBlockEntity;
import cy.jdkdigital.productivefarming.util.TraitsHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.StreamServerDataProvider;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElementHelper;

public class CropProvider implements IBlockComponentProvider, StreamServerDataProvider<BlockAccessor, CropProvider.Data>
{
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "crop");
    static final CropProvider INSTANCE = new CropProvider();

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        Data data = decodeFromData(accessor).orElse(null);
        if (data == null) {
            return;
        }
        IElementHelper helper = IElementHelper.get();
        if (data.hasMutation()) {
            tooltip.add(Component.translatable("jade." + ProductiveFarming.MODID + ".mutation", Component.translatable("block." + ProductiveFarming.MODID + "." + data.mutation().getPath()).withStyle(ChatFormatting.GREEN)));
            tooltip.add(Component.translatable("jade." + ProductiveFarming.MODID + ".mutation_harvest"));
        }
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().player.isShiftKeyDown()) {
            tooltip.add(Component.translatable(ProductiveFarming.MODID + ".trait." + TraitsHelper.GROWTH, TraitsHelper.getValueName(TraitsHelper.GROWTH, data.growth)));
            tooltip.add(Component.translatable(ProductiveFarming.MODID + ".trait." + TraitsHelper.YIELD, TraitsHelper.getValueName(TraitsHelper.YIELD, data.yield)));
            tooltip.add(Component.translatable(ProductiveFarming.MODID + ".trait." + TraitsHelper.RESISTANCE, TraitsHelper.getValueName(TraitsHelper.RESISTANCE, data.resistance)));
            tooltip.add(Component.translatable(ProductiveFarming.MODID + ".trait." + TraitsHelper.MUTABILITY, TraitsHelper.getValueName(TraitsHelper.MUTABILITY, data.mutability)));
        } else {
            tooltip.add(Component.translatable(ProductiveFarming.MODID + ".tooltip.extend").withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    @Override
    public Data streamData(BlockAccessor accessor) {
        CropBlockEntity access = (CropBlockEntity) accessor.getBlockEntity();
        return new Data(access.getGrowth(), access.getYield(), access.getResistance(), access.getMutability(), access.hasMutation(), access.hasMutation() ? access.getMutation() : ResourceLocation.parse(""));
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Data> streamCodec() {
        return Data.STREAM_CODEC;
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    public record Data(int growth, int yield, int resistance, int mutability, boolean hasMutation, ResourceLocation mutation) {
        public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT,
                Data::growth,
                ByteBufCodecs.INT,
                Data::yield,
                ByteBufCodecs.INT,
                Data::resistance,
                ByteBufCodecs.INT,
                Data::mutability,
                ByteBufCodecs.BOOL,
                Data::hasMutation,
                ResourceLocation.STREAM_CODEC,
                Data::mutation,
                Data::new);
    }
}