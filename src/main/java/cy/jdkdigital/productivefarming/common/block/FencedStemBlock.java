package cy.jdkdigital.productivefarming.common.block;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.common.block.entity.FencedCropBlockEntity;
import cy.jdkdigital.productivefarming.registry.ModTags;
import cy.jdkdigital.productivefarming.util.CropConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.Tags;

import java.util.Optional;

public class FencedStemBlock extends StemBlock implements IAgeableCropBlock
{
    public FencedStemBlock(CropConfig crop, Properties properties) {
        super(
                ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name() + "_leaves")),
                ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name() + "_stem")),
                ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name() + "_seed")), properties);
    }

    @Override
    public IntegerProperty getAgeProperty() {
        return BlockStateProperties.AGE_7;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isAreaLoaded(pos, 1)) return;
        if (level.getRawBrightness(pos, 0) >= 9) {
            float f = CropBlock.getGrowthSpeed(state, level, pos);
            if (CommonHooks.canCropGrow(level, pos, state, random.nextInt((int)(25.0F / f) + 1) == 0)) {
                int i = state.getValue(AGE);
                if (i < 7) {
                    level.setBlock(pos, state.setValue(AGE, i + 1), 2);
                } else {
                    BlockPos leafPos = pos.relative(Direction.UP);
                    BlockState fenceBlockState = level.getBlockState(leafPos);
                    BlockState farmlandBlockState = level.getBlockState(pos.below());
                    if (fenceBlockState.is(Tags.Blocks.FENCES) && farmlandBlockState.is(ModTags.FARMLAND)) {
                        Registry<Block> registry = level.registryAccess().registryOrThrow(Registries.BLOCK);
                        Optional<Block> optional = registry.getOptional(this.fruit);
                        Optional<Block> optional1 = registry.getOptional(this.attachedStem);
                        if (optional.isPresent() && optional1.isPresent()) {
                            level.setBlockAndUpdate(leafPos, optional.get().defaultBlockState());
                            level.setBlockAndUpdate(pos, optional1.get().defaultBlockState());
                            if (level.getBlockEntity(leafPos) instanceof FencedCropBlockEntity fencedCropBlockEntity) {
                                fencedCropBlockEntity.setFence(fenceBlockState);
                            }
                        }
                    }
                }
                CommonHooks.fireCropGrowPost(level, pos, state);
            }
        }
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(ModTags.FARMLAND);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (state.is(Tags.Blocks.FENCES) && mayPlaceOn(level.getBlockState(pos.below()), level, pos)) {

        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
}
