package cy.jdkdigital.productivefarming.common.block;

import com.mojang.datafixers.DataFixUtils;
import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.common.block.entity.CropBlockEntity;
import cy.jdkdigital.productivefarming.common.block.entity.FencedCropBlockEntity;
import cy.jdkdigital.productivefarming.util.CropConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.util.TriState;

abstract class FencedPlantLeafBlock extends ProductiveCropBlock
{
    protected final ResourceKey<Block> stem;
    protected final ResourceKey<Item> fruit;

    public FencedPlantLeafBlock(CropConfig crop, Properties pProperties) {
        super(crop, pProperties);

        this.stem = ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "attached_" + crop.name() + "_stem"));
        this.fruit = ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name()));
        this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.FACING, Direction.DOWN).setValue(BlockStateProperties.DISTANCE, 1));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext pContext) {
        Vec3 vec3 = state.getOffset(level, pos);
        return Shapes.block().move(vec3.x, vec3.y, vec3.z);
    }

    @Override
    protected float getMaxHorizontalOffset() {
        return 0.05F;
    }

    @Override
    protected float getMaxVerticalOffset() {
        return 0.03F;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return Shapes.block();
    }

    abstract Direction[] validGrowthDirections(Level level, BlockPos pos);

    abstract boolean isValidPropagationLeaf(ServerLevel level, BlockPos pos, Direction dir);

    protected BlockState propagationState(ServerLevel level, BlockPos fromPos, Direction dir) {
        var attachedState = level.getBlockState(fromPos);
        return this.defaultBlockState().setValue(BlockStateProperties.FACING, dir.getOpposite()).setValue(BlockStateProperties.DISTANCE, attachedState.hasProperty(BlockStateProperties.DISTANCE) ? Math.min(BlockStateProperties.MAX_DISTANCE, attachedState.getValue(BlockStateProperties.DISTANCE) + 1) : 1);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);

        // Update fence state when a fence is placed next to the crop
        var blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof FencedCropBlockEntity fencedCropBlockEntity && fencedCropBlockEntity.getFence() != null && fencedCropBlockEntity.getFence().is(BlockTags.FENCES)) {
            for (Direction dir : Direction.values()) {
                if (dir.getAxis().isHorizontal()) {
                    var fenceState = fencedCropBlockEntity.getFence();
                    if (level.getBlockState(pos.relative(dir)).is(BlockTags.FENCES)) {
                        fenceState = fenceState.setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(dir), true);
                    } else {
                        fenceState = fenceState.setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(dir), false);
                    }
                    fencedCropBlockEntity.setFence(fenceState);
                }
            }
            fencedCropBlockEntity.setChanged();
        }
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(DataFixUtils.orElse(level.registryAccess().registryOrThrow(Registries.ITEM).getOptional(this.fruit), this));
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isAreaLoaded(pos, 1)) return;
        if (level.getRawBrightness(pos, 0) >= 9) {
            float f = getGrowthSpeed(state, level, pos);
            // Check if there's any fences touching the leaf and replace with leaf copies
            for (Direction dir : validGrowthDirections(level, pos)) {
                var fenceState = level.getBlockState(pos.relative(dir));
                if (fenceState.is(Tags.Blocks.FENCES)) {
                    if (isValidPropagationLeaf(level, pos, dir)) {
                        if (random.nextInt((int) (25.0F / (f * (getAge(state) + 1))) + 1) == 0) {
                            // replace
                            level.setBlockAndUpdate(pos.relative(dir), propagationState(level, pos, dir));
                            if (level.getBlockEntity(pos.relative(dir)) instanceof FencedCropBlockEntity fencedCropBlockEntity) {
                                fencedCropBlockEntity.setFence(fenceState);
                                if (level.getBlockEntity(pos) instanceof CropBlockEntity cropBlockEntity) {
                                    fencedCropBlockEntity.copyTraitsFromOtherCrop(cropBlockEntity);
                                }
                            }
                        }
                    }
                }
            }
            int i = this.getAge(state);
            if (i < this.getMaxAge() && canGrow(level, pos, state)) {
                if (CommonHooks.canCropGrow(level, pos, state, random.nextInt((int)(25.0F / f) + 1) == 0)) {
                    level.setBlock(pos, state.setValue(this.getAgeProperty(), i  + 1), Block.UPDATE_CLIENTS);
                    CommonHooks.fireCropGrowPost(level, pos, state);
                }
            }
        }
    }

    protected boolean canGrow(ServerLevel level, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(BlockStateProperties.FACING).add(BlockStateProperties.DISTANCE);
    }

    @Override
    public boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState, Direction dir) {
        return neighborState.is(this);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof FencedCropBlockEntity fencedCropBlockEntity && fencedCropBlockEntity.getFence() != null) {
            level.setBlockAndUpdate(pos, fencedCropBlockEntity.getFence());
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        net.neoforged.neoforge.common.util.TriState soilDecision = level.getBlockState(pos.below()).canSustainPlant(level, pos.below(), net.minecraft.core.Direction.UP, state);
        if (!soilDecision.isDefault()) return soilDecision.isTrue();

        var stateAt = level.getBlockState(pos); // where the plant will go
        return (stateAt.is(this) || stateAt.is(Tags.Blocks.FENCES)) && super.canSurvive(state, level, pos);
    }

    @Override
    public TriState canSustainPlant(BlockState state, BlockGetter level, BlockPos soilPosition, Direction facing, BlockState plant) {
        return level.getBlockState(soilPosition).is(this) ? TriState.TRUE : TriState.DEFAULT;
    }
}
