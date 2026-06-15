package cy.jdkdigital.productivefarming.client.render.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import cy.jdkdigital.productivefarming.ProductiveFarming;
import net.minecraft.client.model.animal.wolf.AdultWolfModel;
import net.minecraft.client.model.animal.wolf.WolfModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.WolfRenderState;
import net.minecraft.resources.Identifier;

import javax.annotation.Nonnull;

public class WolfHotdogLayer extends RenderLayer<WolfRenderState, WolfModel>
{
    public static final ModelLayerLocation HOTDOG_LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "hotdog"), "hotdog");

    @SuppressWarnings("unused")
    private static final Identifier HOTDOG_TEXTURE = Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "textures/entity/wolf/hotdog.png");

    @SuppressWarnings("unused")
    private final WolfModel model;

    public WolfHotdogLayer(RenderLayerParent<WolfRenderState, WolfModel> renderer, EntityModelSet models) {
        super(renderer);
        this.model = new AdultWolfModel(models.bakeLayer(WolfHotdogLayer.HOTDOG_LAYER));
    }

    @Override
    public void submit(@Nonnull PoseStack poseStack, @Nonnull SubmitNodeCollector collector, int packedLight, @Nonnull WolfRenderState state, float yRot, float xRot) {
    }

    public static MeshDefinition createMeshDefinition(CubeDeformation cubeDeformation) {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(-1.0F, 13.5F, -7.0F));
        head.addOrReplaceChild(
                "real_head",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-2.0F, -3.0F, -2.0F, 6.0F, 6.0F, 4.0F, cubeDeformation)
                        .texOffs(16, 14)
                        .addBox(-2.0F, -5.0F, 0.0F, 2.0F, 2.0F, 1.0F, cubeDeformation)
                        .texOffs(16, 14)
                        .addBox(2.0F, -5.0F, 0.0F, 2.0F, 2.0F, 1.0F, cubeDeformation)
                        .texOffs(0, 10)
                        .addBox(-0.5F, -0.001F, -5.0F, 3.0F, 3.0F, 4.0F, cubeDeformation),
                PartPose.ZERO
        );
        PartDefinition body = partdefinition.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -2.0F, -3.0F, 6.0F, 9.0F, 6.0F, cubeDeformation),
                PartPose.offsetAndRotation(0.0F, 14.0F, 2.0F, (float) (Math.PI / 2), 0.0F, 0.0F)
        );
        partdefinition.addOrReplaceChild(
                "upper_body",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -3.0F, -3.0F, 8.0F, 6.0F, 7.0F, cubeDeformation),
                PartPose.offsetAndRotation(-1.0F, 14.0F, -3.0F, (float) (Math.PI / 2), 0.0F, 0.0F)
        );
        CubeListBuilder cubelistbuilder = CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, cubeDeformation);
        partdefinition.addOrReplaceChild("right_hind_leg", cubelistbuilder, PartPose.offset(-2.5F, 16.0F, 7.0F));
        partdefinition.addOrReplaceChild("left_hind_leg", cubelistbuilder, PartPose.offset(0.5F, 16.0F, 7.0F));
        partdefinition.addOrReplaceChild("right_front_leg", cubelistbuilder, PartPose.offset(-2.5F, 16.0F, -4.0F));
        partdefinition.addOrReplaceChild("left_front_leg", cubelistbuilder, PartPose.offset(0.5F, 16.0F, -4.0F));
        PartDefinition tail = partdefinition.addOrReplaceChild(
                "tail", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.0F, 12.0F, 8.0F, (float) (Math.PI / 5), 0.0F, 0.0F)
        );
        tail.addOrReplaceChild(
                "real_tail", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, cubeDeformation), PartPose.ZERO
        );

        body.addOrReplaceChild("hotdog", CubeListBuilder.create()
                .texOffs(32, 46).addBox(-5.0F, -16.0F, -4.0F, 3.0F, 5.0F, 13.0F, new CubeDeformation(0.0F))
                .texOffs(32, 28).addBox(2.0F, -16.0F, -4.0F, 3.0F, 5.0F, 13.0F, new CubeDeformation(0.0F))
                .texOffs(0, 49).addBox(-2.0F, -17.0F, -3.0F, 4.0F, 4.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0F, -11.0F, -1.5708F, 0.0F, 0.0F));

        return meshdefinition;
    }
}
