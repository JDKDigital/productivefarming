package cy.jdkdigital.productivefarming.common.block.entity;

import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class FencedVerticalCropBlockEntity extends FencedCropBlockEntity
{
    public FencedVerticalCropBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(FarmingRegistrator.FENCED_VERTICAL_CROP_BLOCK_ENTITY.get(), blockPos, blockState);
    }
}
