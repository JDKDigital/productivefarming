package cy.jdkdigital.productivefarming.datagen.recipe;

import net.minecraft.world.item.Item;

public record CropDropSpec(Item item, int min, int max, float chance)
{
}
