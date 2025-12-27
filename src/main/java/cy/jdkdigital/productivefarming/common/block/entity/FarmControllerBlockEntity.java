package cy.jdkdigital.productivefarming.common.block.entity;

import cy.jdkdigital.productivefarming.Config;
import cy.jdkdigital.productivefarming.inventory.FarmControllerContainer;
import cy.jdkdigital.productivefarming.registry.FarmingDataComponents;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import cy.jdkdigital.productivefarming.registry.ModTags;
import cy.jdkdigital.productivelib.common.block.entity.IMultiBlockControllerBlockEntity;
import cy.jdkdigital.productivelib.common.block.entity.IUpgradeableBlockEntity;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivelib.registry.LibItems;
import cy.jdkdigital.productivelib.util.MultiBlockDetector;
import cy.jdkdigital.productivelib.util.harvest.HarvestCompatHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class FarmControllerBlockEntity extends TickingBlockEntity implements IMultiBlockControllerBlockEntity, IUpgradeableBlockEntity, MenuProvider
{
    private MultiBlockDetector.MultiBlockData farmConfig;

    public final IItemHandlerModifiable inventoryHandler = new InventoryHandlerHelper.BlockEntityItemStackHandler(27, this)
    {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return true;
        }

        @Override
        public boolean isInsertableSlot(int slot) {
            return true;
        }

        @Override
        public boolean isInputSlot(int slot) {
            return false;
        }

        @Override
        public int[] getOutputSlots() {
            return new int[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26};
        }
    };
    private final IFluidHandler fluidHandler = new FluidTank(10000, fluidStack -> fluidStack.getFluid().isSame(FarmingRegistrator.NUTRIENT_WATER.get()));

    protected IItemHandlerModifiable upgradeHandler = new InventoryHandlerHelper.UpgradeHandler(4, this, List.of(
            LibItems.UPGRADE_TIME.get(),
            LibItems.UPGRADE_TIME_2.get(),
            LibItems.UPGRADE_STABILITY.get()
    ));

    public FarmControllerBlockEntity(BlockPos pos, BlockState state) {
        super(FarmingRegistrator.FARM_CONTROLLER_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public Component getName() {
        return Component.translatable(this.getBlockState().getBlock().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new FarmControllerContainer(pContainerId, pPlayerInventory, this);
    }

    @Override
    int tickRate() {
        int speedModifier = getUpgradeCount(LibItems.UPGRADE_TIME.get()) + (getUpgradeCount(LibItems.UPGRADE_TIME_2.get()) * 2) + 1;
        return (int)(300f * (1 - Math.min(0.93, speedModifier * Config.SERVER.speedUpgradeModifier.get())));
    }

    @Override
    public void tickServer(ServerLevel level, BlockPos blockPos, BlockState blockState, TickingBlockEntity blockEntity) {
        if (blockEntity instanceof FarmControllerBlockEntity farmControllerBlockEntity) {
            // Only harvest when there's an empty slot in the inventory
            boolean canHarvest = false;
            for (int slot = 0; slot < farmControllerBlockEntity.getItemHandler().getSlots(); slot++) {
                if (farmControllerBlockEntity.getItemHandler().getStackInSlot(slot).isEmpty()) {
                    canHarvest = true;
                }
            }

            if (canHarvest) {
                var farmConfig = farmControllerBlockEntity.farmConfig;
                var cropPositions = BlockPos.betweenClosedStream(farmConfig.topCorners().getFirst(), farmConfig.topCorners().getSecond()).map(BlockPos::above).collect(Collectors.toCollection(ArrayList::new));

                if (farmConfig.height() > 1) {
                    farmControllerBlockEntity.processFishFarm(cropPositions);
                }

                // do crop farming
                // initiate worker for harvesting crops
                processCropFarm(cropPositions);

                // collect items, void excess
                boolean stripStats = getUpgradeCount(LibItems.UPGRADE_STABILITY.get()) > 0;
                List<ItemEntity> lootStacks = level.getEntitiesOfClass(ItemEntity.class, (new AABB(farmConfig.topCorners().getFirst().above(3).getCenter(), farmConfig.topCorners().getSecond().below(farmConfig.height() + 1).getBottomCenter()))).stream().toList();
                lootStacks.forEach(itemEntity -> {
                    if (stripStats && itemEntity.getItem().has(FarmingDataComponents.GROWTH)) {
                        itemEntity.getItem().remove(FarmingDataComponents.GROWTH);
                        itemEntity.getItem().remove(FarmingDataComponents.YIELD);
                        itemEntity.getItem().remove(FarmingDataComponents.RESISTANCE);
                        itemEntity.getItem().remove(FarmingDataComponents.MUTABILITY);
                    }
                    if (inventoryHandler instanceof InventoryHandlerHelper.BlockEntityItemStackHandler handler && handler.addOutput(itemEntity.getItem()).isEmpty()) {
                        itemEntity.kill();
                    }
                });
            }
        }
    }

    public static <E extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState blockState, FarmControllerBlockEntity blockEntity) {
        if (blockEntity.farmConfig != null) {
            blockEntity.tickHandler(level, blockPos, blockState, blockEntity);
        }
    }

    @Override
    public IItemHandler getItemHandler() {
        return inventoryHandler;
    }

    @Override
    public IFluidHandler getFluidHandler() {
        return fluidHandler;
    }

    @Override
    public IItemHandlerModifiable getUpgradeHandler() {
        return upgradeHandler;
    }

    @Override
    public void savePacketNBT(CompoundTag tag, HolderLookup.Provider provider) {
        super.savePacketNBT(tag, provider);

        if (this.getMultiblockData() != null) {
            tag.put("multiData", this.getMultiblockData().serializeNBT(provider));
        }
    }

    @Override
    public void loadPacketNBT(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadPacketNBT(tag, provider);

        if (tag.contains("multiData")) {
            var data = new MultiBlockDetector.MultiBlockData(null, null, List.of(), 0, 0);
            data.deserializeNBT(provider, Objects.requireNonNull(tag.get("multiData")));
            setMultiBlockData(data);
        }
    }

    private void processFishFarm(List<BlockPos> cropPositions) {
        if (level instanceof ServerLevel serverLevel) {
            List<LivingEntity> entities = serverLevel.getEntitiesOfClass(LivingEntity.class, (new AABB(farmConfig.topCorners().getFirst().below(farmConfig.height()).getCenter(), farmConfig.topCorners().getSecond().getCenter()))).stream().filter(e -> e.getType().is(ModTags.FISH_FARM_ENTITIES) || e instanceof AbstractFish).toList();
            if (entities.size() > 1) {
                // Entity count map
                Map<EntityType<?>, Integer> entityCount = new HashMap<>();
                entities.forEach(livingEntity -> {
                    var type = livingEntity.getType();
                    if (entityCount.containsKey(type)) {
                        entityCount.put(type, entityCount.get(type) + 1);
                    } else {
                        entityCount.put(type, 1);
                    }
                });
                List<Map.Entry<EntityType<?>, Integer>> list = new LinkedList<>(entityCount.entrySet());
                list.sort(Comparator.comparingInt(Map.Entry::getValue));

                int maxAllowedEntities = cropPositions.size() / 2;
                if (maxAllowedEntities < entities.size()) {
                    // kill excess
                    AtomicInteger toKill = new AtomicInteger(entities.size() - maxAllowedEntities);
                    entities.forEach(entity -> {
                        if (toKill.getAndDecrement() >= 0 && entityCount.get(entity.getType()) > 2) {
                            entity.kill();
                            entityCount.put(entity.getType(), entityCount.get(entity.getType()) - 1);
                        }
                    });
                }

                BlockPos middle = new BlockPos((farmConfig.topCorners().getFirst().getX() + farmConfig.topCorners().getSecond().getX()) / 2, farmConfig.topCorners().getFirst().getY() - 1, (farmConfig.topCorners().getFirst().getZ() + farmConfig.topCorners().getSecond().getZ()) / 2);
                List<EntityType<?>> bredSpecies = new ArrayList<>();
                entities.forEach(livingEntity -> {
                    // breed
                    var count = entityCount.get(livingEntity.getType());
                    if (count >= 2 && !bredSpecies.contains(livingEntity.getType())) {
                        bredSpecies.add(livingEntity.getType());
                        var newBreeds = serverLevel.random.nextInt(count / 2);
                        for (int i = 0; i < newBreeds; i++) {
                            if (serverLevel.random.nextBoolean()) {
                                if (livingEntity instanceof Animal animal) {
                                    var offSpring = animal.getBreedOffspring(serverLevel, animal);
                                    if (offSpring != null) {
                                        offSpring.setPos(middle.getX(), middle.getY(), middle.getZ());
                                        serverLevel.addFreshEntity(offSpring);
                                    }
                                } else {
                                    livingEntity.getType().spawn(serverLevel, middle.relative(Direction.fromYRot(serverLevel.random.nextInt(360))), MobSpawnType.BREEDING);
                                }
                            }
                        }
                    }
                });
            }

            // Propagate clams and oysters
            Map<BlockPos, BlockState> clamMap = new HashMap<>();
            for (var pos : BlockPos.betweenClosed(farmConfig.topCorners().getFirst(), farmConfig.topCorners().getSecond())) {
                var state = serverLevel.getBlockState(pos);
                if (state.is(ModTags.Blocks.FARMABLE_FISH_BLOCKS)) {
                    clamMap.put(new BlockPos(pos), state);
                }
            }

            float clamChance = (float) Config.SERVER.clamSpreadChance.getAsDouble();

            List<Direction> directions = Arrays.asList(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);
            for (Map.Entry<BlockPos, BlockState> clamPos : clamMap.entrySet()) {
                var state = clamPos.getValue();
                if (serverLevel.random.nextFloat() < clamChance) {
                    var hasPropagated = false;
                    Collections.shuffle(directions);
                    for (Direction dir : directions) {
                        if (hasPropagated) continue;
                        var neighborState = serverLevel.getBlockState(clamPos.getKey().relative(dir));
                        if (neighborState.getFluidState().is(FluidTags.WATER) && serverLevel.random.nextBoolean()) {
                            serverLevel.setBlockAndUpdate(clamPos.getKey().relative(dir), state.getBlock().defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, true));
                            hasPropagated = true;
                        }
                    }
                    // Harvest the clam
                    if (hasPropagated && serverLevel.random.nextBoolean()) {
                        serverLevel.destroyBlock(clamPos.getKey(), true);
                    }
                }
            }

            // Calculate nutrient water production
            int sludge = Math.max(entities.size() * 50 - clamMap.size() * 5, 0);
            fluidHandler.fill(new FluidStack(FarmingRegistrator.NUTRIENT_WATER.get(), sludge), IFluidHandler.FluidAction.EXECUTE);
        }
    }

    private void processCropFarm(List<BlockPos> cropPositions) {
        if (level instanceof ServerLevel serverLevel) {
            Collections.shuffle(cropPositions);
            cropPositions.forEach(p -> {
                for (BlockPos pos: new BlockPos[]{p, p.above()}) {
                    HarvestCompatHandler.harvestBlock(serverLevel, pos);

                    if (fluidHandler.getFluidInTank(0).getAmount() >= 100) {
                        var state = serverLevel.getBlockState(pos);
                        if (state.getBlock() instanceof BonemealableBlock bonemealableBlock) {
                            bonemealableBlock.performBonemeal(serverLevel, serverLevel.random, pos, state);
                            fluidHandler.drain(100, IFluidHandler.FluidAction.EXECUTE);
                            level.levelEvent(1505, pos, 15);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void setMultiBlockData(MultiBlockDetector.MultiBlockData multiBlockData) {
        // sync if the multiblock is formed or has changed from/to formed
        if (level instanceof ServerLevel serverLevel && (this.farmConfig != multiBlockData || multiBlockData != null)) {
            this.sync(serverLevel);
        }
        this.farmConfig = multiBlockData;
        this.setChanged();
    }

    @Override
    public MultiBlockDetector.MultiBlockData getMultiblockData() {
        return this.farmConfig;
    }

    public void sync(Level level) {
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        invalidateCapabilities();
    }
}
