package cy.jdkdigital.productivefarming.util;

import cy.jdkdigital.productivefarming.Config;
import cy.jdkdigital.productivefarming.common.attachment.CropTraitState;
import cy.jdkdigital.productivefarming.common.attachment.CropTraitStore;
import cy.jdkdigital.productivefarming.common.block.ProductiveCropBlock;
import cy.jdkdigital.productivefarming.registry.FarmingAttachments;
import cy.jdkdigital.productivefarming.registry.FarmingDataComponents;
import cy.jdkdigital.productivefarming.registry.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class ExternalCropStats
{
    private static final CropTraitState BASE = new CropTraitState(0, 0, 0, 0, Optional.empty());

    public static boolean isEnabled() {
        return Config.SERVER_CONFIG.isLoaded() && Config.SERVER.statsOnExternalCrops.get();
    }

    public static boolean isEligible(BlockState state) {
        return state.is(ModTags.Blocks.EXTERNAL_STAT_CROPS)
                && state.getBlock() instanceof CropBlock
                && !(state.getBlock() instanceof ProductiveCropBlock);
    }

    public static BlockPos canonicalPos(BlockPos pos, BlockState state) {
        if (isUpperHalf(state)) {
            return pos.below();
        }
        return pos;
    }

    public static boolean isUpperHalf(BlockState state) {
        return state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)
                && state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER;
    }

    public static boolean isMaxAge(BlockState state) {
        IntegerProperty age = ((CropBlock) state.getBlock()).getAgeProperty();
        return state.getValue(age) >= Collections.max(age.getPossibleValues());
    }

    public static CropTraitState fromSeedStack(ItemStack seed) {
        return new CropTraitState(
                seed.getOrDefault(FarmingDataComponents.GROWTH, TraitsHelper.getDefaultTrait(seed.typeHolder(), FarmingDataComponents.GROWTH)),
                seed.getOrDefault(FarmingDataComponents.YIELD, TraitsHelper.getDefaultTrait(seed.typeHolder(), FarmingDataComponents.YIELD)),
                seed.getOrDefault(FarmingDataComponents.RESISTANCE, TraitsHelper.getDefaultTrait(seed.typeHolder(), FarmingDataComponents.RESISTANCE)),
                seed.getOrDefault(FarmingDataComponents.MUTABILITY, TraitsHelper.getDefaultTrait(seed.typeHolder(), FarmingDataComponents.MUTABILITY)),
                Optional.empty());
    }

    public static CropTraitState getTrait(Level level, BlockPos pos, BlockState state) {
        BlockPos key = canonicalPos(pos, state);
        CropTraitState trait = store(level, key).get(key);
        return trait != null ? trait : BASE;
    }

    public static void setTrait(Level level, BlockPos pos, BlockState state, CropTraitState trait) {
        update(level, pos, state, current -> trait);
    }

    public static void update(Level level, BlockPos pos, BlockState state, UnaryOperator<CropTraitState> op) {
        BlockPos key = canonicalPos(pos, state);
        LevelChunk chunk = level.getChunkAt(key);
        CropTraitStore store = chunk.getData(FarmingAttachments.CROP_TRAITS);
        CropTraitState current = store.get(key);
        store.put(key, op.apply(current != null ? current : BASE));
        chunk.setData(FarmingAttachments.CROP_TRAITS, store);
    }

    public static void remove(Level level, BlockPos pos, BlockState state) {
        BlockPos key = canonicalPos(pos, state);
        LevelChunk chunk = level.getChunkAt(key);
        CropTraitStore store = chunk.getData(FarmingAttachments.CROP_TRAITS);
        store.remove(key);
        chunk.setData(FarmingAttachments.CROP_TRAITS, store);
    }

    public static boolean shouldForceGrow(RandomSource random, int growth) {
        return growth > 0 && random.nextFloat() < 1f - 1f / (growth + 1f);
    }

    public static void rollStatIncrease(ServerLevel level, BlockPos pos, BlockState state) {
        if (!isMaxAge(state)) {
            return;
        }
        String trait = TraitsHelper.rollIncreasedStat(level.getRandom(), state.getBlock().getCloneItemStack(level, pos, state, false, null).typeHolder());
        if (trait != null) {
            update(level, pos, state, current -> current.increase(trait));
        }
    }

    public static void setMutation(Level level, BlockPos pos, BlockState state, Identifier mutation) {
        update(level, pos, state, current -> current.withMutation(mutation));
    }

    public static void applyToLoot(ServerLevel level, BlockPos pos, BlockState state, List<ItemStack> loot) {
        CropTraitState trait = getTrait(level, pos, state);
        for (ItemStack stack : loot) {
            if (trait.yield() > 0) {
                stack.grow(trait.yield());
            }
            if (stack.is(ModTags.Items.EXTERNAL_SEEDS)) {
                applyTraits(stack, trait);
            }
        }
        trait.mutation().ifPresent(mutation -> {
            ItemStack mutatedSeed = applyTraits(TraitsHelper.mutatedSeed(mutation), trait);
            if (!mutatedSeed.isEmpty()) {
                loot.add(mutatedSeed);
            }
        });
    }

    private static ItemStack applyTraits(ItemStack stack, CropTraitState trait) {
        return TraitsHelper.applyTraits(stack, trait.growth(), trait.yield(), trait.resistance(), trait.mutability());
    }

    private static CropTraitStore store(Level level, BlockPos pos) {
        return level.getChunkAt(pos).getData(FarmingAttachments.CROP_TRAITS);
    }
}
