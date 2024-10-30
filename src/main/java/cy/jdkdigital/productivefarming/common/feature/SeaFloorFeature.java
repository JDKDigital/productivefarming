package cy.jdkdigital.productivefarming.common.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.CountConfiguration;

import java.util.function.Supplier;

public class SeaFloorFeature extends Feature<CountConfiguration>
{
    private final Supplier<Block> blockSupplier;

    public SeaFloorFeature(Codec<CountConfiguration> codec, Supplier<Block> blockSupplier) {
        super(codec);
        this.blockSupplier = blockSupplier;
    }

    public boolean place(FeaturePlaceContext<CountConfiguration> pContext) {
        int i = 0;
        RandomSource randomsource = pContext.random();
        WorldGenLevel worldgenlevel = pContext.level();
        BlockPos blockpos = pContext.origin();
        int j = pContext.config().count().sample(randomsource);

        for(int k = 0; k < j; ++k) {
            int l = randomsource.nextInt(8) - randomsource.nextInt(8);
            int i1 = randomsource.nextInt(8) - randomsource.nextInt(8);
            int j1 = worldgenlevel.getHeight(Heightmap.Types.OCEAN_FLOOR, blockpos.getX() + l, blockpos.getZ() + i1);
            BlockPos blockpos1 = new BlockPos(blockpos.getX() + l, j1, blockpos.getZ() + i1);
            BlockState blockstate = this.blockSupplier.get().defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, true);
            if (worldgenlevel.getBlockState(blockpos1).is(Blocks.WATER) && blockstate.canSurvive(worldgenlevel, blockpos1)) {
                worldgenlevel.setBlock(blockpos1, blockstate, 2);
                ++i;
            }
        }

        return i > 0;
    }
}
