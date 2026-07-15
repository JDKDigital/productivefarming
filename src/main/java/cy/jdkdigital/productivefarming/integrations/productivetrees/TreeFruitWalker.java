package cy.jdkdigital.productivefarming.integrations.productivetrees;

import cy.jdkdigital.productivetrees.common.block.ProductiveFruitBlock;
import cy.jdkdigital.productivetrees.common.block.ProductiveLeavesBlock;
import cy.jdkdigital.productivetrees.common.block.ProductiveLogBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class TreeFruitWalker
{
    private TreeFruitWalker() {}

    public static List<BlockPos> collectFruit(ServerLevel level, BlockPos base, int maxNodes, int maxRadius) {
        List<BlockPos> fruit = new ArrayList<>();
        Set<BlockPos> visited = new HashSet<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();

        BlockPos origin = base.immutable();
        queue.add(origin);
        visited.add(origin);

        while (!queue.isEmpty() && visited.size() <= maxNodes) {
            BlockPos pos = queue.poll();
            Block block = level.getBlockState(pos).getBlock();

            if (block instanceof ProductiveFruitBlock) {
                fruit.add(pos);
            }

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) {
                            continue;
                        }
                        BlockPos next = pos.offset(dx, dy, dz);
                        if (visited.contains(next) || chebyshev(origin, next) > maxRadius) {
                            continue;
                        }
                        BlockState nextState = level.getBlockState(next);
                        Block nextBlock = nextState.getBlock();
                        if (nextBlock instanceof ProductiveLogBlock || nextBlock instanceof ProductiveLeavesBlock) {
                            visited.add(next.immutable());
                            queue.add(next.immutable());
                        }
                    }
                }
            }
        }
        return fruit;
    }

    private static int chebyshev(BlockPos a, BlockPos b) {
        return Math.max(Math.abs(a.getX() - b.getX()), Math.max(Math.abs(a.getY() - b.getY()), Math.abs(a.getZ() - b.getZ())));
    }
}
