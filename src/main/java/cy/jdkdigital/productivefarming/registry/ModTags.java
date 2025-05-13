package cy.jdkdigital.productivefarming.registry;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags
{
    public static class Blocks {
        public static final TagKey<Block> FARM_BLOCKS = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "farm_blocks"));
    }

    public static final TagKey<EntityType<?>> FISH_FARM_ENTITIES = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "fish_farm_entities"));
    public static final TagKey<Block> FARMABLE_FISH_BLOCKS = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "farmable_fish_blocks"));

    public static final TagKey<Block> FARMLAND = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "farmland"));
    public static final TagKey<Item> FERTILIZERS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "fertilizers"));

    public static final TagKey<Item> CRAB_FOOD = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "crab_food"));

    public static final TagKey<Item> FISHES = TagKey.create(Registries.ITEM, ResourceLocation.withDefaultNamespace("fishes"));
    public static final TagKey<Item> SEEDS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "seeds"));
    public static final TagKey<Item> BERRIES = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "berries"));
    public static final TagKey<Item> HERBS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "herbs"));
    public static final TagKey<Item> MUSHROOMS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "mushrooms"));
    public static final TagKey<Item> DRIED_TOBACCO = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "tobacco/dried"));
    public static final TagKey<Item> TOBACCO = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "tobacco"));

    // Storage blocks (crates of food)
    public static final TagKey<Block> STORAGE_BLOCKS_POTATO = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/potato"));

    public static TagKey<PoiType> SALT_LICK_POI_TAG = TagKey.create(Registries.POINT_OF_INTEREST_TYPE, ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "salt_lick"));
}
