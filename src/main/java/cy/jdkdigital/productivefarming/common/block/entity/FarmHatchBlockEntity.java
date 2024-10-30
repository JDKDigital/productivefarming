package cy.jdkdigital.productivefarming.common.block.entity;

import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class FarmHatchBlockEntity extends AuxiliaryFarmBlockEntity
{
    public FarmHatchBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(FarmingRegistrator.FARM_HATCH_BLOCK_ENTITY.get(), pPos, pBlockState);
    }
}
