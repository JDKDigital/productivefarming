package cy.jdkdigital.productivefarming.client.color;

import com.mojang.serialization.MapCodec;
import cy.jdkdigital.productivefarming.registry.FarmingDataComponents;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public record FlowerItemTintSource() implements ItemTintSource
{
    public static final FlowerItemTintSource INSTANCE = new FlowerItemTintSource();
    public static final MapCodec<FlowerItemTintSource> MAP_CODEC = MapCodec.unit(INSTANCE);

    @Override
    public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity owner) {
        return stack.getOrDefault(FarmingDataComponents.COLOR, -1);
    }

    @Override
    public MapCodec<? extends ItemTintSource> type() {
        return MAP_CODEC;
    }
}
