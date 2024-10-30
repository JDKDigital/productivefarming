package cy.jdkdigital.productivefarming.common.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.level.Level;

public class Sturgeon extends WaterAnimal
{
    public Sturgeon(EntityType<? extends Cod> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
}
