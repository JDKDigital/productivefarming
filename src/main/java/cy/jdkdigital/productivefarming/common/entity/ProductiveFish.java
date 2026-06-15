package cy.jdkdigital.productivefarming.common.entity;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

interface ProductiveFish extends Bucketable
{
    String getFishName();

    default ItemStack getBucket() {
        return new ItemStack(BuiltInRegistries.ITEM.get(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, getFishName() + "_bucket")).map(Holder::value).orElse(Items.AIR));
    }
}
