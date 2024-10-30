package cy.jdkdigital.productivefarming.util;

import cy.jdkdigital.productivefarming.common.block.FastCropBlock;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import net.minecraft.world.food.FoodProperties;

public final class CropConfig
{
    private final String name;
    private final boolean hasSeed;
    private final FoodProperties food;
    private final FarmingRegistrator.CropBlockSupplier supplier;

    public CropConfig(String name, boolean hasSeed, FoodProperties food) {
        this(name, hasSeed, food, FastCropBlock::new);
    }

    public CropConfig(String name, boolean hasSeed, FoodProperties food, FarmingRegistrator.CropBlockSupplier supplier) {
        this.name = name;
        this.hasSeed = hasSeed;
        this.food = food;
        this.supplier = supplier;
    }

    public String name() {
        return name;
    }

    public boolean hasSeed() {
        return hasSeed;
    }

    public FoodProperties food() {
        return food;
    }

    public FarmingRegistrator.CropBlockSupplier supplier() {
        return supplier;
    }
}
