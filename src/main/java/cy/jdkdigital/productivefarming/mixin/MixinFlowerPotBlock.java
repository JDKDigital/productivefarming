package cy.jdkdigital.productivefarming.mixin;

import cy.jdkdigital.productivefarming.common.block.entity.ColorfulFlowerPotBlockEntity;
import cy.jdkdigital.productivefarming.registry.FarmingDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = FlowerPotBlock.class)
public abstract class MixinFlowerPotBlock
{
    @Inject(at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;consume(ILnet/minecraft/world/entity/LivingEntity;)V")}, method = {"useItemOn"})
    public void canSurvive(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (stack.has(FarmingDataComponents.COLOR) && level.getBlockEntity(pos) instanceof ColorfulFlowerPotBlockEntity colorfulFlowerPotBlock) {
            colorfulFlowerPotBlock.setColor(stack.getOrDefault(FarmingDataComponents.COLOR, -1));
        }
    }
}
