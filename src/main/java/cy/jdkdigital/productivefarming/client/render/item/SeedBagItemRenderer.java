package cy.jdkdigital.productivefarming.client.render.item;

import com.mojang.serialization.MapCodec;
import com.mojang.blaze3d.vertex.PoseStack;
import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.common.item.SeedBagItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class SeedBagItemRenderer implements SpecialModelRenderer<ItemStack>
{
    @Override
    public @Nullable ItemStack extractArgument(ItemStack stack) {
        if (stack.getItem() instanceof SeedBagItem seedBagItem) {
            Item seedItem = BuiltInRegistries.ITEM.getValue(seedBagItem.getSeed());
            ItemStack seedStack = new ItemStack(seedItem);
            return seedStack.isEmpty() ? null : seedStack;
        }
        return null;
    }

    @Override
    public void submit(@Nullable ItemStack seedStack, PoseStack poseStack, SubmitNodeCollector collector, int packedLight, int packedOverlay, boolean hasFoil, int outlineColor) {
        if (seedStack == null || seedStack.isEmpty()) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        ItemModelResolver resolver = mc.getItemModelResolver();
        ItemStackRenderState seedState = new ItemStackRenderState();
        resolver.updateForTopItem(seedState, seedStack, ItemDisplayContext.GUI, mc.level, null, 0);
        if (seedState.isEmpty()) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.45F, 0.75F);
        poseStack.scale(0.5F, 0.5F, 0.5F);
        seedState.submit(poseStack, collector, packedLight, packedOverlay, outlineColor);
        poseStack.popPose();
    }

    @Override
    public void getExtents(Consumer<Vector3fc> consumer) {
        consumer.accept(new Vector3f(0.0F, 0.0F, 0.0F));
        consumer.accept(new Vector3f(1.0F, 1.0F, 1.0F));
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked<ItemStack> {
        public static final Identifier ID = Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "seed_bag");
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public SpecialModelRenderer<ItemStack> bake(SpecialModelRenderer.BakingContext context) {
            return new SeedBagItemRenderer();
        }
    }
}
