package cy.jdkdigital.productivefarming.client.color;

import cy.jdkdigital.productivefarming.common.block.entity.ColorfulFlowerBlockEntity;
import cy.jdkdigital.productivefarming.common.block.entity.ColorfulFlowerPotBlockEntity;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class FlowerColorTintSource implements BlockTintSource
{
    public static final FlowerColorTintSource INSTANCE = new FlowerColorTintSource();

    private FlowerColorTintSource() {}

    @Override
    public int color(BlockState state) {
        return -1;
    }

    @Override
    public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof ColorfulFlowerBlockEntity flower) {
            return flower.getColor();
        }
        if (be instanceof ColorfulFlowerPotBlockEntity pot) {
            return pot.getColor();
        }
        return -1;
    }
}
