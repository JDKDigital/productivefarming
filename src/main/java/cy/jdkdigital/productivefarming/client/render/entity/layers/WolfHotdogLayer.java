package cy.jdkdigital.productivefarming.client.render.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.ItemStack;

public class WolfHotdogLayer extends RenderLayer<Wolf, WolfModel<Wolf>>
{
    public static final ModelLayerLocation HOTDOG_LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "hotdog"), "hotdog");

    private static final ResourceLocation HOTDOG_TEXTURE = ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "textures/entity/wolf/hotdog.png");

    private final WolfModel<Wolf> model;

    public WolfHotdogLayer(RenderLayerParent<Wolf, WolfModel<Wolf>> renderer, EntityModelSet models) {
        super(renderer);
        this.model = new WolfModel<>(models.bakeLayer(WolfHotdogLayer.HOTDOG_LAYER));
    }

    public void render(
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            Wolf livingEntity,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        if (livingEntity.hasArmor()) {
            ItemStack stack = livingEntity.getBodyArmorItem();
            if (stack.is(FarmingRegistrator.HOTDOG_ARMOR)) {
                this.getParentModel().copyPropertiesTo(this.model);
                this.model.prepareMobModel(livingEntity, limbSwing, limbSwingAmount, partialTick);
                this.model.setupAnim(livingEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
                this.maybeRenderHotdogLayer(poseStack, bufferSource, packedLight, stack);
            }
        }
    }

    private void maybeRenderHotdogLayer(PoseStack poseStack, MultiBufferSource buffer, int packedLight, ItemStack armorStack) {
        VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.entityTranslucent(HOTDOG_TEXTURE));
        this.model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);
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
