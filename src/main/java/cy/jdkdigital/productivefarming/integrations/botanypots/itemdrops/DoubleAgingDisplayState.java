package cy.jdkdigital.productivefarming.integrations.botanypots.itemdrops;

import net.darkhax.botanypots.common.impl.data.display.types.AgingDisplayState;
import net.darkhax.botanypots.common.impl.data.display.types.BasicOptions;
import net.minecraft.world.level.block.Block;

public class DoubleAgingDisplayState extends AgingDisplayState
{
    // BlockStateProperties.DOUBLE_BLOCK_HALF
    public DoubleAgingDisplayState(Block block, BasicOptions renderOptions) {
        super(block, renderOptions);
    }
}
