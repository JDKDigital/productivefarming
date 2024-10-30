package cy.jdkdigital.productivefarming.event;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.client.render.block.FencedCropBlockEntityRenderer;
import cy.jdkdigital.productivefarming.inventory.screen.FarmControllerScreen;
import cy.jdkdigital.productivefarming.inventory.screen.FeedingTroughScreen;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import cy.jdkdigital.productivefarming.util.CropConfig;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.block.StemBlock;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

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
    }

    @SubscribeEvent
    public static void onScreenRegister(final RegisterMenuScreensEvent event) {
        event.register(FarmingRegistrator.FARM_CONTROLLER_MENU.get(), FarmControllerScreen::new);
        event.register(FarmingRegistrator.FEEDING_TROUGH_MENU.get(), FeedingTroughScreen::new);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(FarmingRegistrator.VINE_LEAVES_BLOCK_ENTITY.get(), FencedCropBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(FarmingRegistrator.TRELLIS_LEAVES_BLOCK_ENTITY.get(), FencedCropBlockEntityRenderer::new);

        FarmingRegistrator.FISHIES.forEach(fishConfig -> {
            if (fishConfig.entitySupplier() != null) {
                var entityType = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, fishConfig.name()));
                if (entityType != null) {
                    event.registerEntityRenderer(entityType, fishConfig.entityRenderer());
                }
            }
        });
    }
}
