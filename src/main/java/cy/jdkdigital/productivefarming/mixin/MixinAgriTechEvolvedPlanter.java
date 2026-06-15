package cy.jdkdigital.productivefarming.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.misterd.agritechevolved.blockentity.custom.PlanterBlockEntity;
import cy.jdkdigital.productivefarming.integrations.agritech.AgriTechStatsHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = PlanterBlockEntity.class, remap = false)
public abstract class MixinAgriTechEvolvedPlanter
{
    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lcom/misterd/agritechevolved/Config;getPlanterBaseProcessingTime()I"))
    private static int productivefarming$growthSpeed(int baseTime, Level level, BlockPos pos, BlockState state, PlanterBlockEntity planter) {
        return AgriTechStatsHelper.adjustGrowthTime(baseTime, planter.getStack(0));
    }

    @Inject(method = "harvestPlant", at = @At("HEAD"))
    private void productivefarming$increaseStat(BlockState state, CallbackInfo ci) {
        PlanterBlockEntity planter = (PlanterBlockEntity) (Object) this;
        Level level = planter.getLevel();
        if (level == null || !planter.isReadyToHarvest()) {
            return;
        }
        AgriTechStatsHelper.increaseInputStat(planter.inventory, planter.getStack(0), level.getRandom());
    }

    @ModifyExpressionValue(method = "harvestPlant", at = @At(value = "INVOKE", target = "Lcom/misterd/agritechevolved/blockentity/custom/PlanterBlockEntity;applyYieldModifier(Ljava/util/List;F)Ljava/util/List;"))
    private List<ItemStack> productivefarming$yieldAndSeeds(List<ItemStack> drops) {
        return AgriTechStatsHelper.applyStats(drops, ((PlanterBlockEntity) (Object) this).getStack(0));
    }
}
