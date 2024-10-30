package cy.jdkdigital.productivefarming.common.fluid;

import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

public abstract class NutrientWater extends BaseFlowingFluid
{
    protected NutrientWater() {
        super(new Properties(
                FarmingRegistrator.NUTRIENT_WATER_TYPE,
                FarmingRegistrator.NUTRIENT_WATER,
                FarmingRegistrator.NUTRIENT_WATER_FLOWING
        ).bucket(FarmingRegistrator.NUTRIENT_WATER_BUCKET));
    }

    public static class Flowing extends NutrientWater
    {
        @Override
        public boolean isSource(FluidState pState) {
            return false;
        }

        @Override
        public int getAmount(FluidState pState) {
            return pState.getValue(BlockStateProperties.LEVEL_FLOWING);
        }
    }

    public static class Source extends NutrientWater
    {
        @Override
        public boolean isSource(FluidState pState) {
            return true;
        }

        @Override
        public int getAmount(FluidState pState) {
            return BlockStateProperties.MAX_LEVEL_8;
        }
    }
}
