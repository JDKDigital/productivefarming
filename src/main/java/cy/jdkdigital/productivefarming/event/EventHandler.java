package cy.jdkdigital.productivefarming.event;

import cy.jdkdigital.productivefarming.Config;
import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.common.block.DoubleCropBlock;
import cy.jdkdigital.productivefarming.common.block.IColorfulFlowerBlock;
import cy.jdkdigital.productivefarming.common.block.entity.ColorfulFlowerBlockEntity;
import cy.jdkdigital.productivefarming.common.block.entity.MushroomGrowthCropBlockEntity;
import cy.jdkdigital.productivefarming.common.item.CropBlockItem;
import cy.jdkdigital.productivefarming.integrations.productivebees.CompatHandler;
import cy.jdkdigital.productivefarming.registry.FarmingDataComponents;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import cy.jdkdigital.productivefarming.registry.ModTags;
import cy.jdkdigital.productivefarming.util.FarmUtil;
import cy.jdkdigital.productivefarming.util.FlowerConfig;
import cy.jdkdigital.productivefarming.util.TraitsHelper;
import cy.jdkdigital.productivelib.event.BeeReleaseEvent;
import cy.jdkdigital.productivelib.event.CollectValidUpgradesEvent;
import cy.jdkdigital.productivelib.event.UpgradeTooltipEvent;
import cy.jdkdigital.productivelib.registry.LibItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.animal.fish.Cod;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TallFlowerBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.event.level.block.CropGrowEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EventBusSubscriber(modid = ProductiveFarming.MODID)
public class EventHandler
{
    @SubscribeEvent
    public static void buildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(FarmingRegistrator.TAB_KEY)) {
            for (DeferredHolder<Item, ? extends Item> item : ProductiveFarming.ITEMS.getEntries()) {
                if (item.is(FarmingRegistrator.FARM_HATCH.getId())) continue;

                if (item.is(ItemTags.FLOWERS) && item.get() instanceof BlockItem blockItem && blockItem.getBlock() instanceof IColorfulFlowerBlock colorfulFlowerBlock) {
                    var stack = item.get().getDefaultInstance();
                    stack.set(FarmingDataComponents.COLOR, colorfulFlowerBlock.getDefaultColor());
                    event.accept(stack);
                } else {
                    event.accept(item.value());
                }
            }
            if (ModList.get().isLoaded("productivebees")) {
                event.accept(LibItems.UPGRADE_POLLEN_SIEVE.get());
            }
        }
    }

    @SubscribeEvent
    public static void onEntityAttributeCreate(EntityAttributeCreationEvent event) {
        // Entity attribute assignments
        FarmingRegistrator.FISHIES.forEach(fishConfig -> {
            if (fishConfig.entitySupplier() != null) {
                var entityType = BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, fishConfig.name()));
                // TODO different attributes per entity
                event.put((EntityType<? extends LivingEntity>) entityType, Cod.createAttributes().build());
            }
        });
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                FarmingRegistrator.FEEDING_TROUGH_BLOCK_ENTITY.get(),
                (myBlockEntity, side) -> myBlockEntity.getItemHandler()
        );
        event.registerBlockEntity(
                Capabilities.Fluid.BLOCK,
                FarmingRegistrator.WATERING_TROUGH_BLOCK_ENTITY.get(),
                (myBlockEntity, side) -> myBlockEntity.getFluidHandler()
        );
        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                FarmingRegistrator.FARM_CONTROLLER_BLOCK_ENTITY.get(),
                (myBlockEntity, side) -> myBlockEntity.getItemHandler()
        );
        event.registerBlockEntity(
                Capabilities.Fluid.BLOCK,
                FarmingRegistrator.FARM_CONTROLLER_BLOCK_ENTITY.get(),
                (myBlockEntity, side) -> myBlockEntity.getFluidHandler()
        );
    }

    @SubscribeEvent
    private static void registerDataMap(final RegisterDataMapTypesEvent event) {
        event.register(FarmingRegistrator.CROP_TRAITS);
        event.register(FarmingRegistrator.STAT_INCREASE_CHANCE);
    }

    @SubscribeEvent
    static void onServerStart(ServerAboutToStartEvent event) {
        Registry<StructureProcessorList> processorListRegistry = event.getServer().registryAccess().lookup(Registries.PROCESSOR_LIST).orElseThrow();

        List<ProcessorRule> plainsModdedCropRules = new ArrayList<>();
        List<ProcessorRule> savannaModdedCropRules = new ArrayList<>();
        List<ProcessorRule> snowyModdedCropRules = new ArrayList<>();
        List<ProcessorRule> taigaModdedCropRules = new ArrayList<>();
        List<ProcessorRule> desertModdedCropRules = new ArrayList<>();
        int plainsCount = (int)BuiltInRegistries.BLOCK.get(ModTags.Blocks.PLAINS_VILLAGE_FARM_CROPS).stream().count();
        int savannaCount = (int)BuiltInRegistries.BLOCK.get(ModTags.Blocks.SAVANNA_VILLAGE_FARM_CROPS).stream().count();
        int snowyCount = (int)BuiltInRegistries.BLOCK.get(ModTags.Blocks.SNOWY_VILLAGE_FARM_CROPS).stream().count();
        int taigaCount = (int)BuiltInRegistries.BLOCK.get(ModTags.Blocks.TAIGA_VILLAGE_FARM_CROPS).stream().count();
        int desertCount = (int)BuiltInRegistries.BLOCK.get(ModTags.Blocks.DESERT_VILLAGE_FARM_CROPS).stream().count();
        FarmingRegistrator.CROPS.forEach(cropConfig -> {
            var crop = BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, cropConfig.name())).defaultBlockState();
            if (crop.is(ModTags.Blocks.PLAINS_VILLAGE_FARM_CROPS)) {
                plainsModdedCropRules.add(new ProcessorRule(
                        new RandomBlockMatchTest(Blocks.WHEAT, 35f / plainsCount / 100f),
                        AlwaysTrueTest.INSTANCE,
                        crop
                ));
            }
            if (crop.is(ModTags.Blocks.SAVANNA_VILLAGE_FARM_CROPS)) {
                savannaModdedCropRules.add(new ProcessorRule(
                        new RandomBlockMatchTest(Blocks.WHEAT, 35f / savannaCount / 100f),
                        AlwaysTrueTest.INSTANCE,
                        crop
                ));
            }
            if (crop.is(ModTags.Blocks.SNOWY_VILLAGE_FARM_CROPS)) {
                snowyModdedCropRules.add(new ProcessorRule(
                        new RandomBlockMatchTest(Blocks.WHEAT, 35f / snowyCount / 100f),
                        AlwaysTrueTest.INSTANCE,
                        crop
                ));
            }
            if (crop.is(ModTags.Blocks.TAIGA_VILLAGE_FARM_CROPS)) {
                taigaModdedCropRules.add(new ProcessorRule(
                        new RandomBlockMatchTest(Blocks.WHEAT, 35f / taigaCount / 100f),
                        AlwaysTrueTest.INSTANCE,
                        crop
                ));
            }
            if (crop.is(ModTags.Blocks.DESERT_VILLAGE_FARM_CROPS)) {
                desertModdedCropRules.add(new ProcessorRule(
                        new RandomBlockMatchTest(Blocks.WHEAT, 35f / desertCount / 100f),
                        AlwaysTrueTest.INSTANCE,
                        crop
                ));
            }
        });
        addNewRuleToProcessorList(Identifier.withDefaultNamespace("farm_plains"), new RuleProcessor(plainsModdedCropRules), processorListRegistry);
        addNewRuleToProcessorList(Identifier.withDefaultNamespace("farm_savanna"), new RuleProcessor(savannaModdedCropRules), processorListRegistry);
        addNewRuleToProcessorList(Identifier.withDefaultNamespace("farm_snowy"), new RuleProcessor(snowyModdedCropRules), processorListRegistry);
        addNewRuleToProcessorList(Identifier.withDefaultNamespace("farm_taiga"), new RuleProcessor(taigaModdedCropRules), processorListRegistry);
        addNewRuleToProcessorList(Identifier.withDefaultNamespace("farm_desert"), new RuleProcessor(desertModdedCropRules), processorListRegistry);
        addNewRuleToProcessorList(Identifier.withDefaultNamespace("zombie_plains"), new RuleProcessor(plainsModdedCropRules), processorListRegistry);
        addNewRuleToProcessorList(Identifier.withDefaultNamespace("zombie_savanna"), new RuleProcessor(savannaModdedCropRules), processorListRegistry);
        addNewRuleToProcessorList(Identifier.withDefaultNamespace("zombie_snowy"), new RuleProcessor(snowyModdedCropRules), processorListRegistry);
        addNewRuleToProcessorList(Identifier.withDefaultNamespace("zombie_taiga"), new RuleProcessor(taigaModdedCropRules), processorListRegistry);
        addNewRuleToProcessorList(Identifier.withDefaultNamespace("zombie_desert"), new RuleProcessor(desertModdedCropRules), processorListRegistry);
    }
    private static void addNewRuleToProcessorList(Identifier targetProcessorList, StructureProcessor processorToAdd, Registry<StructureProcessorList> processorListRegistry) {
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
            if (event.getUsePhase().equals(UseItemOnBlockEvent.UsePhase.BLOCK) && event.getItemStack().is(ModTags.Items.MUSHROOMS) && serverLevel.getRandom().nextFloat() < 0.2f) {
                var blockName = Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, BuiltInRegistries.ITEM.getKey(event.getItemStack().getItem()).getPath() + "_growth");
                var growth = BuiltInRegistries.BLOCK.getValue(blockName).defaultBlockState();
                if (growth.hasProperty(BlockStateProperties.FACING)) {
                    // find free spot on the composter side to plant shrooms
                    for (Direction direction : Direction.Plane.HORIZONTAL.shuffledCopy(serverLevel.getRandom())) {
                        if (serverLevel.getBlockState(event.getPos().relative(direction)).isAir()) {
                            serverLevel.setBlockAndUpdate(event.getPos().relative(direction), growth.setValue(BlockStateProperties.FACING, direction));
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
    public static void collectValidUpgrades(CollectValidUpgradesEvent event) {
        if (ModList.get().isLoaded("productivebees")) {
            CompatHandler.collectValidUpgrades(event);
        }
    }

    @SubscribeEvent
    public static void addUpgradeTooltip(UpgradeTooltipEvent event) {
        var upgradeType = BuiltInRegistries.ITEM.getKey(event.getStack().getItem());

        String tPrefix = "productivefarming.information.upgrade." + upgradeType.getPath() + ".";
        switch (upgradeType.getPath()) {
            case "upgrade_time", "upgrade_time_2", "upgrade_stability" -> {
                event.addValidBlock(Component.translatable("productivefarming.devices.farm_controller"), tPrefix + "farm_controller");
            }
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
        if (Config.SERVER.statsOnExternalCrops.get() && event.getEntity() instanceof ItemEntity itemEntity) {
            var stack = itemEntity.getItem();
            if (stack.is(ModTags.Items.VANILLA_SEEDS) && !stack.has(FarmingDataComponents.GROWTH)) {
                TraitsHelper.setDefaultsOnStack(stack);
            }
        }
    }

    @SubscribeEvent
    static void itemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        boolean own = stack.getItem() instanceof CropBlockItem;
        boolean external = Config.SERVER.statsOnExternalCrops.get() && stack.is(ModTags.Items.EXTERNAL_SEEDS) && stack.has(FarmingDataComponents.GROWTH);
        if (!own && !external) {
            return;
        }
        if (own || stack.is(ModTags.Items.VANILLA_SEEDS)) {
            event.getToolTip().add(Component.translatable(FarmUtil.getLatinTranslationKey(stack.getItem())).withStyle(ChatFormatting.DARK_GREEN).withStyle(ChatFormatting.ITALIC));
        }
        if (event.getFlags().hasShiftDown()) {
            event.getToolTip().add(Component.translatable(ProductiveFarming.MODID + ".trait." + TraitsHelper.GROWTH, TraitsHelper.getValueName(TraitsHelper.GROWTH, stack.getOrDefault(FarmingDataComponents.GROWTH, 0))).withStyle(ChatFormatting.GRAY));
            event.getToolTip().add(Component.translatable(ProductiveFarming.MODID + ".trait." + TraitsHelper.YIELD, TraitsHelper.getValueName(TraitsHelper.YIELD, stack.getOrDefault(FarmingDataComponents.YIELD, 0))).withStyle(ChatFormatting.GRAY));
            event.getToolTip().add(Component.translatable(ProductiveFarming.MODID + ".trait." + TraitsHelper.RESISTANCE, TraitsHelper.getValueName(TraitsHelper.RESISTANCE, stack.getOrDefault(FarmingDataComponents.RESISTANCE, 0))).withStyle(ChatFormatting.GRAY));
            event.getToolTip().add(Component.translatable(ProductiveFarming.MODID + ".trait." + TraitsHelper.MUTABILITY, TraitsHelper.getValueName(TraitsHelper.MUTABILITY, stack.getOrDefault(FarmingDataComponents.MUTABILITY, 0))).withStyle(ChatFormatting.GRAY));
        } else {
            event.getToolTip().add(Component.translatable(ProductiveFarming.MODID + ".tooltip.extend").withStyle(ChatFormatting.DARK_GRAY));
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
                if (event.getLevel().getRandom().nextInt(8) == 0) {
                    FlowerConfig flower = FarmingRegistrator.FLOWERS.get(event.getLevel().getRandom().nextInt(FarmingRegistrator.FLOWERS.size()));
                    BlockState newFlower = BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, flower.name())).defaultBlockState();
                    if (newFlower.is(ModTags.Blocks.CAN_SPAWN_FROM_BONEMEAL)) {
                        event.getLevel().setBlockAndUpdate(position, newFlower);
                        // for double flower
                        if (newFlower.getBlock() instanceof TallFlowerBlock && event.getLevel().getBlockEntity(position) instanceof ColorfulFlowerBlockEntity flowerBlockEntity) {
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
}