package cy.jdkdigital.productivefarming.common.block.entity;

import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TrellisBlockEntity extends FencedCropBlockEntity
{
    public TrellisBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(FarmingRegistrator.TRELLIS_LEAVES_BLOCK_ENTITY.get(), blockPos, blockState);
    }
}
