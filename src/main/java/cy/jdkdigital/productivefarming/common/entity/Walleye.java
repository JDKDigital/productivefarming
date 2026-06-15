package cy.jdkdigital.productivefarming.common.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.fish.Cod;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class Walleye extends Cod implements ProductiveFish
{
    public Walleye(EntityType<? extends Cod> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public String getFishName() {
        return "walleye";
    }

    @Override
    public ItemStack getBucketItemStack() {
        return getBucket();
    }
}
