package cy.jdkdigital.productivefarming.integrations.jade;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.common.block.entity.CropBlockEntity;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.StreamServerDataProvider;

public class CropServerProvider implements StreamServerDataProvider<BlockAccessor, CropServerProvider.Data>
{
    public static final Identifier UID = Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "crop");
    public static final CropServerProvider INSTANCE = new CropServerProvider();

    @Override
    public Data streamData(BlockAccessor accessor) {
        CropBlockEntity access = (CropBlockEntity) accessor.getBlockEntity();
        return new Data(access.getGrowth(), access.getYield(), access.getResistance(), access.getMutability(), access.hasMutation(), access.hasMutation() ? access.getMutation() : Identifier.parse(""));
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Data> streamCodec() {
        return Data.STREAM_CODEC;
    }

    @Override
    public Identifier getUid() {
        return UID;
    }

    public record Data(int growth, int yield, int resistance, int mutability, boolean hasMutation, Identifier mutation) {
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
                Identifier.STREAM_CODEC,
                Data::mutation,
                Data::new);
    }
}
