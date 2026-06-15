package cy.jdkdigital.productivefarming.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import cy.jdkdigital.productivefarming.common.block.entity.FencedCropBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import javax.annotation.Nonnull;

public class FencedCropBlockEntityRenderer implements BlockEntityRenderer<FencedCropBlockEntity, FencedCropBlockEntityRenderer.FencedCropRenderState>
{
    private final BlockModelResolver blockModelResolver;

    public FencedCropBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.blockModelResolver = context.blockModelResolver();
    }

    @Override
    public FencedCropRenderState createRenderState() {
        return new FencedCropRenderState();
    }

    @Override
    public void extractRenderState(FencedCropBlockEntity be, FencedCropRenderState state, float partialTicks, @Nonnull Vec3 cameraPos, ModelFeatureRenderer.@Nullable CrumblingOverlay crumbling) {
        BlockEntityRenderState.extractBase(be, state, crumbling);

        BlockState fence = be.getFence();
        state.hasFence = fence != null;
        if (state.hasFence) {
            this.blockModelResolver.update(state.fenceModel, fence, BlockDisplayContext.create());
        } else {
            state.fenceModel.clear();
        }
    }

    @Override
    public void submit(FencedCropRenderState state, @Nonnull PoseStack poseStack, @Nonnull SubmitNodeCollector collector, @Nonnull CameraRenderState cameraState) {
        if (state.hasFence && !state.fenceModel.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.0F, -0.0001F, 0.0F);
            state.fenceModel.submitMultiLayer(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
    }

    public static class FencedCropRenderState extends BlockEntityRenderState
    {
        public boolean hasFence;
        public final BlockModelRenderState fenceModel = new BlockModelRenderState();
    }
}
