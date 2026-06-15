package cy.jdkdigital.productivefarming.common.block;

import cy.jdkdigital.productivefarming.registry.ModTags;
import cy.jdkdigital.productivefarming.util.CropConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.Tags;

public class HerbBlock extends ProductiveCropBlock
{
    public HerbBlock(CropConfig crop, Properties pProperties) {
        super(crop, pProperties);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        boolean isFullyGrown = state.getValue(getAgeProperty()) == getMaxAge();
        if (!isFullyGrown && player.getItemInHand(hand).is(ModTags.Items.FERTILIZERS)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        } else if (state.getValue(getAgeProperty()) > 1 && player.getItemInHand(hand).is(Tags.Items.TOOLS_SHEAR)) {
            popResource(level, pos, getCloneItemStack(level, pos, state, false));
            level.playSound(null, pos, SoundEvents.MOOSHROOM_SHEAR, SoundSource.BLOCKS, 1.0F, 0.8F + level.getRandom().nextFloat() * 0.4F);
            BlockState blockstate = state.setValue(getAgeProperty(), state.getValue(getAgeProperty()) - 1);
            level.setBlock(pos, blockstate, UPDATE_CLIENTS);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, blockstate));
            if (!level.isClientSide()) {
                player.getItemInHand(hand).hurtAndBreak(1, player, hand);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }
}
