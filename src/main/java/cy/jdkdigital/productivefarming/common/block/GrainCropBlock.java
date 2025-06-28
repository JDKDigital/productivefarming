package cy.jdkdigital.productivefarming.common.block;

import cy.jdkdigital.productivefarming.util.CropConfig;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class GrainCropBlock extends ProductiveCropBlock
{
    public GrainCropBlock(CropConfig crop, Properties pProperties) {
        super(crop, pProperties);
    }
}
