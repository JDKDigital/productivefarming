package cy.jdkdigital.productivefarming.common.block;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.common.block.entity.MushroomGrowthCropBlockEntity;
import cy.jdkdigital.productivefarming.util.CropConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class MushroomGrowthBlock extends ProductiveCropBlock
{
    private static final Map<Direction, VoxelShape[]> SHAPE_BY_AGE = new HashMap<>() {{
        put(Direction.EAST, new VoxelShape[]{
                Block.box(0.0, 0.0, 0.0, 1.0, 16.0, 16.0),
                Block.box(0.0, 0.0, 0.0, 4.0, 16.0, 16.0),
                Block.box(0.0, 0.0, 0.0, 6.0, 16.0, 16.0),
                Block.box(0.0, 0.0, 0.0, 8.0, 16.0, 16.0),
                Block.box(0.0, 0.0, 0.0, 10.0, 16.0, 16.0)
        });
        put(Direction.WEST, new VoxelShape[]{
                Block.box(15.0, 0.0, 0.0, 16.0, 16.0, 16.0),
                Block.box(12.0, 0.0, 0.0, 16.0, 16.0, 16.0),
                Block.box(10.0, 0.0, 0.0, 16.0, 16.0, 16.0),
                Block.box(8.0, 0.0, 0.0, 16.0, 16.0, 16.0),
                Block.box(6.0, 0.0, 0.0, 16.0, 16.0, 16.0)
        });
        put(Direction.NORTH, new VoxelShape[]{
                Block.box(0.0, 0.0, 15.0, 16.0, 16.0, 16.0),
                Block.box(0.0, 0.0, 12.0, 16.0, 16.0, 16.0),
                Block.box(0.0, 0.0, 10.0, 16.0, 16.0, 16.0),
                Block.box(0.0, 0.0, 8.0, 16.0, 16.0, 16.0),
                Block.box(0.0, 0.0, 6.0, 16.0, 16.0, 16.0)
        });
        put(Direction.SOUTH, new VoxelShape[]{
                Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 1.0),
                Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 4.0),
                Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 6.0),
                Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 8.0),
                Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 10.0)
        });
        put(Direction.UP, new VoxelShape[]{
                Block.box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0),
                Block.box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
                Block.box(0.0, 0.0, 0.0, 16.0, 6.0, 16.0),
                Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),
                Block.box(0.0, 0.0, 0.0, 16.0, 10.0, 16.0)
        });
        put(Direction.DOWN, new VoxelShape[]{
                Block.box(0.0, 0.0, 0.0, 16.0, 15.0, 16.0),
                Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0),
                Block.box(0.0, 0.0, 0.0, 16.0, 10.0, 16.0),
                Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),
                Block.box(0.0, 0.0, 0.0, 16.0, 6.0, 16.0)
        });
    }};

    public MushroomGrowthBlock(CropConfig crop, Properties properties) {
        super(crop, properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(this.getAgeProperty(), 0).setValue(BlockStateProperties.FACING, Direction.UP));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MushroomGrowthCropBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(this.getAgeProperty()).add(BlockStateProperties.FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(BlockStateProperties.FACING, context.getHorizontalDirection());
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE_BY_AGE.get(state.getValue(BlockStateProperties.FACING))[this.getAge(state)];
    }

    @Override
    protected BlockState getStateForAge(BlockState state, Level level, BlockPos pos, int age) {
        return super.getStateForAge(age).setValue(BlockStateProperties.FACING, state.getValue(BlockStateProperties.FACING));
    }

    @Override
    public IntegerProperty getAgeProperty() {
        return BlockStateProperties.AGE_4;
    }

    @Override
    public int getMaxAge() {
        return BlockStateProperties.MAX_AGE_4;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return !level.getBlockState(pos.relative(state.getValue(BlockStateProperties.FACING).getOpposite())).isAir();
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(Blocks.COMPOSTER);
    }

    @Override
    public ItemStack getHarvestItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return getBaseSeedId().asItem().getDefaultInstance();
    }

    @Override
    protected ItemLike getBaseSeedId() {
        var item = BuiltInRegistries.ITEM.get(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, getCropConfig().name())).map(Holder::value).orElse(null);
        if (item == null || item.getDefaultInstance().isEmpty()) {
            item = BuiltInRegistries.ITEM.get(Identifier.withDefaultNamespace(getCropConfig().name())).map(Holder::value).orElse(null);
        }
        return item;
    }
}
