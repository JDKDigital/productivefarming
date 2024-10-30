package cy.jdkdigital.productivefarming.common.fluid.type;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class NutrientWaterType extends FluidType
{
    public NutrientWaterType() {
        super(Properties.create()
                .descriptionId("block." + ProductiveFarming.MODID + ".nutrient_water")
                .fallDistanceModifier(0F)
                .canExtinguish(true)
                .canConvertToSource(false)
                .supportsBoating(true)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)
                .canHydrate(true));
    }

    @Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer)
    {
        consumer.accept(new IClientFluidTypeExtensions()
        {
            private static final ResourceLocation
                    UNDERWATER_LOCATION = ResourceLocation.withDefaultNamespace("textures/misc/underwater.png"),
                    WATER_STILL = ResourceLocation.withDefaultNamespace("block/water_still"),
                    WATER_FLOW = ResourceLocation.withDefaultNamespace("block/water_flow"),
                    WATER_OVERLAY = ResourceLocation.withDefaultNamespace("block/water_overlay");

            @Override
            public ResourceLocation getStillTexture()
            {
                return WATER_STILL;
            }

            @Override
            public ResourceLocation getFlowingTexture()
            {
                return WATER_FLOW;
            }

            @Nullable
            @Override
            public ResourceLocation getOverlayTexture()
            {
                return WATER_OVERLAY;
            }

            @Override
            public ResourceLocation getRenderOverlayTexture(Minecraft mc)
            {
                return UNDERWATER_LOCATION;
            }

            @Override
            public int getTintColor()
            {
                return 0xFFb57d21;
            }

            @Override
            public int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos)
            {
                return getTintColor();
            }
        });
    }
}
