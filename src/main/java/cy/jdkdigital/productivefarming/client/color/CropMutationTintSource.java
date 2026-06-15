package cy.jdkdigital.productivefarming.client.color;

import cy.jdkdigital.productivefarming.common.block.entity.CropBlockEntity;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class CropMutationTintSource implements BlockTintSource
{
    public static final CropMutationTintSource INSTANCE = new CropMutationTintSource();

    private CropMutationTintSource() {}

    @Override
    public int color(BlockState state) {
        return -1;
    }

    @Override
    public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        return be instanceof CropBlockEntity crop ? crop.getMutationColor() : -1;
    }
}
