package cy.jdkdigital.productivefarming.common.block.entity;

import cy.jdkdigital.productivefarming.registry.FarmingDataComponents;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import cy.jdkdigital.productivelib.common.block.entity.AbstractBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class ColorfulFlowerPotBlockEntity extends AbstractBlockEntity
{
    private int color;

    public ColorfulFlowerPotBlockEntity(BlockPos pos, BlockState state) {
        this(pos, state, 0xff);
    }

    public ColorfulFlowerPotBlockEntity(BlockPos pos, BlockState state, int defaultColor) {
        super(FarmingRegistrator.FLOWER_POT_BLOCK_ENTITY.get(), pos, state);
        setColor(defaultColor);
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
            setColor(tag.getInt("color"));
        }
    }

    @Override
    public void savePacketNBT(CompoundTag tag, HolderLookup.Provider provider) {
        super.savePacketNBT(tag, provider);
        tag.putInt("color", this.color);
    }

//    @Override
//    protected void applyImplicitComponents(DataComponentInput componentInput) {
//        super.applyImplicitComponents(componentInput);
//        setColor(componentInput.getOrDefault(FarmingDataComponents.COLOR, this.color));
//    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(FarmingDataComponents.COLOR, this.getColor());
    }
}
