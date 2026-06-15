package cy.jdkdigital.productivefarming.common.block.entity;

import cy.jdkdigital.productivefarming.registry.FarmingDataComponents;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import cy.jdkdigital.productivelib.common.block.entity.AbstractBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

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
    public void loadPacketNBT(ValueInput input) {
        super.loadPacketNBT(input);
        setColor(input.getIntOr("color", this.color));
    }

    @Override
    public void savePacketNBT(ValueOutput output) {
        super.savePacketNBT(output);
        output.putInt("color", this.color);
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter componentInput) {
        super.applyImplicitComponents(componentInput);
        setColor(componentInput.getOrDefault(FarmingDataComponents.COLOR, this.color));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(FarmingDataComponents.COLOR, this.getColor());
    }
}
