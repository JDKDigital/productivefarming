package cy.jdkdigital.productivefarming.integrations.jade;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.common.attachment.CropTraitState;
import cy.jdkdigital.productivefarming.util.ExternalCropStats;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.StreamServerDataProvider;

public class ExternalCropServerProvider implements StreamServerDataProvider<BlockAccessor, CropServerProvider.Data>
{
    public static final Identifier UID = Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "external_crop");
    public static final ExternalCropServerProvider INSTANCE = new ExternalCropServerProvider();

    @Override
    public CropServerProvider.Data streamData(BlockAccessor accessor) {
        BlockState state = accessor.getBlockState();
        if (!ExternalCropStats.isEnabled() || !ExternalCropStats.isEligible(state)) {
            return new CropServerProvider.Data(-1, 0, 0, 0, false, Identifier.parse(""));
        }
        CropTraitState trait = ExternalCropStats.getTrait(accessor.getLevel(), accessor.getPosition(), state);
        return new CropServerProvider.Data(trait.growth(), trait.yield(), trait.resistance(), trait.mutability(), trait.hasMutation(), trait.mutation().orElse(Identifier.parse("")));
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, CropServerProvider.Data> streamCodec() {
        return CropServerProvider.Data.STREAM_CODEC;
    }

    @Override
    public Identifier getUid() {
        return UID;
    }
}
