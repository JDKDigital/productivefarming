package cy.jdkdigital.productivefarming.integrations.jade;

import com.misterd.agritechtwo.blockentity.custom.PlanterBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

final class AgriTechTwoPlanterReader
{
    private AgriTechTwoPlanterReader() {}

    static ItemStack readSeed(BlockEntity blockEntity) {
        if (blockEntity instanceof PlanterBlockEntity planter) {
            return planter.getStack(0);
        }
        return ItemStack.EMPTY;
    }
}
