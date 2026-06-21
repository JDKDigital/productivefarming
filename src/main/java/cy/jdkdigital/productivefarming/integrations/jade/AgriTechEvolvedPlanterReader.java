package cy.jdkdigital.productivefarming.integrations.jade;

import com.misterd.agritechevolved.blockentity.custom.AdvancedPlanterBlockEntity;
import com.misterd.agritechevolved.blockentity.custom.PlanterBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

final class AgriTechEvolvedPlanterReader
{
    private AgriTechEvolvedPlanterReader() {}

    static ItemStack readSeed(BlockEntity blockEntity) {
        if (blockEntity instanceof PlanterBlockEntity planter) {
            return planter.getStack(0);
        }
        if (blockEntity instanceof AdvancedPlanterBlockEntity planter) {
            return planter.getStack(0);
        }
        return ItemStack.EMPTY;
    }
}
