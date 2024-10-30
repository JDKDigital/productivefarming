package cy.jdkdigital.productivefarming.common.block;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.common.block.entity.VineLeafBlockEntity;
import cy.jdkdigital.productivefarming.util.CropConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;

public class VineLeafBlock extends FencedPlantLeafBlock
{
    public VineLeafBlock(CropConfig crop, Properties pProperties) {
        super(crop, pProperties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new VineLeafBlockEntity(pPos, pState);
    }

    @Override
    Direction[] validGrowthDirections(Level level, BlockPos pos) {
        return new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
    }

    @Override
    boolean isValidPropagationLeaf(ServerLevel level, BlockPos pos, Direction dir) {
        var attachedState = level.getBlockState(pos);
        var requiresSupport = attachedState.getValue(BlockStateProperties.DISTANCE)%2 == 0;
        return level.getBlockState(pos.below()).is(this.stem) ||
                (
                        attachedState.getValue(BlockStateProperties.DISTANCE) < 4 &&
                        (level.getBlockState(pos.relative(dir).below()).is(Tags.Blocks.FENCES) || !requiresSupport)
                );
    }
}
