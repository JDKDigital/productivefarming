package cy.jdkdigital.productivefarming.client.color;

import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Set;

public final class StemAgeTintSource implements BlockTintSource
{
    public static final StemAgeTintSource INSTANCE = new StemAgeTintSource();

    private StemAgeTintSource() {}

    @Override
    public int color(BlockState state) {
        int i = state.getValue(StemBlock.AGE);
        return ARGB.color(i * 32, 255 - i * 8, i * 4);
    }

    @Override
    public Set<Property<?>> relevantProperties() {
        return Set.of(StemBlock.AGE);
    }
}
