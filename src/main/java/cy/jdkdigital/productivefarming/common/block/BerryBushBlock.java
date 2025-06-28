package cy.jdkdigital.productivefarming.common.block;

import cy.jdkdigital.productivefarming.util.CropConfig;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class BerryBushBlock extends ProductiveCropBlock
{
    public BerryBushBlock(CropConfig crop, Properties pProperties) {
        super(crop, pProperties);
    }

    @Override
    public IntegerProperty getAgeProperty() {
        return AGE_6;
    }

    @Override
    public int getMaxAge() {
        return 6;
    }

    @Override
    public int getHarvestedAge() {
        return 2;
    }
}
