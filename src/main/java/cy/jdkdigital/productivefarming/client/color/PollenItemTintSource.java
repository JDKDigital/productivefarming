package cy.jdkdigital.productivefarming.client.color;

import com.mojang.serialization.MapCodec;
import cy.jdkdigital.productivefarming.common.item.PollenItem;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public record PollenItemTintSource() implements ItemTintSource
{
    public static final PollenItemTintSource INSTANCE = new PollenItemTintSource();
    public static final MapCodec<PollenItemTintSource> MAP_CODEC = MapCodec.unit(INSTANCE);

    @Override
    public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity owner) {
        return PollenItem.getColor(stack);
    }

    @Override
    public MapCodec<? extends ItemTintSource> type() {
        return MAP_CODEC;
    }
}
