package cy.jdkdigital.productivefarming.integrations.productivetrees;

import com.mojang.authlib.GameProfile;
import cy.jdkdigital.productivetrees.common.block.ProductiveFruitBlock;
import cy.jdkdigital.productivetrees.common.block.ProductiveLogBlock;
import cy.jdkdigital.productivelib.util.harvest.HarvestCompatHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public final class ProductiveTreesCompat
{
    private ProductiveTreesCompat() {}

    private static final int MAX_NODES = 4096;
    private static final int MAX_RADIUS = 24;

    public static void processTreeFruit(ServerLevel level, Collection<BlockPos> farmCells, TreeFruitCache cache, long gameTime, BooleanSupplier nutrientSink, BooleanSupplier outputHasSpace, Consumer<ItemEntity> dropCollector) {
        if (cache.needsRescan(gameTime)) {
            rescan(level, farmCells, cache);
            cache.markScanned(gameTime);
        }
        harvestCached(level, cache, nutrientSink, outputHasSpace, dropCollector);
    }

    private static void rescan(ServerLevel level, Collection<BlockPos> farmCells, TreeFruitCache cache) {
        cache.clear();
        for (BlockPos cell : farmCells) {
            if (level.getBlockState(cell).getBlock() instanceof ProductiveLogBlock) {
                List<BlockPos> fruit = TreeFruitWalker.collectFruit(level, cell, MAX_NODES, MAX_RADIUS);
                if (!fruit.isEmpty()) {
                    cache.put(cell, fruit);
                }
            }
        }
    }

    private static void harvestCached(ServerLevel level, TreeFruitCache cache, BooleanSupplier nutrientSink, BooleanSupplier outputHasSpace, Consumer<ItemEntity> dropCollector) {
        Iterator<Map.Entry<BlockPos, List<BlockPos>>> trunks = cache.trunks().entrySet().iterator();
        while (trunks.hasNext()) {
            Map.Entry<BlockPos, List<BlockPos>> entry = trunks.next();
            if (!(level.getBlockState(entry.getKey()).getBlock() instanceof ProductiveLogBlock)) {
                trunks.remove();
                continue;
            }
            entry.getValue().removeIf(pos -> {
                BlockState state = level.getBlockState(pos);
                if (!(state.getBlock() instanceof ProductiveFruitBlock)) {
                    return true;
                }
                if (ProductiveFruitBlock.isMaxAge(state)) {
                    if (outputHasSpace.getAsBoolean()) {
                        harvest(level, pos);
                        for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, new AABB(pos).inflate(1.0))) {
                            dropCollector.accept(item);
                        }
                    }
                } else if (state.getBlock() instanceof BonemealableBlock bonemealable
                        && bonemealable.isValidBonemealTarget(level, pos, state)
                        && nutrientSink.getAsBoolean()) {
                    bonemealable.performBonemeal(level, level.getRandom(), pos, state);
                    level.levelEvent(1505, pos, 15);
                }
                return false;
            });
        }
    }

    private static void harvest(ServerLevel level, BlockPos pos) {
        FakePlayer fakePlayer = FakePlayerFactory.get(level, new GameProfile(HarvestCompatHandler.FARMER_UUID, "productive_farmer"));
        BlockHitResult hit = new BlockHitResult(Vec3.ZERO, Direction.DOWN, pos, true);
        fakePlayer.gameMode.useItemOn(fakePlayer, level, ItemStack.EMPTY, InteractionHand.MAIN_HAND, hit);
    }
}
