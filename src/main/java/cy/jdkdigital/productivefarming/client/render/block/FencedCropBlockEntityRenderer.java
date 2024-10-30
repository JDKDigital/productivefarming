package cy.jdkdigital.productivefarming.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import cy.jdkdigital.productivefarming.common.block.entity.FencedCropBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.neoforged.neoforge.client.model.data.ModelData;

import javax.annotation.Nonnull;

public class FencedCropBlockEntityRenderer implements BlockEntityRenderer<FencedCropBlockEntity>
{
    public FencedCropBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    public void render(FencedCropBlockEntity blockEntity, float partialTicks, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource bufferSource, int combinedLightIn, int combinedOverlayIn) {
        if (blockEntity.getFence() != null && Minecraft.getInstance().level != null) {
            var fenceState = blockEntity.getFence();

            poseStack.pushPose();
            poseStack.translate(0,-0.0001, 0);
            Minecraft.getInstance().getBlockRenderer().renderBatched(fenceState, blockEntity.getBlockPos(), blockEntity.getLevel(), poseStack, bufferSource.getBuffer(RenderType.cutout()), false, blockEntity.getLevel().getRandom(), ModelData.EMPTY, RenderType.solid());
            poseStack.popPose();
        }
    }
}