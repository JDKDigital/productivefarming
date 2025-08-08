package cy.jdkdigital.productivefarming.util;

import cy.jdkdigital.productivefarming.common.block.ProductiveCropBlock;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import net.minecraft.world.food.FoodProperties;

public final class CropConfig
{
    private final String name;
    private final boolean hasSeed;
    private final FoodProperties food;
    private final FarmingRegistrator.CropBlockSupplier supplier;
    private final int cropColor;

    public CropConfig(String name, boolean hasSeed, FoodProperties food) {
        this(name, hasSeed, food, 0xff);
    }

    public CropConfig(String name, boolean hasSeed, FoodProperties food, int cropColor) {
        this(name, hasSeed, food, ProductiveCropBlock::new, cropColor);
    }

    public CropConfig(String name, boolean hasSeed, FoodProperties food, FarmingRegistrator.CropBlockSupplier supplier) {
        this(name, hasSeed, food, supplier, 0xff);
    }

    public CropConfig(String name, boolean hasSeed, FoodProperties food, FarmingRegistrator.CropBlockSupplier supplier, int cropColor) {
        this.name = name;
        this.hasSeed = hasSeed;
        this.food = food;
        this.supplier = supplier;
        this.cropColor = cropColor;
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

    public int getCropColor() {
        return cropColor;
    }
}
