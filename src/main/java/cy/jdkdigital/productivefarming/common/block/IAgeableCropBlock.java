package cy.jdkdigital.productivefarming.common.block;

import net.minecraft.world.level.block.state.properties.IntegerProperty;

public interface IAgeableCropBlock
{
    IntegerProperty getAgeProperty();

    int getHarvestedAge();
}
