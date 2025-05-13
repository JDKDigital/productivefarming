package cy.jdkdigital.productivefarming.common.block;

import cy.jdkdigital.productivefarming.registry.ModTags;
import cy.jdkdigital.productivefarming.util.CropConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;

import java.util.function.Supplier;

public class ProductiveCropBlock extends CropBlock implements IAgeableCropBlock
{
    protected final Supplier<Item> itemSupplier;

    public ProductiveCropBlock(CropConfig crop, Properties pProperties) {
        super(pProperties);
        this.itemSupplier = () -> BuiltInRegistries.ITEM.get(BuiltInRegistries.BLOCK.getKey(this).withPath(p -> crop.hasSeed() ? p + "_seeds" : p));
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isAreaLoaded(pos, 1)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light
        if (level.getRawBrightness(pos, 0) >= 9) {
            int i = this.getAge(state);
            if (i < this.getMaxAge()) {
                float f = getGrowthSpeed(state, level, pos);
                if (net.neoforged.neoforge.common.CommonHooks.canCropGrow(level, pos, state, random.nextInt((int)(25.0F / f) + 1) == 0)) {
                    level.setBlock(pos, this.getStateForAge(state, level, pos, i + 1), 2);
                    net.neoforged.neoforge.common.CommonHooks.fireCropGrowPost(level, pos, state);
                }
            }
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (isMaxAge(state)) {
            if (!level.isClientSide) {
                popResource(level, pos.relative(hitResult.getDirection()), getCloneItemStack(level, pos, state));
                // TODO get sound event method
//                level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
                // TODO method for getting reset age, for berries it's 1 or 2
                level.setBlock(pos, state.setValue(this.getAgeProperty(), 0), Block.UPDATE_CLIENTS);
            }
            player.swing(InteractionHand.MAIN_HAND);
            return InteractionResult.SUCCESS_NO_ITEM_USED;
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    protected BlockState getStateForAge(BlockState state, ServerLevel level, BlockPos pos, int age) {
        return super.getStateForAge(age);
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return this.itemSupplier.get();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(getAgeProperty());
    }

    @Override
    protected int getBonemealAgeIncrease(Level pLevel) {
        return Mth.nextInt(pLevel.random, getMaxAge() > 4 ? 2 : 1, getMaxAge() < 4 ? 2 : 5);
    }
}
