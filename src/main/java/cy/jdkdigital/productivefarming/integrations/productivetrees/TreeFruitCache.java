package cy.jdkdigital.productivefarming.integrations.productivetrees;

import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeFruitCache
{
    public static final int RESCAN_INTERVAL = 1200;

    private final Map<BlockPos, List<BlockPos>> fruitByTrunk = new HashMap<>();
    private long lastScanTick = Long.MIN_VALUE;

    public boolean needsRescan(long gameTime) {
        return fruitByTrunk.isEmpty() || gameTime - lastScanTick >= RESCAN_INTERVAL;
    }

    public void markScanned(long gameTime) {
        this.lastScanTick = gameTime;
    }

    public void clear() {
        this.fruitByTrunk.clear();
    }

    public void put(BlockPos trunk, List<BlockPos> fruit) {
        this.fruitByTrunk.put(trunk.immutable(), new ArrayList<>(fruit));
    }

    public Map<BlockPos, List<BlockPos>> trunks() {
        return this.fruitByTrunk;
    }

    public boolean isEmpty() {
        return this.fruitByTrunk.isEmpty();
    }
}
