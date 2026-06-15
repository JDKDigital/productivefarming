package cy.jdkdigital.productivefarming.registry;

import com.mojang.serialization.Codec;
import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.util.TraitsHelper;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;

import java.util.function.Supplier;

public class FarmingDataComponents
{
    // pipe stuff
    public static final Supplier<DataComponentType<Boolean>> IS_LIT = ProductiveFarming.DATA_COMPONENTS.register("is_lit", () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());
    public static final Supplier<DataComponentType<Integer>> CHARGES = ProductiveFarming.DATA_COMPONENTS.register("charges", () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());

    // crop traits
    public static final Supplier<DataComponentType<Integer>> GROWTH = ProductiveFarming.DATA_COMPONENTS.register(TraitsHelper.GROWTH, () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());
    public static final Supplier<DataComponentType<Integer>> YIELD = ProductiveFarming.DATA_COMPONENTS.register(TraitsHelper.YIELD, () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());
    public static final Supplier<DataComponentType<Integer>> RESISTANCE = ProductiveFarming.DATA_COMPONENTS.register(TraitsHelper.RESISTANCE, () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());
    public static final Supplier<DataComponentType<Integer>> MUTABILITY = ProductiveFarming.DATA_COMPONENTS.register(TraitsHelper.MUTABILITY, () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());

    // pollen
    public static final Supplier<DataComponentType<Identifier>> POLLEN_BLOCK_COMPONENT = ProductiveFarming.DATA_COMPONENTS.register("pollen_block", () -> DataComponentType.<Identifier>builder().persistent(Identifier.CODEC).networkSynchronized(Identifier.STREAM_CODEC).build());

    // flowers
    public static final Supplier<DataComponentType<Integer>> COLOR = ProductiveFarming.DATA_COMPONENTS.register("color", () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());

    public static void init() {
    }
}
