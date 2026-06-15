package cy.jdkdigital.productivefarming.common.block;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CrateBlock extends Block
{
    public CrateBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        Block inkSacCrate = BuiltInRegistries.BLOCK.get(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "ink_sac_crate")).map(Holder::value).orElse(null);
        if (inkSacCrate != null && state.is(inkSacCrate)) {
            for (int i = 0; i < random.nextInt(1) + 1; i++) {
                this.trySpawnDripParticles(level, pos, state);
            }
        }
        super.animateTick(state, level, pos, random);
    }

    private void trySpawnDripParticles(Level level, BlockPos pos, BlockState state) {
        if (state.getFluidState().isEmpty() && !(level.getRandom().nextFloat() < 0.3F)) {
            VoxelShape voxelshape = state.getCollisionShape(level, pos);
            double d0 = voxelshape.max(Direction.Axis.Y);
            if (d0 >= 1.0 && !state.is(BlockTags.IMPERMEABLE)) {
                double d1 = voxelshape.min(Direction.Axis.Y);
                if (d1 > 0.0) {
                    this.spawnParticle(level, pos, voxelshape, (double)pos.getY() + d1 - 0.05);
                } else {
                    BlockPos blockpos = pos.below();
                    BlockState blockstate = level.getBlockState(blockpos);
                    VoxelShape voxelshape1 = blockstate.getCollisionShape(level, blockpos);
                    double d2 = voxelshape1.max(Direction.Axis.Y);
                    if ((d2 < 1.0 || !blockstate.isCollisionShapeFullBlock(level, blockpos)) && blockstate.getFluidState().isEmpty()) {
                        this.spawnParticle(level, pos, voxelshape, (double)pos.getY() - 0.05);
                    }
                }
            }
        }
    }

    private void spawnParticle(Level level, BlockPos pos, VoxelShape shape, double y) {
        this.spawnFluidParticle(
                level,
                (double)pos.getX() + shape.min(Direction.Axis.X),
                (double)pos.getX() + shape.max(Direction.Axis.X),
                (double)pos.getZ() + shape.min(Direction.Axis.Z),
                (double)pos.getZ() + shape.max(Direction.Axis.Z),
                y
        );
    }

    private void spawnFluidParticle(Level particleData, double x1, double x2, double z1, double z2, double y) {
        particleData.addParticle(
                ParticleTypes.DRIPPING_OBSIDIAN_TEAR,
                Mth.lerp(particleData.getRandom().nextDouble(), x1, x2),
                y,
                Mth.lerp(particleData.getRandom().nextDouble(), z1, z2),
                0.0,
                0.0,
                0.0
        );
    }
}
