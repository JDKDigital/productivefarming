package cy.jdkdigital.productivefarming.common.item;

import cy.jdkdigital.productivefarming.registry.FarmingDataComponents;
import cy.jdkdigital.productivefarming.registry.ModTags;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class CornPipeItem extends Item
{
    public CornPipeItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.EAT;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        boolean hasTobacco = player.getItemInHand(InteractionHand.OFF_HAND).is(ModTags.Items.DRIED_TOBACCO);
        boolean hasLighter = player.getItemInHand(InteractionHand.OFF_HAND).is(Items.FLINT_AND_STEEL);
        boolean isStuffed = itemStack.getOrDefault(FarmingDataComponents.CHARGES, 16) > 0;
        if (isStuffed && itemStack.getOrDefault(FarmingDataComponents.IS_LIT, false)) {
            if (player instanceof ServerPlayer serverPlayer) {
                itemStack.hurtAndBreak(1, player, hand);
                int charges = itemStack.getOrDefault(FarmingDataComponents.CHARGES, 32);
                if (charges == 1) {
                    itemStack.set(FarmingDataComponents.IS_LIT, false);
                }
                itemStack.set(FarmingDataComponents.CHARGES, charges - 1);
                serverPlayer.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
            } else {
                var pos = player.blockPosition().relative(Direction.UP);
                for (int i = 0; i < 5; i++) {
                    level.addParticle(
                            level.getRandom().nextBoolean() ? ParticleTypes.CAMPFIRE_SIGNAL_SMOKE : ParticleTypes.CAMPFIRE_COSY_SMOKE,
                            (double)pos.getX() + 0.5 + level.getRandom().nextDouble() / 4.0 * (double)(level.getRandom().nextBoolean() ? 1 : -1) + player.getLookAngle().x,
                            (double)pos.getY() + 0.4,
                            (double)pos.getZ() + 0.5 + level.getRandom().nextDouble() / 4.0 * (double)(level.getRandom().nextBoolean() ? 1 : -1) + player.getLookAngle().z,
                            (level.getRandom().nextBoolean() ? 1 : -1) * (float) level.getRandom().nextInt(0, 4) / 100f,
                            (float) level.getRandom().nextInt(1, 7) / 100f,
                            (level.getRandom().nextBoolean() ? 1 : -1) * (float) level.getRandom().nextInt(0, 4) / 100f
                    );
                }
            }
            return InteractionResult.SUCCESS;
        } else if (hasLighter && isStuffed) {
            if (player instanceof ServerPlayer serverPlayer) {
                itemStack.set(FarmingDataComponents.IS_LIT, true);
                itemStack.hurtAndBreak(1, player, InteractionHand.OFF_HAND);
            } else {
                var pos = player.blockPosition().relative(Direction.UP);
                level.addParticle(
                        ParticleTypes.LAVA,
                        (double)pos.getX() + 0.5,
                        (double)pos.getY() + 0.5,
                        (double)pos.getZ() + 0.5,
                        level.getRandom().nextFloat() / 2.0F,
                        5.0E-5,
                        level.getRandom().nextFloat() / 2.0F
                );
                level.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
            }
            return InteractionResult.SUCCESS;
        } else if (hasTobacco && !isStuffed) {
            player.startUsingItem(hand);
            return InteractionResult.CONSUME;
        } else {
            return InteractionResult.FAIL;
        }
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 60;
    }

    @Override
    public @NotNull ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        boolean hasTobacco = livingEntity.getItemInHand(InteractionHand.OFF_HAND).is(ModTags.Items.DRIED_TOBACCO);
        if (hasTobacco) {
            stack.set(FarmingDataComponents.CHARGES, 32);
            if (!livingEntity.hasInfiniteMaterials()) {
                livingEntity.getItemInHand(InteractionHand.OFF_HAND).shrink(1);
            }
        }
        return super.finishUsingItem(stack, level, livingEntity);
    }
}
