package cy.jdkdigital.productivefarming.common.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivefarming.common.datamap.CropTrait;
import cy.jdkdigital.productivefarming.util.TraitsHelper;
import net.minecraft.resources.Identifier;

import java.util.Optional;

public record CropTraitState(int growth, int yield, int resistance, int mutability, Optional<Identifier> mutation)
{
    public static final Codec<CropTraitState> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                    Codec.INT.fieldOf(TraitsHelper.GROWTH).forGetter(CropTraitState::growth),
                    Codec.INT.fieldOf(TraitsHelper.YIELD).forGetter(CropTraitState::yield),
                    Codec.INT.fieldOf(TraitsHelper.RESISTANCE).forGetter(CropTraitState::resistance),
                    Codec.INT.fieldOf(TraitsHelper.MUTABILITY).forGetter(CropTraitState::mutability),
                    Identifier.CODEC.optionalFieldOf("mutation").forGetter(CropTraitState::mutation)
            )
            .apply(builder, CropTraitState::new));

    public static CropTraitState fromTrait(CropTrait trait) {
        return new CropTraitState(trait.growth(), trait.yield(), trait.resistance(), trait.mutability(), Optional.empty());
    }

    public boolean hasMutation() {
        return mutation.isPresent();
    }

    public boolean isTrivial() {
        return growth == 0 && yield == 0 && resistance == 0 && mutability == 0 && mutation.isEmpty();
    }

    public CropTraitState withMutation(Identifier mutation) {
        return new CropTraitState(growth, yield, resistance, mutability, Optional.ofNullable(mutation));
    }

    public CropTraitState increase(String trait) {
        return switch (trait) {
            case TraitsHelper.GROWTH -> new CropTraitState(clamp(TraitsHelper.GROWTH, growth + 1), yield, resistance, mutability, mutation);
            case TraitsHelper.YIELD -> new CropTraitState(growth, clamp(TraitsHelper.YIELD, yield + 1), resistance, mutability, mutation);
            case TraitsHelper.RESISTANCE -> new CropTraitState(growth, yield, clamp(TraitsHelper.RESISTANCE, resistance + 1), mutability, mutation);
            case TraitsHelper.MUTABILITY -> new CropTraitState(growth, yield, resistance, clamp(TraitsHelper.MUTABILITY, mutability + 1), mutation);
            default -> this;
        };
    }

    private static int clamp(String trait, int value) {
        return Math.min(value, TraitsHelper.getMaxValue(trait));
    }
}
