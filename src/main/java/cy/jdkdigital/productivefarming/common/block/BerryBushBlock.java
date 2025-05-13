package cy.jdkdigital.productivefarming.common.block;

import cy.jdkdigital.productivefarming.registry.ModTags;
import cy.jdkdigital.productivefarming.util.CropConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

import java.util.function.Supplier;

public class BerryBushBlock extends ProductiveCropBlock
{
    public BerryBushBlock(CropConfig crop, Properties pProperties) {
        super(crop, pProperties);
    }

//    @Override
//    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
//        int i = state.getValue(AGE);
//        boolean isFullyGrown = i == 3;
//        return !isFullyGrown && stack.is(ModTags.FERTILIZERS)
//                ? ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION
//                : super.useItemOn(stack, state, level, pos, player, hand, hitResult);
//    }

//    @Override
//    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
//        int age = state.getValue(AGE);
//        boolean isFullyGrown = age == 3;
//        if (age > 1) {
//            int j = 1 + level.random.nextInt(2);
//            popResource(level, pos, new ItemStack(itemSupplier.get(), j + (isFullyGrown ? 1 : 0)));
//            level.playSound(
//                    null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F
//            );
//            BlockState blockstate = state.setValue(AGE, Integer.valueOf(1));
//            level.setBlock(pos, blockstate, 2);
//            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, blockstate));
//            return InteractionResult.sidedSuccess(level.isClientSide);
//        } else {
//            return super.useWithoutItem(state, level, pos, player, hitResult);
//        }
//    }
}
