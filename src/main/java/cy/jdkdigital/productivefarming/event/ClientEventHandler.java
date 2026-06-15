package cy.jdkdigital.productivefarming.event;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.client.color.CropMutationTintSource;
import cy.jdkdigital.productivefarming.client.color.FlowerColorTintSource;
import cy.jdkdigital.productivefarming.client.color.FlowerItemTintSource;
import cy.jdkdigital.productivefarming.client.color.PollenItemTintSource;
import cy.jdkdigital.productivefarming.client.color.StemAgeTintSource;
import cy.jdkdigital.productivefarming.client.render.block.FencedCropBlockEntityRenderer;
import cy.jdkdigital.productivefarming.client.render.entity.layers.WolfHotdogLayer;
import cy.jdkdigital.productivefarming.client.render.item.SeedBagItemRenderer;
import cy.jdkdigital.productivefarming.inventory.screen.FarmControllerScreen;
import cy.jdkdigital.productivefarming.inventory.screen.FeedingTroughScreen;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import cy.jdkdigital.productivelib.util.ColorUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.BlockPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterFluidModelsEvent;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;

import java.util.List;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;

@EventBusSubscriber(modid = ProductiveFarming.MODID, value = Dist.CLIENT)
public class ClientEventHandler
{
    private static final Material WATER_STILL = new Material(Identifier.withDefaultNamespace("block/water_still"));
    private static final Material WATER_FLOWING = new Material(Identifier.withDefaultNamespace("block/water_flow"));
    private static final Material WATER_OVERLAY = new Material(Identifier.withDefaultNamespace("block/water_overlay"));

    @SubscribeEvent
    public static void registerBlockTintSources(RegisterColorHandlersEvent.BlockTintSources event) {
        event.register(List.of(BlockTintSources.water()), FarmingRegistrator.WATERING_TROUGH.get());

        FarmingRegistrator.STEMS.forEach(crop -> {
            Block stem = BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name() + "_stem"));
            Block attached = BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "attached_" + crop.name() + "_stem"));
            event.register(List.of(StemAgeTintSource.INSTANCE), stem);
            event.register(List.of(BlockTintSources.constant(-2046180)), attached);
        });

        event.register(List.of(CropMutationTintSource.INSTANCE), FarmingRegistrator.getAllCrops());
        event.register(List.of(FlowerColorTintSource.INSTANCE), FarmingRegistrator.getFlowers());
        event.register(List.of(FlowerColorTintSource.INSTANCE), FarmingRegistrator.getFlowerPots());
    }

    @SubscribeEvent
    public static void registerItemTintSources(RegisterColorHandlersEvent.ItemTintSources event) {
        event.register(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "pollen"), PollenItemTintSource.MAP_CODEC);
        event.register(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "flower_color"), FlowerItemTintSource.MAP_CODEC);
    }

    @SubscribeEvent
    public static void registerSpecialModelRenderers(RegisterSpecialModelRendererEvent event) {
        event.register(SeedBagItemRenderer.Unbaked.ID, SeedBagItemRenderer.Unbaked.MAP_CODEC);
    }

    @SubscribeEvent
    public static void onScreenRegister(final RegisterMenuScreensEvent event) {
        event.register(FarmingRegistrator.FARM_CONTROLLER_MENU.get(), FarmControllerScreen::new);
        event.register(FarmingRegistrator.FEEDING_TROUGH_MENU.get(), FeedingTroughScreen::new);
    }

    @SubscribeEvent
    public static void registerFluidModels(RegisterFluidModelsEvent event) {
        var model = new FluidModel.Unbaked(WATER_STILL, WATER_FLOWING, WATER_OVERLAY, BlockTintSources.constant(0xFF3F76E4));
        event.register(model, FarmingRegistrator.NUTRIENT_WATER.get());
        event.register(model, FarmingRegistrator.NUTRIENT_WATER.get().getFlowing());
    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(new IClientFluidTypeExtensions() {
            private static final Identifier UNDERWATER = Identifier.withDefaultNamespace("textures/misc/underwater.png");

            @Override
            public Identifier getRenderOverlayTexture(Minecraft mc) {
                return UNDERWATER;
            }

            @Override
            public void modifyFogColor(@NotNull Camera camera, float partialTick, @NotNull ClientLevel level, int renderDistance, float darkenWorldAmount, @NotNull Vector4f fluidFogColor) {
                BlockPos pos = camera.blockPosition();
                int color = ColorUtil.darkenColor(BiomeColors.getAverageWaterColor(level, pos), 0.4f);
                fluidFogColor.x = (color >> 16 & 255) / 255.0F;
                fluidFogColor.y = (color >> 8 & 255) / 255.0F;
                fluidFogColor.z = (color & 255) / 255.0F;
            }
        }, ProductiveFarming.FLUID_TYPES.getEntries().stream()
                .filter(holder -> holder.getId().getPath().equals("nutrient_water"))
                .findFirst().orElseThrow().get());
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(FarmingRegistrator.FENCED_VERTICAL_CROP_BLOCK_ENTITY.get(), FencedCropBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(FarmingRegistrator.FENCED_LEAVES_BLOCK_ENTITY.get(), FencedCropBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(FarmingRegistrator.FENCED_STEM_BLOCK_ENTITY.get(), FencedCropBlockEntityRenderer::new);

        FarmingRegistrator.FISHIES.forEach(fishConfig -> {
            if (fishConfig.entitySupplier() != null && fishConfig.entityRenderer() != null) {
                BuiltInRegistries.ENTITY_TYPE.get(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, fishConfig.name()))
                        .map(Holder::value)
                        .ifPresent(entityType -> event.registerEntityRenderer(entityType, fishConfig.entityRenderer()));
            }
        });
    }

    @SubscribeEvent
    public static void layerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(WolfHotdogLayer.HOTDOG_LAYER, () -> LayerDefinition.create(WolfHotdogLayer.createMeshDefinition(CubeDeformation.NONE), 64, 64));
    }

    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        if (event.getRenderer(EntityType.WOLF) instanceof WolfRenderer wolfRenderer) {
            wolfRenderer.addLayer(new WolfHotdogLayer(wolfRenderer, event.getEntityModels()));
        }
    }
}
