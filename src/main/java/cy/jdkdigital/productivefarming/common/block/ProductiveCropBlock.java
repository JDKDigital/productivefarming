package cy.jdkdigital.productivefarming.common.block;

import cy.jdkdigital.productivefarming.common.block.entity.CropBlockEntity;
import cy.jdkdigital.productivefarming.common.block.entity.SimpleCropBlockEntity;
import cy.jdkdigital.productivefarming.registry.FarmingDataComponents;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import cy.jdkdigital.productivefarming.util.CropConfig;
import cy.jdkdigital.productivefarming.util.RecipeHelper;
import cy.jdkdigital.productivefarming.util.TraitsHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.CommonHooks;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ProductiveCropBlock extends CropBlock implements IAgeableCropBlock, EntityBlock
{
    public static final IntegerProperty AGE_6 = IntegerProperty.create("age", 0, 6);

    protected final Supplier<Item> itemSupplier;
    private final CropConfig cropConfig;

    public ProductiveCropBlock(CropConfig cropConfig, Properties pProperties) {
        super(pProperties);
        this.itemSupplier = () -> BuiltInRegistries.ITEM.get(BuiltInRegistries.BLOCK.getKey(this).withPath(p -> cropConfig.hasSeed() ? p + "_seeds" : p)).map(Holder::value).orElse(null);
        this.cropConfig = cropConfig;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isAreaLoaded(pos, 1)) return;
        if (level.getRawBrightness(pos, 0) >= 9) {
            int i = this.getAge(state);
            if (i < this.getMaxAge()) {
                if (CommonHooks.canCropGrow(level, pos, state, random.nextInt((int)(25.0F / getModifiedGrowthSpeed(state, level, pos)) + 1) == 0)) {
                    BlockState growthState = this.getStateForAge(state, level, pos, i + 1);
                    level.setBlock(pos, growthState, 2);
                    // Random chance to increase stats when growing to max stage
                    increaseStatOnGrowth(level, growthState, pos);

                    CommonHooks.fireCropGrowPost(level, pos, state);
                }
            }
        }
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof CropBlockEntity cropBlockEntity) {
            if (stack.is(FarmingRegistrator.POLLEN.get()) && stack.has(FarmingDataComponents.POLLEN_BLOCK_COMPONENT)) {
                var recipe = RecipeHelper.getPollinationRecipe(level, BuiltInRegistries.BLOCK.getKey(state.getBlock()), stack.get(FarmingDataComponents.POLLEN_BLOCK_COMPONENT));
                if (recipe != null) {
                    if (!level.isClientSide()) {
                        cropBlockEntity.setMutation(recipe.value().mutation());
                        if (!player.hasInfiniteMaterials()) {
                            stack.shrink(1);
                        }
                        level.levelEvent(2005, pos, 0);
                        return InteractionResult.FAIL;
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (isMaxAge(state)) {
            if (!level.isClientSide() && level.getBlockEntity(pos) instanceof CropBlockEntity cropBlockEntity) {
                var cropStack = getHarvestItemStack(level, pos, state);
                if (state.getBlock() instanceof ProductiveCropBlock cropBlock && !cropBlock.getCropConfig().hasSeed()) {
                    // Apply traits to seedless crops when harvested
                    cropBlockEntity.applyComponentsToItemStack(cropStack);
                }
                if (cropBlockEntity.hasMutation()) {
                    cropStack = cropBlockEntity.getMutatedSeedStack(cropBlockEntity.getMutation());
                    cropBlockEntity.setMutation(null);
                }
                cropStack.grow(cropBlockEntity.getYield());
                popResource(level, pos.relative(hitResult.getDirection()), cropStack);
                // TODO get sound event method
                level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + level.getRandom().nextFloat() * 0.4F);
                level.setBlock(pos, state.setValue(this.getAgeProperty(), getHarvestedAge()), Block.UPDATE_CLIENTS);
                postHarvest(state, level, pos, player);
                return InteractionResult.CONSUME;
            }
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    protected void postHarvest(BlockState state, Level level, BlockPos pos, Player player) {}

    public ItemStack getHarvestItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return BuiltInRegistries.ITEM.get(BuiltInRegistries.BLOCK.getKey(this)).map(Holder::value).map(Item::getDefaultInstance).orElse(ItemStack.EMPTY);
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        var seedStack = BuiltInRegistries.ITEM.get(BuiltInRegistries.BLOCK.getKey(this).withPath(p -> p + (this.cropConfig.hasSeed() ? "_seeds" : ""))).map(Holder::value).map(Item::getDefaultInstance).orElse(ItemStack.EMPTY);
        if (!seedStack.isEmpty() && level.getBlockEntity(pos) instanceof CropBlockEntity cropBlockEntity) {
            cropBlockEntity.applyComponentsToItemStack(seedStack);
        }
        return seedStack;
    }

    @Override
    public IntegerProperty getAgeProperty() {
        return AGE_6;
    }

    @Override
    public int getMaxAge() {
        return 6;
    }

    @Override
    public int getHarvestedAge() {
        return 0;
    }

    protected BlockState getStateForAge(BlockState state, Level level, BlockPos pos, int age) {
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
    public void growCrops(Level level, BlockPos pos, BlockState state) {
        int i = this.getAge(state) + this.getBonemealAgeIncrease(level);
        int j = this.getMaxAge();
        if (i > j) {
            i = j;
        }

        level.setBlock(pos, this.getStateForAge(state, level, pos, i), 2);

        // Increase stats even when grown by bonemeal
        increaseStatOnGrowth(level, state, pos);
    }

    @Override
    protected int getBonemealAgeIncrease(Level pLevel) {
        return Mth.nextInt(pLevel.getRandom(), getMaxAge() > 4 ? 2 : 1, getMaxAge() < 4 ? 2 : 5);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        var drops = super.getDrops(state, params);
        if (params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof CropBlockEntity cropBlockEntity) {
            List<ItemStack> newDrops = new ArrayList<>();
            drops.forEach(cropStack -> {
                if (state.getBlock() instanceof ProductiveCropBlock cropBlock && !cropBlock.getCropConfig().hasSeed()) {
                    // Apply traits to seedless crops when harvested
                    cropBlockEntity.applyComponentsToItemStack(cropStack);
                }
                if (cropBlockEntity.hasMutation()) {
                    cropStack = cropBlockEntity.getMutatedSeedStack(cropBlockEntity.getMutation());
                }
                cropStack.grow(cropBlockEntity.getYield());
                newDrops.add(cropStack);
            });
            cropBlockEntity.setMutation(null);
            return newDrops;
        }
        return drops;
    }

    public static float getModifiedGrowthSpeed(BlockState blockState, BlockGetter level, BlockPos pos) {
        float speed = getGrowthSpeed(blockState, level, pos) * 2;
        if (level.getBlockEntity(pos) instanceof SimpleCropBlockEntity cropBlockEntity) {
            speed /= (cropBlockEntity.getGrowth() + 1);
        }
        return Math.max(1, speed);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SimpleCropBlockEntity(pos, state);
    }

    public CropConfig getCropConfig() {
        return cropConfig;
    }

    private void increaseStatOnGrowth(Level level, BlockState growthState, BlockPos pos) {
        if (growthState.getValue(getAgeProperty()) == getMaxAge() && level.getBlockEntity(pos) instanceof CropBlockEntity cropBlockEntity) {
            String trait = TraitsHelper.rollIncreasedStat(level.getRandom(), BuiltInRegistries.ITEM.wrapAsHolder(getBaseSeedId().asItem()));
            if (trait != null) {
                cropBlockEntity.increaseStat(trait);
            }
        }
    }
}
