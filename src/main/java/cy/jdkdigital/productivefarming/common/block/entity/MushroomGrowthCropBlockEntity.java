package cy.jdkdigital.productivefarming.common.block.entity;

import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class MushroomGrowthCropBlockEntity extends CropBlockEntity
{
    public MushroomGrowthCropBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(FarmingRegistrator.MUSHROOM_GROWTH_BLOCK_ENTITY.get(), blockPos, blockState);
    }
}
