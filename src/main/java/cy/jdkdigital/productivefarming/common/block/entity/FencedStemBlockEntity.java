package cy.jdkdigital.productivefarming.common.block.entity;

import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class FencedStemBlockEntity extends FencedCropBlockEntity
{
    public FencedStemBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(FarmingRegistrator.FENCED_STEM_BLOCK_ENTITY.get(), blockPos, blockState);
    }
}
