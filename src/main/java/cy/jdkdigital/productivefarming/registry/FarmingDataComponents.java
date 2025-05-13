package cy.jdkdigital.productivefarming.registry;

import com.mojang.serialization.Codec;
import cy.jdkdigital.productivefarming.ProductiveFarming;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;

import java.util.function.Supplier;

public class FarmingDataComponents
{
    public static final Supplier<DataComponentType<Boolean>> IS_LIT = ProductiveFarming.DATA_COMPONENTS.register("is_lit", () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());
    public static final Supplier<DataComponentType<Integer>> CHARGES = ProductiveFarming.DATA_COMPONENTS.register("charges", () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());

    public static void init() {
    }
}
