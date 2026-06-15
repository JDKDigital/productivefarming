package cy.jdkdigital.productivefarming.common.block;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.common.block.entity.FencedCropBlockEntity;
import cy.jdkdigital.productivefarming.common.block.entity.FencedStemBlockEntity;
import cy.jdkdigital.productivefarming.registry.ModTags;
import cy.jdkdigital.productivefarming.util.CropConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class FencedStemBlock extends StemBlock implements IAgeableCropBlock, EntityBlock
{
    private static final VoxelShape AABBS = Block.box(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);

    public FencedStemBlock(CropConfig crop, Properties properties) {
        super(
                ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name() + "_leaves")),
                ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "attached_" + crop.name() + "_stem")),
                ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name() + "_seeds")),
                ModTags.Blocks.FARMLAND, ModTags.Blocks.FARMLAND, properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return AABBS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FencedStemBlockEntity(pos, state);
    }

    @Override
    public IntegerProperty getAgeProperty() {
        return BlockStateProperties.AGE_7;
    }

    @Override
    public int getHarvestedAge() {
        return 0;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isAreaLoaded(pos, 1)) return;
        if (level.getRawBrightness(pos, 0) >= 9) {
            float f = CropBlock.getGrowthSpeed(state, level, pos);
            if (CommonHooks.canCropGrow(level, pos, state, random.nextInt((int)(25.0F / f) + 1) == 0)) {
                var stemBlockEntity = level.getBlockEntity(pos);
                if (stemBlockEntity instanceof FencedCropBlockEntity fencedStemBlockEntity && fencedStemBlockEntity.getFence() != null) {
                    int i = state.getValue(AGE);
                    if (i < 7) {
                        level.setBlock(pos, state.setValue(AGE, i + 1), 2);
                    } else {
                        BlockPos leafPos = pos.relative(Direction.UP);
                        BlockState fenceBlockState = level.getBlockState(leafPos);
                        BlockState farmlandBlockState = level.getBlockState(pos.below());
                        if (fenceBlockState.is(Tags.Blocks.FENCES) && farmlandBlockState.is(ModTags.Blocks.FARMLAND)) {
                            Registry<Block> registry = level.registryAccess().lookupOrThrow(Registries.BLOCK);
                            Optional<Block> fruitBlock = registry.getOptional(this.fruit);
                            Optional<Block> attachedStemBlock = registry.getOptional(this.attachedStem);
                            if (fruitBlock.isPresent() && attachedStemBlock.isPresent()) {
                                level.setBlockAndUpdate(leafPos, fruitBlock.get().defaultBlockState());
                                level.setBlockAndUpdate(pos, attachedStemBlock.get().defaultBlockState());
                                if (level.getBlockEntity(leafPos) instanceof FencedCropBlockEntity fencedCropBlockEntity) {
                                    fencedCropBlockEntity.setFence(fenceBlockState);
                                }
                                if (level.getBlockEntity(pos) instanceof FencedCropBlockEntity fencedCropBlockEntity) {
                                    if (fencedStemBlockEntity.getFence() != null) {
                                        fencedCropBlockEntity.setFence(fencedStemBlockEntity.getFence());
                                    }
                                }
                            }
                        }
                    }
                    CommonHooks.fireCropGrowPost(level, pos, state);
                }
            }
        }
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(ModTags.Blocks.FARMLAND);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(Tags.Items.FENCES) && stack.getItem() instanceof BlockItem blockItem && level.getBlockEntity(pos) instanceof FencedStemBlockEntity fencedStemBlockEntity && fencedStemBlockEntity.getFence() == null) {
            fencedStemBlockEntity.setFence(blockItem.getBlock().defaultBlockState());
            return InteractionResult.SUCCESS;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        BlockState newState = level.getBlockState(pos);
        if (!state.is(newState.getBlock()) && !(newState.getBlock() instanceof AttachedFencedStemBlock) && level.getBlockEntity(pos) instanceof FencedCropBlockEntity fencedCropBlockEntity && fencedCropBlockEntity.getFence() != null) {
            level.setBlockAndUpdate(pos, fencedCropBlockEntity.getFence());
        }
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
    }
}
