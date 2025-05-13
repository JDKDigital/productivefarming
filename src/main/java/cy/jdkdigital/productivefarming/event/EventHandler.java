package cy.jdkdigital.productivefarming.event;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import cy.jdkdigital.productivefarming.registry.ModTags;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;

import java.util.Optional;
import java.util.stream.Stream;

@EventBusSubscriber(modid = ProductiveFarming.MODID)
public class EventHandler
{
    @SubscribeEvent
    static void itemUseEvent(UseItemOnBlockEvent event) {
        // Shrooms growing on composter
        if (event.getLevel() instanceof ServerLevel serverLevel && serverLevel.getBlockState(event.getPos()).is(Blocks.COMPOSTER)) {
            if (event.getUsePhase().equals(UseItemOnBlockEvent.UsePhase.BLOCK) && event.getItemStack().is(ModTags.MUSHROOMS) && serverLevel.random.nextFloat() < 0.2f) {
                var blockName = ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, BuiltInRegistries.ITEM.getKey(event.getItemStack().getItem()).getPath() + "_growth");
                var growth = BuiltInRegistries.BLOCK.get(blockName).defaultBlockState();
                if (growth.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
                    // find free spot on the composter side to plant shrooms
                    for (Direction direction : Direction.Plane.HORIZONTAL.shuffledCopy(serverLevel.random)) {
                        if (serverLevel.getBlockState(event.getPos().relative(direction)).isAir()) {
                            serverLevel.setBlockAndUpdate(event.getPos().relative(direction), growth.setValue(BlockStateProperties.HORIZONTAL_FACING, direction));
                            break;
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    static void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {

        }
    }

    @SubscribeEvent
    static void onBabySpawn(BabyEntitySpawnEvent event) {
        if (event.getChild() != null && event.getChild().level() instanceof ServerLevel serverLevel && event.getChild() instanceof Horse horse) {
            PoiManager poiManager = serverLevel.getPoiManager();
            Stream<PoiRecord> stream = poiManager.getInRange((poi) -> poi.is(ModTags.SALT_LICK_POI_TAG), event.getParentA().blockPosition(), 2, PoiManager.Occupancy.ANY); // TODO config range
            if (stream.findAny().isPresent()) {
                horse.getAttribute(Attributes.MAX_HEALTH).setBaseValue(horse.getAttribute(Attributes.MAX_HEALTH).getBaseValue() * 1.1);
                horse.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(horse.getAttribute(Attributes.MAX_HEALTH).getBaseValue() * 1.1);
                horse.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(horse.getAttribute(Attributes.MAX_HEALTH).getBaseValue() * 1.1);
            }
        }
    }

    @SubscribeEvent
    public static void onVillagerTradesEvent(VillagerTradesEvent event) {
        if (event.getType().equals(VillagerProfession.FARMER)) {
            FarmingRegistrator.CROPS.forEach(cropConfig -> {
                var seed = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, cropConfig.name() + (cropConfig.hasSeed() ? "_seeds" : "")));
                event.getTrades().get(1).add((trader, rand) -> new MerchantOffer(new ItemCost(Items.EMERALD), Optional.empty(), new ItemStack(seed, (int) (Math.random() * 6)), 1, 16, 1, 0.2F));
            });
            FarmingRegistrator.HERBS.forEach(cropConfig -> {
                var seed = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, cropConfig.name() + (cropConfig.hasSeed() ? "_seeds" : "")));
                event.getTrades().get(2).add((trader, rand) -> new MerchantOffer(new ItemCost(Items.EMERALD), Optional.empty(), new ItemStack(seed, (int) (Math.random() * 8)), 1, 16, 1, 0.2F));
            });
            FarmingRegistrator.BERRIES.forEach(cropConfig -> {
                var seed = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, cropConfig.name() + (cropConfig.hasSeed() ? "_seeds" : "")));
                event.getTrades().get(2).add((trader, rand) -> new MerchantOffer(new ItemCost(Items.EMERALD), Optional.empty(), new ItemStack(seed, (int) (Math.random() * 8)), 1, 16, 1, 0.2F));
            });
            FarmingRegistrator.VINES.forEach(cropConfig -> {
                var seed = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, cropConfig.name() + (cropConfig.hasSeed() ? "_seeds" : "")));
                event.getTrades().get(3).add((trader, rand) -> new MerchantOffer(new ItemCost(Items.EMERALD, 2), Optional.empty(), new ItemStack(seed, (int) (Math.random() * 8)), 1, 16, 1, 0.2F));
            });
            FarmingRegistrator.STEMS.forEach(cropConfig -> {
                var seed = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, cropConfig.name() + (cropConfig.hasSeed() ? "_seeds" : "")));
                event.getTrades().get(3).add((trader, rand) -> new MerchantOffer(new ItemCost(Items.EMERALD, 2), Optional.empty(), new ItemStack(seed, (int) (Math.random() * 8)), 1, 16, 1, 0.2F));
            });
        }
    }
}