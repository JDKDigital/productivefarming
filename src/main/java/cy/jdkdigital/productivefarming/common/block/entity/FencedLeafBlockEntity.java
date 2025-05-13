package cy.jdkdigital.productivefarming.common.block.entity;

import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class FencedLeafBlockEntity extends FencedCropBlockEntity
{
    public FencedLeafBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(FarmingRegistrator.FENCED_LEAVES_BLOCK_ENTITY.get(), blockPos, blockState);
    }
}
