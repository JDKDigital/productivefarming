package cy.jdkdigital.productivefarming.event;

import cy.jdkdigital.productivefarming.Config;
import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.common.block.DoubleCropBlock;
import cy.jdkdigital.productivefarming.integrations.productivebees.CompatHandler;
import cy.jdkdigital.productivefarming.registry.FarmingDataComponents;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import cy.jdkdigital.productivefarming.registry.ModTags;
import cy.jdkdigital.productivefarming.util.FarmUtil;
import cy.jdkdigital.productivefarming.util.TraitsHelper;
import cy.jdkdigital.productivelib.event.BeeReleaseEvent;
import cy.jdkdigital.productivelib.event.CollectValidUpgradesEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.event.level.block.CropGrowEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;

@EventBusSubscriber(modid = ProductiveFarming.MODID)
public class EventHandler
{
    @SubscribeEvent
    static void itemUseEvent(UseItemOnBlockEvent event) {
        // Shrooms growing on composter
        if (event.getLevel() instanceof ServerLevel serverLevel && serverLevel.getBlockState(event.getPos()).is(Blocks.COMPOSTER)) {
            if (event.getUsePhase().equals(UseItemOnBlockEvent.UsePhase.BLOCK) && event.getItemStack().is(ModTags.Items.MUSHROOMS) && serverLevel.random.nextFloat() < 0.2f) {
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
    public static void onPlayerInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getTarget() instanceof Wolf wolf && !wolf.level().isClientSide) {
            if (wolf.isOwnedBy(event.getEntity()) && wolf.getBodyArmorItem().isEmpty() && !wolf.isBaby()) {
                wolf.setBodyArmorItem(event.getItemStack().copyWithCount(1));
                event.getItemStack().consume(1, event.getEntity());
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));
            } else if (event.getItemStack().canPerformAction(net.neoforged.neoforge.common.ItemAbilities.SHEARS_REMOVE_ARMOR)
                    && wolf.isOwnedBy(event.getEntity())
                    && wolf.hasArmor()
                    && (!EnchantmentHelper.has(wolf.getBodyArmorItem(), EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE) || event.getEntity().isCreative())) {
                event.getItemStack().hurtAndBreak(1, event.getEntity(), event.getItemStack().getEquipmentSlot());
                wolf.playSound(SoundEvents.ARMOR_UNEQUIP_WOLF);
                ItemStack itemstack1 = wolf.getBodyArmorItem();
                wolf.setBodyArmorItem(ItemStack.EMPTY);
                wolf.spawnAtLocation(itemstack1);
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));
            }
        }
    }

    @SubscribeEvent
    public static void collectValidUpgrades(CollectValidUpgradesEvent event) {
        if (ModList.get().isLoaded("productivebees")) {
            CompatHandler.collectValidUpgrades(event);
        }
    }

    @SubscribeEvent
    public static void beeRelease(BeeReleaseEvent event) {
        if (ModList.get().isLoaded("productivebees")) {
            CompatHandler.beeRelease(event);
        } else if (event.getLevel() instanceof ServerLevel level && event.getBeeState().equals(BeehiveBlockEntity.BeeReleaseStatus.HONEY_DELIVERED) && event.getBlockEntity() instanceof BeehiveBlockEntity && event.getBee().getHivePos() != null) {
            FarmUtil.pollinateCrops(level, event.getBee().getHivePos(), 4, false, new ArrayList<>());
        }
    }

    @SubscribeEvent
    static void onCropGrow(CropGrowEvent.Post event) {
        // Make double crops sync growth
        if (event.getLevel() instanceof ServerLevel serverLevel && event.getState().getBlock() instanceof DoubleCropBlock crop) {
            var aboveState = serverLevel.getBlockState(event.getPos().above());
            if (aboveState.is(crop)) {
                serverLevel.setBlockAndUpdate(event.getPos().above(), aboveState.setValue(crop.getAgeProperty(), event.getState().getValue(crop.getAgeProperty())));
            }
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
    static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (Config.SERVER.traitsOnVanillaCrops.get() && event.getEntity() instanceof ItemEntity itemEntity) {
            var stack = itemEntity.getItem();
            if (stack.is(ModTags.Items.VANILLA_SEEDS) && !stack.has(FarmingDataComponents.GROWTH)) {
                TraitsHelper.setDefaultsOnItem(stack);
            }
        }
    }

    @SubscribeEvent
    static void itemTooltip(ItemTooltipEvent event) {
        if (Config.SERVER.traitsOnVanillaCrops.get() && event.getItemStack().is(ModTags.Items.VANILLA_SEEDS) && event.getItemStack().has(FarmingDataComponents.GROWTH)) {
            event.getToolTip().add(Component.translatable(FarmUtil.getLatinTranslationKey(event.getItemStack().getItem())).withStyle(ChatFormatting.DARK_GREEN).withStyle(ChatFormatting.ITALIC));
            if (event.getFlags().hasShiftDown()) {
                event.getToolTip().add(Component.translatable(ProductiveFarming.MODID + ".trait." + TraitsHelper.GROWTH, TraitsHelper.getValueName(TraitsHelper.GROWTH, event.getItemStack().getOrDefault(FarmingDataComponents.GROWTH, 0))).withStyle(ChatFormatting.GRAY));
                event.getToolTip().add(Component.translatable(ProductiveFarming.MODID + ".trait." + TraitsHelper.YIELD, TraitsHelper.getValueName(TraitsHelper.YIELD, event.getItemStack().getOrDefault(FarmingDataComponents.YIELD, 0))).withStyle(ChatFormatting.GRAY));
                event.getToolTip().add(Component.translatable(ProductiveFarming.MODID + ".trait." + TraitsHelper.RESISTANCE, TraitsHelper.getValueName(TraitsHelper.RESISTANCE, event.getItemStack().getOrDefault(FarmingDataComponents.RESISTANCE, 0))).withStyle(ChatFormatting.GRAY));
                event.getToolTip().add(Component.translatable(ProductiveFarming.MODID + ".trait." + TraitsHelper.MUTABILITY, TraitsHelper.getValueName(TraitsHelper.MUTABILITY, event.getItemStack().getOrDefault(FarmingDataComponents.MUTABILITY, 0))).withStyle(ChatFormatting.GRAY));
            } else {
                event.getToolTip().add(Component.translatable(ProductiveFarming.MODID + ".tooltip.extend").withStyle(ChatFormatting.DARK_GRAY));
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