package cy.jdkdigital.productivefarming.common.datamap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivefarming.util.TraitsHelper;

public record CropTrait(int growth, int yield, int resistance, int mutability)
{
    public static final Codec<CropTrait> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                    Codec.INT.fieldOf(TraitsHelper.GROWTH).forGetter(CropTrait::growth),
                    Codec.INT.fieldOf(TraitsHelper.YIELD).forGetter(CropTrait::yield),
                    Codec.INT.fieldOf(TraitsHelper.RESISTANCE).forGetter(CropTrait::resistance),
                    Codec.INT.fieldOf(TraitsHelper.MUTABILITY).forGetter(CropTrait::mutability)
            )
            .apply(builder, CropTrait::new));
}
