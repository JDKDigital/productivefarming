package cy.jdkdigital.productivefarming.event;

import cy.jdkdigital.productivefarming.Config;
import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.common.block.DoubleCropBlock;
import cy.jdkdigital.productivefarming.common.block.entity.ColorfulFlowerBlockEntity;
import cy.jdkdigital.productivefarming.common.block.entity.MushroomGrowthCropBlockEntity;
import cy.jdkdigital.productivefarming.integrations.productivebees.CompatHandler;
import cy.jdkdigital.productivefarming.registry.FarmingDataComponents;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import cy.jdkdigital.productivefarming.registry.ModTags;
import cy.jdkdigital.productivefarming.util.FarmUtil;
import cy.jdkdigital.productivefarming.util.FlowerConfig;
import cy.jdkdigital.productivefarming.util.RecipeHelper;
import cy.jdkdigital.productivefarming.util.TraitsHelper;
import cy.jdkdigital.productivelib.event.BeeReleaseEvent;
import cy.jdkdigital.productivelib.event.CollectValidUpgradesEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.event.level.block.CropGrowEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EventBusSubscriber(modid = ProductiveFarming.MODID)
public class EventHandler
{
    @SubscribeEvent
    static void onServerStart(ServerAboutToStartEvent event) {
        Registry<StructureProcessorList> processorListRegistry = event.getServer().registryAccess().registry(Registries.PROCESSOR_LIST).orElseThrow();

        // Replace vanilla crops with modded replacements
        if (Config.SERVER.traitsOnVanillaCrops.get()) {
            StructureProcessor wheatCropProcessor = new RuleProcessor(List.of(new ProcessorRule(
                    // We replace the vanilla Wheat block with Sweet Berry Bush 50% of the time.
                    // Note, Potatoes and Beetroot will also be avaliable to be replaced too by our processor.
                    new RandomBlockMatchTest(Blocks.WHEAT, 1.0F),
                    // Location predicate. Keep this as always true for most use-cases.
                    AlwaysTrueTest.INSTANCE,
                    // The modded block to use.
                    BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "wheat")).defaultBlockState()
            )));
            StructureProcessor potatoCropProcessor = new RuleProcessor(List.of(new ProcessorRule(
                    new RandomBlockMatchTest(Blocks.POTATOES, 1.0F),
                    AlwaysTrueTest.INSTANCE,
                    BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "potato")).defaultBlockState()
            )));
            StructureProcessor carrotCropProcessor = new RuleProcessor(List.of(new ProcessorRule(
                    new RandomBlockMatchTest(Blocks.CARROTS, 1.0F),
                    AlwaysTrueTest.INSTANCE,
                    BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "carrot")).defaultBlockState()
            )));
            StructureProcessor beetrootCropProcessor = new RuleProcessor(List.of(new ProcessorRule(
                    new RandomBlockMatchTest(Blocks.BEETROOTS, 1.0F),
                    AlwaysTrueTest.INSTANCE,
                    BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "beetroot")).defaultBlockState()
            )));

            addNewRuleToVanillaFarms(wheatCropProcessor, processorListRegistry);
            addNewRuleToVanillaFarms(potatoCropProcessor, processorListRegistry);
            addNewRuleToVanillaFarms(carrotCropProcessor, processorListRegistry);
            addNewRuleToVanillaFarms(beetrootCropProcessor, processorListRegistry);
        }

        // Replace farm crops with random modded crop
        List<ProcessorRule> plainsModdedCropRules = new ArrayList<>();
        List<ProcessorRule> savannaModdedCropRules = new ArrayList<>();
        List<ProcessorRule> snowyModdedCropRules = new ArrayList<>();
        List<ProcessorRule> taigaModdedCropRules = new ArrayList<>();
        List<ProcessorRule> desertModdedCropRules = new ArrayList<>();
        int plainsCount = (int)BuiltInRegistries.BLOCK.getTag(ModTags.Blocks.PLAINS_VILLAGE_FARM_CROPS).stream().count();
        int savannaCount = (int)BuiltInRegistries.BLOCK.getTag(ModTags.Blocks.SAVANNA_VILLAGE_FARM_CROPS).stream().count();
        int snowyCount = (int)BuiltInRegistries.BLOCK.getTag(ModTags.Blocks.SNOWY_VILLAGE_FARM_CROPS).stream().count();
        int taigaCount = (int)BuiltInRegistries.BLOCK.getTag(ModTags.Blocks.TAIGA_VILLAGE_FARM_CROPS).stream().count();
        int desertCount = (int)BuiltInRegistries.BLOCK.getTag(ModTags.Blocks.DESERT_VILLAGE_FARM_CROPS).stream().count();
        FarmingRegistrator.CROPS.forEach(cropConfig -> {
            var crop = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, cropConfig.name())).defaultBlockState();
            if (crop.is(ModTags.Blocks.PLAINS_VILLAGE_FARM_CROPS)) {
                plainsModdedCropRules.add(new ProcessorRule(
                        new RandomBlockMatchTest(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "wheat")), 35f / plainsCount / 100f),
                        AlwaysTrueTest.INSTANCE,
                        crop
                ));
            }
            if (crop.is(ModTags.Blocks.SAVANNA_VILLAGE_FARM_CROPS)) {
                savannaModdedCropRules.add(new ProcessorRule(
                        new RandomBlockMatchTest(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "wheat")), 35f / savannaCount / 100f),
                        AlwaysTrueTest.INSTANCE,
                        crop
                ));
            }
            if (crop.is(ModTags.Blocks.SNOWY_VILLAGE_FARM_CROPS)) {
                snowyModdedCropRules.add(new ProcessorRule(
                        new RandomBlockMatchTest(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "wheat")), 35f / snowyCount / 100f),
                        AlwaysTrueTest.INSTANCE,
                        crop
                ));
            }
            if (crop.is(ModTags.Blocks.TAIGA_VILLAGE_FARM_CROPS)) {
                taigaModdedCropRules.add(new ProcessorRule(
                        new RandomBlockMatchTest(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "wheat")), 35f / taigaCount / 100f),
                        AlwaysTrueTest.INSTANCE,
                        crop
                ));
            }
            if (crop.is(ModTags.Blocks.DESERT_VILLAGE_FARM_CROPS)) {
                desertModdedCropRules.add(new ProcessorRule(
                        new RandomBlockMatchTest(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "wheat")), 35f / desertCount / 100f),
                        AlwaysTrueTest.INSTANCE,
                        crop
                ));
            }
        });
        addNewRuleToProcessorList(ResourceLocation.withDefaultNamespace("farm_plains"), new RuleProcessor(plainsModdedCropRules), processorListRegistry);
        addNewRuleToProcessorList(ResourceLocation.withDefaultNamespace("farm_savanna"), new RuleProcessor(savannaModdedCropRules), processorListRegistry);
        addNewRuleToProcessorList(ResourceLocation.withDefaultNamespace("farm_snowy"), new RuleProcessor(snowyModdedCropRules), processorListRegistry);
        addNewRuleToProcessorList(ResourceLocation.withDefaultNamespace("farm_taiga"), new RuleProcessor(taigaModdedCropRules), processorListRegistry);
        addNewRuleToProcessorList(ResourceLocation.withDefaultNamespace("farm_desert"), new RuleProcessor(desertModdedCropRules), processorListRegistry);
        addNewRuleToProcessorList(ResourceLocation.withDefaultNamespace("zombie_plains"), new RuleProcessor(plainsModdedCropRules), processorListRegistry);
        addNewRuleToProcessorList(ResourceLocation.withDefaultNamespace("zombie_savanna"), new RuleProcessor(savannaModdedCropRules), processorListRegistry);
        addNewRuleToProcessorList(ResourceLocation.withDefaultNamespace("zombie_snowy"), new RuleProcessor(snowyModdedCropRules), processorListRegistry);
        addNewRuleToProcessorList(ResourceLocation.withDefaultNamespace("zombie_taiga"), new RuleProcessor(taigaModdedCropRules), processorListRegistry);
        addNewRuleToProcessorList(ResourceLocation.withDefaultNamespace("zombie_desert"), new RuleProcessor(desertModdedCropRules), processorListRegistry);
    }
    private static void addNewRuleToVanillaFarms(StructureProcessor processorToAdd, Registry<StructureProcessorList> processorListRegistry) {
        addNewRuleToProcessorList(ResourceLocation.withDefaultNamespace("farm_plains"), processorToAdd, processorListRegistry);
        addNewRuleToProcessorList(ResourceLocation.withDefaultNamespace("farm_savanna"), processorToAdd, processorListRegistry);
        addNewRuleToProcessorList(ResourceLocation.withDefaultNamespace("farm_snowy"), processorToAdd, processorListRegistry);
        addNewRuleToProcessorList(ResourceLocation.withDefaultNamespace("farm_taiga"), processorToAdd, processorListRegistry);
        addNewRuleToProcessorList(ResourceLocation.withDefaultNamespace("farm_desert"), processorToAdd, processorListRegistry);
        addNewRuleToProcessorList(ResourceLocation.withDefaultNamespace("zombie_plains"), processorToAdd, processorListRegistry);
        addNewRuleToProcessorList(ResourceLocation.withDefaultNamespace("zombie_savanna"), processorToAdd, processorListRegistry);
        addNewRuleToProcessorList(ResourceLocation.withDefaultNamespace("zombie_snowy"), processorToAdd, processorListRegistry);
        addNewRuleToProcessorList(ResourceLocation.withDefaultNamespace("zombie_taiga"), processorToAdd, processorListRegistry);
        addNewRuleToProcessorList(ResourceLocation.withDefaultNamespace("zombie_desert"), processorToAdd, processorListRegistry);
    }
    private static void addNewRuleToProcessorList(ResourceLocation targetProcessorList, StructureProcessor processorToAdd, Registry<StructureProcessorList> processorListRegistry) {
        processorListRegistry.getOptional(targetProcessorList)
                .ifPresent(processorList -> {
                    List<StructureProcessor> newSafeList = new ArrayList<>(processorList.list());
                    newSafeList.add(processorToAdd);
                    processorList.list = newSafeList;
                });
    }

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
                            if (serverLevel.getBlockEntity(event.getPos().relative(direction)) instanceof MushroomGrowthCropBlockEntity growthCropBlockEntity) {
                                growthCropBlockEntity.applyComponentsFromItemStack(event.getItemStack());
                            }
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
    public static void onBonemeal(BonemealEvent event) {
        if (Config.SERVER.spawnFlowersWithBonemeal.get() && !event.getLevel().isClientSide() && event.getState().is(Blocks.GRASS_BLOCK) && event.getLevel().getBiome(event.getPos()).is(ModTags.Biomes.HAS_FLOWERS)) {
            var positions = BlockPos.betweenClosedStream(
                        event.getPos().below(2).west(3).south(3),
                        event.getPos().above(2).east(3).north(3)
                    )
                    .map(BlockPos::immutable)
                    .filter(pos -> event.getLevel().getBlockState(pos).isAir() && event.getLevel().getBlockState(pos.below()).is(Blocks.GRASS_BLOCK))
                    .collect(Collectors.toCollection(ArrayList::new));

            for (BlockPos position : positions) {
                if (event.getLevel().random.nextInt(8) == 0) {
                    FlowerConfig flower = FarmingRegistrator.FLOWERS.get(event.getLevel().random.nextInt(FarmingRegistrator.FLOWERS.size()));
                    BlockState newFlower = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, flower.name())).defaultBlockState();
                    if (newFlower.is(ModTags.Blocks.CAN_SPAWN_FROM_BONEMEAL)) {
                        event.getLevel().setBlockAndUpdate(position, newFlower);
                        // for double flower
                        if (newFlower.is(BlockTags.TALL_FLOWERS) && event.getLevel().getBlockEntity(position) instanceof ColorfulFlowerBlockEntity flowerBlockEntity) {
                            event.getLevel().setBlockAndUpdate(position.above(), newFlower.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER));
                            if (event.getLevel().getBlockEntity(position.above()) instanceof ColorfulFlowerBlockEntity flowerBlockEntityAbove) {
                                flowerBlockEntityAbove.setColor(flowerBlockEntity.getColor());
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onVillagerTradesEvent(VillagerTradesEvent event) {
        if (Config.SERVER_CONFIG.isLoaded() && Config.SERVER.villagersTradeSeeds.get() && event.getType().equals(VillagerProfession.FARMER)) {
            FarmingRegistrator.CROPS.forEach(cropConfig -> {
                if (!RecipeHelper.isMutatedCrop(cropConfig)) {
                    var seed = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, cropConfig.name() + (cropConfig.hasSeed() ? "_seeds" : "")));
                    if (!seed.getDefaultInstance().isEmpty()) {
                        event.getTrades().get(1).add((trader, rand) -> new MerchantOffer(new ItemCost(Items.EMERALD), Optional.empty(), new ItemStack(seed, (int) (1 + Math.random() * 6)), 1, 16, 1, 0.2F));
                    }
                }
            });
            FarmingRegistrator.HERBS.forEach(cropConfig -> {
                if (!RecipeHelper.isMutatedCrop(cropConfig)) {
                    var seed = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, cropConfig.name() + (cropConfig.hasSeed() ? "_seeds" : "")));
                    if (!seed.getDefaultInstance().isEmpty()) {
                        event.getTrades().get(2).add((trader, rand) -> new MerchantOffer(new ItemCost(Items.EMERALD), Optional.empty(), new ItemStack(seed, (int) (1 + Math.random() * 8)), 1, 16, 3, 0.2F));
                    }
                }
            });
            FarmingRegistrator.BERRIES.forEach(cropConfig -> {
                if (!RecipeHelper.isMutatedCrop(cropConfig)) {
                    var seed = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, cropConfig.name() + (cropConfig.hasSeed() ? "_seeds" : "")));
                    if (!seed.getDefaultInstance().isEmpty()) {
                        event.getTrades().get(2).add((trader, rand) -> new MerchantOffer(new ItemCost(Items.EMERALD), Optional.empty(), new ItemStack(seed, (int) (1 + Math.random() * 8)), 1, 16, 4, 0.2F));
                    }
                }
            });
            FarmingRegistrator.GRAPES.forEach(cropConfig -> {
                if (!RecipeHelper.isMutatedCrop(cropConfig)) {
                    var seed = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, cropConfig.name() + (cropConfig.hasSeed() ? "_seeds" : "")));
                    if (!seed.getDefaultInstance().isEmpty()) {
                        event.getTrades().get(3).add((trader, rand) -> new MerchantOffer(new ItemCost(Items.EMERALD, 2), Optional.empty(), new ItemStack(seed, (int) (1 + Math.random() * 4)), 1, 16, 4, 0.2F));
                    }
                }
            });
            FarmingRegistrator.STEMS.forEach(cropConfig -> {
                if (!RecipeHelper.isMutatedCrop(cropConfig)) {
                    var seed = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, cropConfig.name() + (cropConfig.hasSeed() ? "_seeds" : "")));
                    if (!seed.getDefaultInstance().isEmpty()) {
                        event.getTrades().get(3).add((trader, rand) -> new MerchantOffer(new ItemCost(Items.EMERALD, 2), Optional.empty(), new ItemStack(seed, (int) (1 + Math.random() * 4)), 1, 16, 4, 0.2F));
                    }
                }
            });
            FarmingRegistrator.TRELLIS.forEach(cropConfig -> {
                if (!RecipeHelper.isMutatedCrop(cropConfig)) {
                    var seed = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, cropConfig.name() + (cropConfig.hasSeed() ? "_seeds" : "")));
                    if (!seed.getDefaultInstance().isEmpty()) {
                        event.getTrades().get(4).add((trader, rand) -> new MerchantOffer(new ItemCost(Items.EMERALD, 2), Optional.empty(), new ItemStack(seed, (int) (1 + Math.random() * 4)), 1, 16, 5, 0.2F));
                    }
                }
            });
            FarmingRegistrator.VERTICAL_TRELLIS.forEach(cropConfig -> {
                if (!RecipeHelper.isMutatedCrop(cropConfig)) {
                    var seed = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, cropConfig.name() + (cropConfig.hasSeed() ? "_seeds" : "")));
                    if (!seed.getDefaultInstance().isEmpty()) {
                        event.getTrades().get(4).add((trader, rand) -> new MerchantOffer(new ItemCost(Items.EMERALD, 2), Optional.empty(), new ItemStack(seed, (int) (1 + Math.random() * 4)), 1, 16, 5, 0.2F));
                    }
                }
            });
        }
    }
}