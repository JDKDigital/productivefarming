package cy.jdkdigital.productivefarming.common.item;

import cy.jdkdigital.productivefarming.registry.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;

import java.util.concurrent.atomic.AtomicInteger;

public class SeedBagItem extends Item
{
    private final Identifier seed;

    public SeedBagItem(Identifier seed, Properties pProperties) {
        super(pProperties);
        this.seed = seed;
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (pContext.getPlayer() != null && pContext.getLevel().getBlockState(pContext.getClickedPos()).is(ModTags.Blocks.FARMLAND)) {
            Item seedItem = BuiltInRegistries.ITEM.get(seed).map(Holder::value).orElse(null);
            if (seedItem instanceof BlockItem seedBlock) {
                // Plant in a 3x3 area
                var area = (new AABB(pContext.getClickedPos())).setMinX(pContext.getClickedPos().getX()-1).setMinZ(pContext.getClickedPos().getZ()-1);

                AtomicInteger plantedSeeds = new AtomicInteger(0);
                BlockPos.betweenClosedStream(area).forEach(blockPos -> {
                    if (pContext.getLevel().getBlockState(blockPos).is(ModTags.Blocks.FARMLAND) && pContext.getLevel().getBlockState(blockPos.above()).isAir()) {
                        pContext.getLevel().setBlockAndUpdate(blockPos.above(), seedBlock.getBlock().defaultBlockState());
                        plantedSeeds.getAndIncrement();
                    }
                });
                if (plantedSeeds.get() < 9) {
                    ResourceHandler<ItemResource> itemHandler = pContext.getPlayer().getCapability(Capabilities.Item.ENTITY);
                    int toReturn = 9 - plantedSeeds.get();
                    int inserted = itemHandler == null ? 0 : ResourceHandlerUtil.insertStacking(itemHandler, ItemResource.of(seedItem), toReturn, null);
                    int leftOver = toReturn - inserted;
                    if (leftOver > 0) {
                        Block.popResource(pContext.getLevel(), pContext.getClickedPos().above(), new ItemStack(seedItem, leftOver));
                    }
                }
                if (plantedSeeds.get() > 0 && !pContext.getPlayer().isCreative()) {
                    pContext.getItemInHand().shrink(1);
                    pContext.getPlayer().swing(pContext.getHand());
                }
                return InteractionResult.CONSUME;
            }
        }

        return super.useOn(pContext);
    }

    public Identifier getSeed() {
        return seed;
    }

}
