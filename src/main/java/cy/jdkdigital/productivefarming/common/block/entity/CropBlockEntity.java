package cy.jdkdigital.productivefarming.common.block.entity;

import cy.jdkdigital.productivefarming.common.block.DoubleCropBlock;
import cy.jdkdigital.productivefarming.common.item.PollenItem;
import cy.jdkdigital.productivefarming.registry.FarmingDataComponents;
import cy.jdkdigital.productivefarming.util.TraitsHelper;
import cy.jdkdigital.productivelib.common.block.entity.AbstractBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public abstract class CropBlockEntity extends AbstractBlockEntity
{
    private Identifier mutation;
    private int growth;
    private int yield;
    private int resistance;
    private int mutability;

    public CropBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    public Identifier getMutation() {
        return this.mutation;
    }

    public void setMutation(Identifier mutation) {
        setMutation(mutation, false);
    }

    public void setMutation(Identifier mutation, boolean isFromLoop) {
        this.mutation = mutation;
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);

            if (!isFromLoop && getBlockState().getBlock() instanceof DoubleCropBlock) {
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
            return PollenItem.getColor(BuiltInRegistries.ITEM.getValue(this.mutation).getDefaultInstance());
        }
        return -1;
    }

    @Override
    public void loadPacketNBT(ValueInput input) {
        super.loadPacketNBT(input);
        input.getString("mutation").ifPresent(s -> this.mutation = Identifier.parse(s));
        this.setGrowth(input.getIntOr(TraitsHelper.GROWTH, 0));
        this.setYield(input.getIntOr(TraitsHelper.YIELD, 0));
        this.setResistance(input.getIntOr(TraitsHelper.RESISTANCE, 0));
        this.setMutability(input.getIntOr(TraitsHelper.MUTABILITY, 0));
    }

    @Override
    public void savePacketNBT(ValueOutput output) {
        super.savePacketNBT(output);
        if (this.hasMutation()) {
            output.putString("mutation", this.mutation.toString());
        }
        output.putInt(TraitsHelper.GROWTH, this.growth);
        output.putInt(TraitsHelper.YIELD, this.yield);
        output.putInt(TraitsHelper.RESISTANCE, this.resistance);
        output.putInt(TraitsHelper.MUTABILITY, this.mutability);
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter componentInput) {
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

    public ItemStack getMutatedSeedStack(Identifier mutation) {
        return applyComponentsToItemStack(TraitsHelper.mutatedSeed(mutation));
    }

    public ItemStack applyComponentsToItemStack(ItemStack stack) {
        return TraitsHelper.applyTraits(stack, getGrowth(), getYield(), getResistance(), getMutability());
    }

    public void increaseStat(String trait) {
        switch (trait) {
            case TraitsHelper.GROWTH -> setGrowth(getGrowth() + 1);
            case TraitsHelper.YIELD -> setYield(getYield() + 1);
            case TraitsHelper.RESISTANCE -> setResistance(getResistance() + 1);
            case TraitsHelper.MUTABILITY -> setMutability(getMutability() + 1);
        }
    }
}
