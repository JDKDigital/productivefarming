package cy.jdkdigital.productivefarming.util;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.food.FoodProperties;

public record FishConfig(String name, EntityType.EntityFactory entitySupplier, EntityRendererProvider entityRenderer, boolean hasBlock, FoodProperties foodRaw, FoodProperties foodCooked) {}
