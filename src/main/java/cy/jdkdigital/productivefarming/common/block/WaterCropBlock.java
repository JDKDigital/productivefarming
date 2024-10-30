package cy.jdkdigital.productivefarming.common.block;

import cy.jdkdigital.productivefarming.util.CropConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;

public class WaterCropBlock extends ProductiveCropBlock implements SimpleWaterloggedBlock
{
    public WaterCropBlock(CropConfig crop, Properties pProperties) {
        super(crop, pProperties);
    }

    // TODO waterlogged?

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return super.canSurvive(state, level, pos) && level.getFluidState(pos.above()).is(FluidTags.WATER);
    }
}
