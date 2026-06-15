package cy.jdkdigital.productivefarming.common.block;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.common.block.entity.FencedCropBlockEntity;
import cy.jdkdigital.productivefarming.common.block.entity.FencedStemBlockEntity;
import cy.jdkdigital.productivefarming.registry.ModTags;
import cy.jdkdigital.productivefarming.util.CropConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class AttachedFencedStemBlock extends VegetationBlock implements EntityBlock
{
    public static final MapCodec<AttachedFencedStemBlock> CODEC = RecordCodecBuilder.mapCodec(
            p_308799_ -> p_308799_.group(
                            ResourceKey.codec(Registries.BLOCK).fieldOf("fruit").forGetter(block -> block.fruit),
                            ResourceKey.codec(Registries.BLOCK).fieldOf("stem").forGetter(block -> block.stem),
                            ResourceKey.codec(Registries.ITEM).fieldOf("seed").forGetter(block -> block.seed),
                            propertiesCodec()
                    )
                    .apply(p_308799_, AttachedFencedStemBlock::new)
    );
    private final ResourceKey<Block> fruit;
    private final ResourceKey<Block> stem;
    private final ResourceKey<Item> seed;

    private static final VoxelShape AABBS = Block.box(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);

    public AttachedFencedStemBlock(CropConfig crop, Properties properties) {
        this(
            ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name() + "_leaves")),
            ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name())),
            ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name())),
            properties
        );
    }

    public AttachedFencedStemBlock(ResourceKey<Block> fruit, ResourceKey<Block> stem, ResourceKey<Item> seed, Properties properties) {
        super(properties);

        this.fruit = fruit;
        this.stem = stem;
        this.seed = seed;
    }

    @Override
    protected MapCodec<? extends VegetationBlock> codec() {
        return CODEC;
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
    protected ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack(DataFixUtils.orElse(level.registryAccess().lookupOrThrow(Registries.ITEM).getOptional(this.seed), this));
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        if (!neighbourState.is(this.fruit) && directionToNeighbour.equals(Direction.UP)) {
            Optional<Block> optional = level.registryAccess().lookupOrThrow(Registries.BLOCK).getOptional(this.stem);
            if (optional.isPresent()) {
                return optional.get().defaultBlockState().trySetValue(BlockStateProperties.AGE_7, 7);
            }
        }

        return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(ModTags.Blocks.FARMLAND);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HorizontalDirectionalBlock.FACING);
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        BlockState newState = level.getBlockState(pos);
        if (!state.is(newState.getBlock()) && !(newState.getBlock() instanceof StemBlock) && level.getBlockEntity(pos) instanceof FencedCropBlockEntity fencedCropBlockEntity && fencedCropBlockEntity.getFence() != null) {
            level.setBlockAndUpdate(pos, fencedCropBlockEntity.getFence());
        }
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
    }
}
