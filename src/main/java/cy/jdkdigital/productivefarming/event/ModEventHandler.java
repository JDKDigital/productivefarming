package cy.jdkdigital.productivefarming.event;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = ProductiveFarming.MODID)
public class ModEventHandler
{
    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
    }

    @SubscribeEvent
    public static void buildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(FarmingRegistrator.TAB_KEY)) {
            for (DeferredHolder<Item, ? extends Item> item : ProductiveFarming.ITEMS.getEntries()) {
                event.accept(item.value());
            }
        }
    }

    @SubscribeEvent
    public static void onEntityAttributeCreate(EntityAttributeCreationEvent event) {
        // Entity attribute assignments
        FarmingRegistrator.FISHIES.forEach(fishConfig -> {
            if (fishConfig.entitySupplier() != null) {
                var entityType = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, fishConfig.name()));
                // TODO different attributes per entity
                event.put((EntityType<? extends LivingEntity>) entityType, Cod.createAttributes().build());
            }
        });
    }

    @SubscribeEvent
    public static void registerBlockEntityCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                FarmingRegistrator.FEEDING_TROUGH_BLOCK_ENTITY.get(),
                (myBlockEntity, side) -> myBlockEntity.getItemHandler()
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                FarmingRegistrator.FARM_CONTROLLER_BLOCK_ENTITY.get(),
                (myBlockEntity, side) -> myBlockEntity.inventoryHandler
        );
//        event.registerBlockEntity(
//                Capabilities.ItemHandler.BLOCK,
//                FarmingRegistrator.FARM_HATCH_BLOCK_ENTITY.get(),
//                (myBlockEntity, side) -> myBlockEntity.inventoryHandler
//        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                FarmingRegistrator.FISH_TRAP_BLOCK_ENTITY.get(),
                (myBlockEntity, side) -> myBlockEntity.inventoryHandler
        );
    }
}
