package cy.jdkdigital.productivefarming.mixin;

import cy.jdkdigital.productivefarming.util.ExternalCropStats;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Pseudo
@Mixin(targets = {"com.blakebr0.mysticalagriculture.block.MysticalCropBlock", "com.blakebr0.mysticalagriculture.block.InferiumCropBlock"}, remap = false)
public abstract class MixinMysticalCrop
{
    @Inject(method = "getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/storage/loot/LootParams$Builder;)Ljava/util/List;", at = @At("RETURN"))
    private void productivefarming$stampDrops(BlockState state, LootParams.Builder builder, CallbackInfoReturnable<List<ItemStack>> cir) {
        if (!ExternalCropStats.isEnabled() || !ExternalCropStats.isEligible(state)) {
            return;
        }
        Vec3 origin = builder.getOptionalParameter(LootContextParams.ORIGIN);
        if (origin == null) {
            return;
        }
        ExternalCropStats.applyToLoot(builder.getLevel(), BlockPos.containing(origin), state, cir.getReturnValue());
    }
}
