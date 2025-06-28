package cy.jdkdigital.productivefarming.common.item;

import cy.jdkdigital.productivefarming.client.render.item.SeedBagItemRenderer;
import cy.jdkdigital.productivefarming.registry.ModTags;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class SeedBagItem extends Item
{
    private final ResourceLocation seed;

    public SeedBagItem(ResourceLocation seed, Properties pProperties) {
        super(pProperties);
        this.seed = seed;
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (pContext.getPlayer() != null && pContext.getLevel().getBlockState(pContext.getClickedPos()).is(ModTags.Blocks.FARMLAND)) {
            var seedItem = BuiltInRegistries.ITEM.get(seed);
            if (seedItem instanceof BlockItem seedBlock) {
                // Plant in a 3x3 area
                var area = (new AABB(pContext.getClickedPos())).inflate(1);

                AtomicInteger plantedSeeds = new AtomicInteger(0);
                BlockPos.betweenClosedStream(area).forEach(blockPos -> {
                    if (pContext.getLevel().getBlockState(blockPos).is(ModTags.Blocks.FARMLAND) && pContext.getLevel().getBlockState(blockPos.above()).isAir()) {
                        pContext.getLevel().setBlockAndUpdate(blockPos.above(), seedBlock.getBlock().defaultBlockState());
                        plantedSeeds.getAndIncrement();
                    }
                });
                if (plantedSeeds.get() < 9) {
                    var iItemHandler = pContext.getPlayer().getCapability(Capabilities.ItemHandler.ENTITY);
                    if (iItemHandler != null) {
                        var leftOver = ItemHandlerHelper.insertItemStacked(iItemHandler, new ItemStack(seedItem, 9 - plantedSeeds.get()), false);
                        if (!leftOver.isEmpty()) {
                            Block.popResource(pContext.getLevel(), pContext.getClickedPos().above(), leftOver);
                        }
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

    public ResourceLocation getSeed() {
        return seed;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions()
        {
            final BlockEntityWithoutLevelRenderer myRenderer = new SeedBagItemRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer()
            {
                return myRenderer;
            }
        });
    }
}
