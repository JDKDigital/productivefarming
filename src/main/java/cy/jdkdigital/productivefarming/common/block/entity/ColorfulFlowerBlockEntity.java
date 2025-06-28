package cy.jdkdigital.productivefarming.common.block.entity;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.registry.FarmingDataComponents;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import cy.jdkdigital.productivelib.common.block.entity.AbstractBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ColorfulFlowerBlockEntity extends AbstractBlockEntity
{
    private int color;

    public ColorfulFlowerBlockEntity(BlockPos pos, BlockState state) {
        this(pos, state, 0xff);
    }

    public ColorfulFlowerBlockEntity(BlockPos pos, BlockState state, int defaultColor) {
        super(FarmingRegistrator.FLOWER_BLOCK_ENTITY.get(), pos, state);
        this.color = defaultColor;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public void loadPacketNBT(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadPacketNBT(tag, provider);
        if (tag.contains("color")) {
            this.color = tag.getInt("color");
        }
    }

    @Override
    public void savePacketNBT(CompoundTag tag, HolderLookup.Provider provider) {
        super.savePacketNBT(tag, provider);
        tag.putInt("color", this.color);
    }

    @Override
    protected void applyImplicitComponents(BlockEntity.DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        this.color = componentInput.getOrDefault(FarmingDataComponents.COLOR, this.color);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(FarmingDataComponents.COLOR, this.getColor());
    }
}
