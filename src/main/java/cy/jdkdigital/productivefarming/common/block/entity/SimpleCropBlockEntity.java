package cy.jdkdigital.productivefarming.common.block.entity;

import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class SimpleCropBlockEntity extends CropBlockEntity
{
    public SimpleCropBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(FarmingRegistrator.CROP_BLOCK_ENTITY.get(), blockPos, blockState);
    }
}
