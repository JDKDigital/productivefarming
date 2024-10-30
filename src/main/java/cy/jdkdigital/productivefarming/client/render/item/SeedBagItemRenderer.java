package cy.jdkdigital.productivefarming.client.render.item;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import cy.jdkdigital.productivefarming.common.item.SeedBagItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BundleContents;

import java.util.List;

public class SeedBagItemRenderer extends BlockEntityWithoutLevelRenderer
{
    static ItemStack bundle = new ItemStack(Items.BUNDLE);
    static {
        BundleContents bundlecontents = new BundleContents(List.of(new ItemStack(Items.WHEAT_SEEDS)));
        bundle.set(DataComponents.BUNDLE_CONTENTS, bundlecontents);
    }

    public SeedBagItemRenderer() {
        super(null, null);
    }

    @Override
    public void renderByItem(ItemStack pStack, ItemDisplayContext pDisplayContext, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        if (pStack.getItem() instanceof SeedBagItem seedBagItem) {
            if (pDisplayContext.equals(ItemDisplayContext.GUI)) {
                Lighting.setupForFlatItems();
                pPackedLight = 15728880;
            }
            boolean leftHand = pDisplayContext.equals(ItemDisplayContext.FIRST_PERSON_LEFT_HAND);
            boolean firstPerson = leftHand || pDisplayContext.equals(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND);
            boolean inHand = firstPerson || pDisplayContext.equals(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) || pDisplayContext.equals(ItemDisplayContext.THIRD_PERSON_LEFT_HAND);
            var itemRenderer = Minecraft.getInstance().getItemRenderer();

            var bundleModel = itemRenderer.getModel(bundle, Minecraft.getInstance().level, null, 0);
            pPoseStack.pushPose();
            // x-positive = right, y-positive = up, z = depth
            pPoseStack.translate(0.5f, 0.5f, 0.5f);
            itemRenderer.render(bundle, pDisplayContext, leftHand, pPoseStack, pBuffer, pPackedLight, pPackedOverlay, bundleModel);
            var seedItem = BuiltInRegistries.ITEM.get(seedBagItem.getSeed());
            if (!inHand) {
                var seedStack = new ItemStack(seedItem);
                var seedModel = itemRenderer.getModel(seedStack, Minecraft.getInstance().level, null, 0);

//                if (inHand) {
//                    pPoseStack.scale(1.0f, 1.0f, 1.1f);
//                    pPoseStack.translate(0f, 0, 0.1f);
//                } else
                if (!pDisplayContext.equals(ItemDisplayContext.GROUND)) {
                    pPoseStack.translate(0f, -0.15f, 0f);
                }
//                if (!inHand) {
                    pPoseStack.scale(0.5f, 0.5f, 1.1f);
//                }
                itemRenderer.render(seedStack, pDisplayContext, leftHand, pPoseStack, pBuffer, pPackedLight, pPackedOverlay, seedModel);
            }
//            pPoseStack.flush();
            if (pDisplayContext.equals(ItemDisplayContext.GUI)) {
                Lighting.setupFor3DItems();
            }
            pPoseStack.popPose();
        }
    }
}
