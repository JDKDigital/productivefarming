package cy.jdkdigital.productivefarming.world.context;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.neoforged.neoforge.common.Tags;

public class FenceCropBlockPlaceContext extends BlockPlaceContext
{
    public FenceCropBlockPlaceContext(UseOnContext context) {
        super(context);
    }

    @Override
    public boolean canPlace() {
        // Can only place when there's a fence
        return this.replaceClicked || this.getLevel().getBlockState(this.getClickedPos()).is(Tags.Blocks.FENCES);
    }
}
