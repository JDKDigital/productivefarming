package cy.jdkdigital.productivefarming.common.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class Lobster extends Cod implements ProductiveFish
{
    public Lobster(EntityType<? extends Cod> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public String getFishName() {
        return "lobster";
    }

    @Override
    public ItemStack getBucketItemStack() {
        return getBucket();
    }
}
