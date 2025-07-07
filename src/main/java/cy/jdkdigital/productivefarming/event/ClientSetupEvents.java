package cy.jdkdigital.productivefarming.event;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.client.render.block.FencedCropBlockEntityRenderer;
import cy.jdkdigital.productivefarming.client.render.entity.layers.WolfHotdogLayer;
import cy.jdkdigital.productivefarming.common.block.entity.ColorfulFlowerBlockEntity;
import cy.jdkdigital.productivefarming.common.block.entity.ColorfulFlowerPotBlockEntity;
import cy.jdkdigital.productivefarming.common.block.entity.CropBlockEntity;
import cy.jdkdigital.productivefarming.common.item.PollenItem;
import cy.jdkdigital.productivefarming.inventory.screen.FarmControllerScreen;
import cy.jdkdigital.productivefarming.inventory.screen.FeedingTroughScreen;
import cy.jdkdigital.productivefarming.registry.FarmingDataComponents;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import cy.jdkdigital.productivefarming.util.CropConfig;
import cy.jdkdigital.productivelib.util.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.model.DynamicFluidContainerModel;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

@EventBusSubscriber(modid = ProductiveFarming.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientSetupEvents
{
    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            for (CropConfig crop: FarmingRegistrator.BERRIES) {
                ItemProperties.register(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name())), ResourceLocation.withDefaultNamespace("count"), (stack, world, entity, i) -> stack.getCount());
            }
        });
    }

    @SubscribeEvent
    public static void registerBlockColors(final RegisterColorHandlersEvent.Block event) {
        event.register((blockState, lightReader, pos, tintIndex) -> {
            return lightReader != null && pos != null ? BiomeColors.getAverageWaterColor(lightReader, pos) : -1;
        }, FarmingRegistrator.WATERING_TROUGH.get());

        FarmingRegistrator.STEMS.forEach(crop -> {
            event.register((blockState, lightReader, pos, tintIndex) -> -2046180, BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "attached_" + crop.name() + "_stem")));
            event.register((blockState, lightReader, pos, tintIndex) -> {
                int i = blockState.getValue(StemBlock.AGE);
                return FastColor.ARGB32.color(i * 32, 255 - i * 8, i * 4);
            }, BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name() + "_stem")));
        });
//        FarmingRegistrator.VINES.forEach(crop -> {
//            event.register((blockState, lightReader, pos, tintIndex) -> -2046180, BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "attached_" + crop.name() + "_stem")));
//            event.register((blockState, lightReader, pos, tintIndex) -> {
//                int i = blockState.getValue(StemBlock.AGE);
//                return FastColor.ARGB32.color(i * 32, 255 - i * 8, i * 4);
//            }, BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name())));
//        });
//        FarmingRegistrator.TRELLIS.forEach(crop -> {
//            event.register((blockState, lightReader, pos, tintIndex) -> -2046180, BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "attached_" + crop.name() + "_stem")));
//            event.register((blockState, lightReader, pos, tintIndex) -> {
//                int i = blockState.getValue(StemBlock.AGE);
//                return FastColor.ARGB32.color(i * 32, 255 - i * 8, i * 4);
//            }, BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name())));
//        });

        event.register((blockState, lightReader, pos, tintIndex) -> {
            return lightReader != null && pos != null && lightReader.getBlockEntity(pos) instanceof CropBlockEntity cropBlockEntity ? cropBlockEntity.getMutationColor() : -1;
        }, FarmingRegistrator.getAllCrops());

        event.register((blockState, lightReader, pos, tintIndex) -> {
            return lightReader != null && pos != null && lightReader.getBlockEntity(pos) instanceof ColorfulFlowerBlockEntity flowerBlockEntity ? flowerBlockEntity.getColor() : -1;
        }, FarmingRegistrator.getFlowers());

        event.register((blockState, lightReader, pos, tintIndex) -> {
            return lightReader != null && pos != null && lightReader.getBlockEntity(pos) instanceof ColorfulFlowerPotBlockEntity flowerBlockEntity ? flowerBlockEntity.getColor() : -1;
        }, FarmingRegistrator.getFlowerPots());
    }

    @SubscribeEvent
    public static void registerItemColors(final RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> PollenItem.getColor(stack), FarmingRegistrator.POLLEN.get());

        event.register((stack, tintIndex) -> {
            return tintIndex == 1 ? stack.getOrDefault(FarmingDataComponents.COLOR, -1) : -1;
        }, FarmingRegistrator.getFlowers());

        event.register(new DynamicFluidContainerModel.Colors(), BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "nutrient_water_bucket")));
    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(new IClientFluidTypeExtensions() {
            private static final ResourceLocation UNDERWATER = ResourceLocation.withDefaultNamespace("textures/misc/underwater.png");
            private static final ResourceLocation STILL = ResourceLocation.withDefaultNamespace("block/water_still");
            private static final ResourceLocation FLOWING = ResourceLocation.withDefaultNamespace("block/water_flow");
            private static final ResourceLocation OVERLAY = ResourceLocation.withDefaultNamespace("block/water_overlay");

            @Override
            public ResourceLocation getStillTexture() {
                return STILL;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return FLOWING;
            }

            @Override
            public ResourceLocation getOverlayTexture() {
                return OVERLAY;
            }

            @Override
            public ResourceLocation getRenderOverlayTexture(Minecraft mc) {
                return UNDERWATER;
            }

            @Override
            public int getTintColor() {
                return 0xFF3F76E4;
            }

            @Override
            public int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
                return ColorUtil.darkenColor(BiomeColors.getAverageWaterColor(getter, pos), 0.4f) | 0xFF000000;
            }
        }, NeoForgeRegistries.FLUID_TYPES.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "nutrient_water")));
    }

    @SubscribeEvent
    public static void onScreenRegister(final RegisterMenuScreensEvent event) {
        event.register(FarmingRegistrator.FARM_CONTROLLER_MENU.get(), FarmControllerScreen::new);
        event.register(FarmingRegistrator.FEEDING_TROUGH_MENU.get(), FeedingTroughScreen::new);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(FarmingRegistrator.FENCED_VERTICAL_CROP_BLOCK_ENTITY.get(), FencedCropBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(FarmingRegistrator.FENCED_LEAVES_BLOCK_ENTITY.get(), FencedCropBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(FarmingRegistrator.FENCED_STEM_BLOCK_ENTITY.get(), FencedCropBlockEntityRenderer::new);

        FarmingRegistrator.FISHIES.forEach(fishConfig -> {
            if (fishConfig.entitySupplier() != null) {
                var entityType = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, fishConfig.name()));
                if (entityType != null) {
                    event.registerEntityRenderer(entityType, fishConfig.entityRenderer());
                }
            }
        });
    }

    @SubscribeEvent
    public static void layerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(WolfHotdogLayer.HOTDOG_LAYER, () -> LayerDefinition.create(WolfHotdogLayer.createMeshDefinition(CubeDeformation.NONE), 64, 64));
    }

    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        var renderer = event.getRenderer(EntityType.WOLF);
        if (renderer instanceof WolfRenderer wolfRenderer) {
            wolfRenderer.addLayer(new WolfHotdogLayer((RenderLayerParent<Wolf, WolfModel<Wolf>>) renderer, event.getEntityModels()));
        }
    }
}
