package cy.jdkdigital.productivefarming.common.block.entity;

import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class VineLeafBlockEntity extends FencedCropBlockEntity
{
    public VineLeafBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(FarmingRegistrator.VINE_LEAVES_BLOCK_ENTITY.get(), blockPos, blockState);
    }
}
