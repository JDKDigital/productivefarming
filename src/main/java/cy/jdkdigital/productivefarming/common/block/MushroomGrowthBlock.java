package cy.jdkdigital.productivefarming.common.block;

import cy.jdkdigital.productivefarming.common.block.entity.MushroomGrowthCropBlockEntity;
import cy.jdkdigital.productivefarming.util.CropConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class MushroomGrowthBlock extends ProductiveCropBlock
{
    private static final Map<Direction, VoxelShape[]> SHAPE_BY_AGE = new HashMap<>() {{
        put(Direction.EAST, new VoxelShape[]{
                Shapes.empty(),
                Block.box(0.0, 0.0, 0.0, 4.0, 16.0, 16.0),
                Block.box(0.0, 0.0, 0.0, 6.0, 16.0, 16.0),
                Block.box(0.0, 0.0, 0.0, 8.0, 16.0, 16.0),
                Block.box(0.0, 0.0, 0.0, 10.0, 16.0, 16.0)
        });
        put(Direction.WEST, new VoxelShape[]{
                Shapes.empty(),
                Block.box(12.0, 0.0, 0.0, 16.0, 16.0, 16.0),
                Block.box(10.0, 0.0, 0.0, 16.0, 16.0, 16.0),
                Block.box(8.0, 0.0, 0.0, 16.0, 16.0, 16.0),
                Block.box(6.0, 0.0, 0.0, 16.0, 16.0, 16.0)
        });
        put(Direction.NORTH, new VoxelShape[]{
                Shapes.empty(),
                Block.box(0.0, 0.0, 12.0, 16.0, 16.0, 16.0),
                Block.box(0.0, 0.0, 10.0, 16.0, 16.0, 16.0),
                Block.box(0.0, 0.0, 8.0, 16.0, 16.0, 16.0),
                Block.box(0.0, 0.0, 6.0, 16.0, 16.0, 16.0)
        });
        put(Direction.SOUTH, new VoxelShape[]{
                Shapes.empty(),
                Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 4.0),
                Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 6.0),
                Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 8.0),
                Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 10.0)
        });
    }};
    private final ResourceLocation shroom;

    public MushroomGrowthBlock(Properties properties, ResourceLocation shroom) {
        super(new CropConfig(shroom.toString(), false, null), properties); // TODO
        this.shroom = shroom;
        this.registerDefaultState(this.stateDefinition.any().setValue(this.getAgeProperty(), 0).setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MushroomGrowthCropBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(this.getAgeProperty()).add(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection());
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE_BY_AGE.get(state.getValue(BlockStateProperties.HORIZONTAL_FACING))[this.getAge(state)];
    }

    @Override
    protected BlockState getStateForAge(BlockState state, Level level, BlockPos pos, int age) {
        return super.getStateForAge(age).setValue(BlockStateProperties.HORIZONTAL_FACING, state.getValue(BlockStateProperties.HORIZONTAL_FACING));
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
        return !level.getBlockState(pos.relative(state.getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite())).isAir();
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return BuiltInRegistries.ITEM.get(shroom);
    }
}
