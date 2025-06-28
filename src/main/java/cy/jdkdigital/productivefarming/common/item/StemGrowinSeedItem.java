package cy.jdkdigital.productivefarming.common.item;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.common.block.entity.FencedCropBlockEntity;
import cy.jdkdigital.productivefarming.common.block.entity.FencedStemBlockEntity;
import cy.jdkdigital.productivefarming.registry.ModTags;
import cy.jdkdigital.productivefarming.world.context.FenceCropBlockPlaceContext;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.common.Tags;

public class StemGrowinSeedItem extends CropBlockItem
{
    public StemGrowinSeedItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var level = context.getLevel();
        var state = level.getBlockState(context.getClickedPos());
        var stateBelow = level.getBlockState(context.getClickedPos().below());
        if (state.is(Tags.Blocks.FENCES) && stateBelow.is(ModTags.Blocks.FARMLAND)) {
            // Click on farmland instead when clicking the fence
            context = new UseOnContext(context.getLevel(), context.getPlayer(), context.getHand(), context.getItemInHand(), new BlockHitResult(Vec3.ZERO, Direction.UP, context.getClickedPos().below(), true));
        } else {
            state = level.getBlockState(context.getClickedPos().above());
        }

        // Try to place the fenced crop
        InteractionResult interactionresult = this.place(new FenceCropBlockPlaceContext(context));
        if (!interactionresult.consumesAction() && context.getItemInHand().has(DataComponents.FOOD)) {
            // If fail, eat instead
            InteractionResult interactionResult = super.use(context.getLevel(), context.getPlayer(), context.getHand()).getResult();
            return interactionResult == InteractionResult.CONSUME ? InteractionResult.CONSUME_PARTIAL : interactionResult;
        }
        // Set fence state on the crop
        if (!level.isClientSide() && level.getBlockEntity(context.getClickedPos().above()) instanceof FencedCropBlockEntity fencedCropBlockEntity) {
            fencedCropBlockEntity.setFence(state.getBlock().defaultBlockState());
        }
        return interactionresult;
    }
}
