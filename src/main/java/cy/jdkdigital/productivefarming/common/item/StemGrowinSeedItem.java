package cy.jdkdigital.productivefarming.common.item;

import cy.jdkdigital.productivefarming.common.block.entity.FencedCropBlockEntity;
import cy.jdkdigital.productivefarming.common.block.entity.FencedStemBlockEntity;
import cy.jdkdigital.productivefarming.registry.ModTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;

public class StemGrowinSeedItem extends ItemNameBlockItem
{
    public StemGrowinSeedItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var level = context.getLevel();
        var state = level.getBlockState(context.getClickedPos());
        var stateBelow = level.getBlockState(context.getClickedPos().below());
        if (state.is(Tags.Blocks.FENCES) && stateBelow.is(ModTags.FARMLAND)) {
            if (!level.isClientSide() && context.getItemInHand().getItem() instanceof StemGrowinSeedItem blockItem) {
                level.setBlockAndUpdate(context.getClickedPos(), blockItem.getBlock().defaultBlockState());
                if (level.getBlockEntity(context.getClickedPos()) instanceof FencedCropBlockEntity fencedCropBlockEntity) {
                    fencedCropBlockEntity.setFence(state.getBlock().defaultBlockState());
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return super.useOn(context);
    }
}
