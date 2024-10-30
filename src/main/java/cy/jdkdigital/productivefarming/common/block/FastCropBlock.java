package cy.jdkdigital.productivefarming.common.block;

import cy.jdkdigital.productivefarming.util.CropConfig;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class FastCropBlock extends ProductiveCropBlock
{
    public FastCropBlock(CropConfig crop, Properties pProperties) {
        super(crop, pProperties);
    }

    @Override
    public IntegerProperty getAgeProperty() {
        return BlockStateProperties.AGE_3;
    }

    @Override
    public int getMaxAge() {
        return BlockStateProperties.MAX_AGE_3;
    }
}
