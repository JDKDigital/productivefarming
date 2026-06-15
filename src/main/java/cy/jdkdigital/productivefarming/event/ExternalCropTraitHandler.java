package cy.jdkdigital.productivefarming.event;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.common.attachment.CropTraitState;
import cy.jdkdigital.productivefarming.registry.FarmingDataComponents;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import cy.jdkdigital.productivefarming.util.ExternalCropStats;
import cy.jdkdigital.productivefarming.util.RecipeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.block.CropGrowEvent;

@EventBusSubscriber(modid = ProductiveFarming.MODID)
public class ExternalCropTraitHandler
{
    @SubscribeEvent
    static void onPlace(BlockEvent.EntityPlaceEvent event) {
        if (!ExternalCropStats.isEnabled() || !(event.getLevel() instanceof Level level) || level.isClientSide()) {
            return;
        }
        BlockState placed = event.getPlacedBlock();
        if (!ExternalCropStats.isEligible(placed)) {
            return;
        }
        if (event.getEntity() instanceof Player player) {
            for (InteractionHand hand : InteractionHand.values()) {
                ItemStack held = player.getItemInHand(hand);
                if (held.getItem() instanceof BlockItem blockItem && blockItem.getBlock() == placed.getBlock()) {
                    CropTraitState trait = ExternalCropStats.fromSeedStack(held);
                    if (!trait.isTrivial()) {
                        ExternalCropStats.setTrait(level, event.getPos(), placed, trait);
                    }
                    return;
                }
            }
        }
    }

    @SubscribeEvent
    static void onCropGrowPre(CropGrowEvent.Pre event) {
        if (!ExternalCropStats.isEnabled() || !(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        BlockState state = event.getState();
        if (!ExternalCropStats.isEligible(state) || ExternalCropStats.isUpperHalf(state)) {
            return;
        }
        if (event.getResult() == CropGrowEvent.Pre.Result.DEFAULT) {
            int growth = ExternalCropStats.getTrait(level, event.getPos(), state).growth();
            if (ExternalCropStats.shouldForceGrow(level.getRandom(), growth)) {
                event.setResult(CropGrowEvent.Pre.Result.GROW);
            }
        }
    }

    @SubscribeEvent
    static void onCropGrowPost(CropGrowEvent.Post event) {
        if (!ExternalCropStats.isEnabled() || !(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        BlockState state = event.getState();
        if (!ExternalCropStats.isEligible(state) || ExternalCropStats.isUpperHalf(state)) {
            return;
        }
        ExternalCropStats.rollStatIncrease(level, event.getPos(), state);
    }

    @SubscribeEvent
    static void onBonemeal(BonemealEvent event) {
        if (!ExternalCropStats.isEnabled() || event.isCanceled() || !(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        if (!event.getStack().is(Items.BONE_MEAL)) {
            return;
        }
        BlockState state = event.getState();
        if (!(state.getBlock() instanceof CropBlock crop) || !ExternalCropStats.isEligible(state)) {
            return;
        }
        BlockPos pos = event.getPos();
        if (!crop.isValidBonemealTarget(level, pos, state)) {
            return;
        }
        event.setSuccessful(true);
        event.setCanceled(true);
        if (crop.isBonemealSuccess(level, level.getRandom(), pos, state)) {
            crop.performBonemeal(level, level.getRandom(), pos, state);
            CommonHooks.fireCropGrowPost(level, pos, state);
        }
        if (event.getPlayer() == null || !event.getPlayer().hasInfiniteMaterials()) {
            event.getStack().shrink(1);
        }
    }

    @SubscribeEvent
    static void onPollen(UseItemOnBlockEvent event) {
        if (!ExternalCropStats.isEnabled() || event.getUsePhase() != UseItemOnBlockEvent.UsePhase.BLOCK) {
            return;
        }
        ItemStack stack = event.getItemStack();
        if (!stack.is(FarmingRegistrator.POLLEN.get()) || !stack.has(FarmingDataComponents.POLLEN_BLOCK_COMPONENT)) {
            return;
        }
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        if (!ExternalCropStats.isEligible(state)) {
            return;
        }
        Identifier cropId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        var recipe = RecipeHelper.getPollinationRecipe(level, cropId, stack.get(FarmingDataComponents.POLLEN_BLOCK_COMPONENT));
        if (recipe == null) {
            return;
        }
        if (!level.isClientSide()) {
            ExternalCropStats.setMutation(level, pos, state, recipe.value().mutation());
            if (event.getPlayer() == null || !event.getPlayer().hasInfiniteMaterials()) {
                stack.shrink(1);
            }
            level.levelEvent(2005, pos, 0);
        }
        event.cancelWithResult(InteractionResult.SUCCESS);
    }

    @SubscribeEvent
    static void onDrops(BlockDropsEvent event) {
        if (!ExternalCropStats.isEnabled()) {
            return;
        }
        BlockState state = event.getState();
        if (!ExternalCropStats.isEligible(state)) {
            return;
        }
        ExternalCropStats.remove(event.getLevel(), event.getPos(), state);
    }
}
