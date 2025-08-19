package cy.jdkdigital.productivefarming.common.block.entity;

import cy.jdkdigital.productivefarming.common.block.DoubleCropBlock;
import cy.jdkdigital.productivefarming.common.item.PollenItem;
import cy.jdkdigital.productivefarming.registry.FarmingDataComponents;
import cy.jdkdigital.productivefarming.util.TraitsHelper;
import cy.jdkdigital.productivelib.common.block.entity.AbstractBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public abstract class CropBlockEntity extends AbstractBlockEntity
{
    private ResourceLocation mutation;
    private int growth;
    private int yield;
    private int resistance;
    private int mutability;

    public CropBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    public ResourceLocation getMutation() {
        return this.mutation;
    }

    public void setMutation(ResourceLocation mutation) {
        setMutation(mutation, false);
    }

    public void setMutation(ResourceLocation mutation, boolean recursive) {
        this.mutation = mutation;
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);

            if (!recursive && getBlockState().getBlock() instanceof DoubleCropBlock) {
                var otherHalf = level.getBlockEntity(getBlockPos().relative(getBlockState().getValue(BlockStateProperties.DOUBLE_BLOCK_HALF).equals(DoubleBlockHalf.LOWER) ? Direction.UP : Direction.DOWN));
                if (otherHalf instanceof CropBlockEntity otherCropBlockEntity && !otherCropBlockEntity.hasMutation()) {
                    otherCropBlockEntity.setMutation(mutation, true);
                }
            }
        }
    }

    public int getGrowth() {
        return growth;
    }

    public void setGrowth(int growth) {
        this.growth = Math.min(growth, TraitsHelper.getMaxValue(TraitsHelper.GROWTH));
    }

    public int getYield() {
        return yield;
    }

    public void setYield(int yield) {
        this.yield = Math.min(yield, TraitsHelper.getMaxValue(TraitsHelper.YIELD));
    }

    public int getResistance() {
        return resistance;
    }

    public void setResistance(int resistance) {
        this.resistance = Math.min(resistance, TraitsHelper.getMaxValue(TraitsHelper.RESISTANCE));
    }

    public int getMutability() {
        return mutability;
    }

    public void setMutability(int mutability) {
        this.mutability = Math.min(mutability, TraitsHelper.getMaxValue(TraitsHelper.MUTABILITY));
    }

    public boolean hasMutation() {
        return this.mutation != null;
    }

    public int getMutationColor() {
        if (hasMutation()) {
            return PollenItem.getColor(BuiltInRegistries.ITEM.get(this.mutation).getDefaultInstance());
        }
        return -1;
    }

    @Override
    public void loadPacketNBT(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadPacketNBT(tag, provider);
        if (tag.contains("mutation")) {
            this.mutation = ResourceLocation.parse(tag.getString("mutation"));
        }
        this.setGrowth(tag.contains(TraitsHelper.GROWTH) ? tag.getInt(TraitsHelper.GROWTH) : 0);;
        this.setYield(tag.contains(TraitsHelper.YIELD) ? tag.getInt(TraitsHelper.YIELD) : 0);;
        this.setResistance(tag.contains(TraitsHelper.RESISTANCE) ? tag.getInt(TraitsHelper.RESISTANCE) : 0);;
        this.setMutability(tag.contains(TraitsHelper.MUTABILITY) ? tag.getInt(TraitsHelper.MUTABILITY) : 0);;
    }

    @Override
    public void savePacketNBT(CompoundTag tag, HolderLookup.Provider provider) {
        super.savePacketNBT(tag, provider);
        if (this.hasMutation()) {
            tag.putString("mutation", this.mutation.toString());
        }
        tag.putInt(TraitsHelper.GROWTH, this.growth);
        tag.putInt(TraitsHelper.YIELD, this.yield);
        tag.putInt(TraitsHelper.RESISTANCE, this.resistance);
        tag.putInt(TraitsHelper.MUTABILITY, this.mutability);
    }

    @Override
    protected void applyImplicitComponents(BlockEntity.DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        this.setGrowth(componentInput.getOrDefault(FarmingDataComponents.GROWTH, 0));
        this.setYield(componentInput.getOrDefault(FarmingDataComponents.YIELD, 0));
        this.setResistance(componentInput.getOrDefault(FarmingDataComponents.RESISTANCE, 0));
        this.setMutability(componentInput.getOrDefault(FarmingDataComponents.MUTABILITY, 0));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(FarmingDataComponents.GROWTH, this.getGrowth());
        components.set(FarmingDataComponents.YIELD, this.getYield());
        components.set(FarmingDataComponents.RESISTANCE, this.getResistance());
        components.set(FarmingDataComponents.MUTABILITY, this.getMutability());
    }

    public void copyTraitsFromOtherCrop(CropBlockEntity cropBlockEntity) {
        this.setGrowth(cropBlockEntity.getGrowth());
        this.setYield(cropBlockEntity.getYield());
        this.setResistance(cropBlockEntity.getResistance());
        this.setMutability(cropBlockEntity.getMutability());
    }

    public ItemStack getMutatedSeedStack(ResourceLocation mutation) {
        var seed = BuiltInRegistries.ITEM.get(mutation.withPath(p -> p + "_seeds")).getDefaultInstance();
        if (seed.is(Items.AIR)) {
            seed = BuiltInRegistries.ITEM.get(mutation).getDefaultInstance();
        }
        return applyComponentsToItemStack(seed);
    }

    public ItemStack applyComponentsToItemStack(ItemStack stack) {
        stack.set(FarmingDataComponents.GROWTH, this.getGrowth());
        stack.set(FarmingDataComponents.YIELD, this.getYield());
        stack.set(FarmingDataComponents.RESISTANCE, this.getResistance());
        stack.set(FarmingDataComponents.MUTABILITY, this.getMutability());
        return stack;
    }
}
