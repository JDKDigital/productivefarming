package cy.jdkdigital.productivefarming.mixin;

import cy.jdkdigital.productivefarming.Config;
import cy.jdkdigital.productivefarming.ProductiveFarming;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockItem.class)
public abstract class MixinBlockItem
{
    @Inject(at = {@At("RETURN")}, method = {"getPlacementState"}, cancellable = true)
    public void getPlacementState(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
        if (Config.SERVER.traitsOnVanillaCrops.get() && cir.getReturnValue() != null) {
            if (cir.getReturnValue().is(Blocks.WHEAT)) {
                cir.setReturnValue(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "wheat")).defaultBlockState());
            } else if (cir.getReturnValue().is(Blocks.POTATOES)) {
                cir.setReturnValue(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "potato")).defaultBlockState());
            } else if (cir.getReturnValue().is(Blocks.CARROTS)) {
                cir.setReturnValue(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "carrot")).defaultBlockState());
            } else if (cir.getReturnValue().is(Blocks.BEETROOTS)) {
                cir.setReturnValue(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "beetroot")).defaultBlockState());
            }
        }
    }
}
