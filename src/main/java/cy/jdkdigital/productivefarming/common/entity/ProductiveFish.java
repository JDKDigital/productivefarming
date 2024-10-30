package cy.jdkdigital.productivefarming.common.entity;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.item.ItemStack;

interface ProductiveFish extends Bucketable
{
    String getFishName();

    default ItemStack getBucket() {
        return new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, getFishName() + "_bucket")));
    }
}
