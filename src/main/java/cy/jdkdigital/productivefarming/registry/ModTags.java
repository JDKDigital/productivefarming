package cy.jdkdigital.productivefarming.registry;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

public class ModTags
{
    public static class Blocks {
        public static final TagKey<Block> FARM_WALL_BLOCKS = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "farm_wall_blocks"));
        public static final TagKey<Block> FARM_BLOCKS = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "farm_blocks"));
        public static final TagKey<Block> POLLINATABLE = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "pollinatable"));
        public static final TagKey<Block> FARMABLE_FISH_BLOCKS = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "farmable_fish_blocks"));
        public static final TagKey<Block> FARMLAND = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("c", "farmland"));
        public static final TagKey<Block> PLAINS_VILLAGE_FARM_CROPS = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "plains_village_farm_crops"));
        public static final TagKey<Block> SAVANNA_VILLAGE_FARM_CROPS = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "savanna_village_farm_crops"));
        public static final TagKey<Block> SNOWY_VILLAGE_FARM_CROPS = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "snowy_village_farm_crops"));
        public static final TagKey<Block> TAIGA_VILLAGE_FARM_CROPS = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "taiga_village_farm_crops"));
        public static final TagKey<Block> DESERT_VILLAGE_FARM_CROPS = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "desert_village_farm_crops"));
        public static final TagKey<Block> CAN_SPAWN_FROM_BONEMEAL = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "can_spawn_from_bonemeal"));
        public static final TagKey<Block> EXTERNAL_STAT_CROPS = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "external_stat_crops"));
    }

    public static class Items {
        public static final TagKey<Item> FARM_WALL_BLOCKS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "farm_wall_blocks"));
        public static final TagKey<Item> VANILLA_SEEDS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "vanilla_seeds"));
        public static final TagKey<Item> EXTERNAL_SEEDS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "external_seeds"));
        public static final TagKey<Item> CRAB_FOOD = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "crab_food"));
        public static final TagKey<Item> FERTILIZERS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "fertilizers"));
        public static final TagKey<Item> FISHES = TagKey.create(Registries.ITEM, Identifier.withDefaultNamespace("fishes"));
        public static final TagKey<Item> BERRIES = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "berries"));
        public static final TagKey<Item> HERBS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "herbs"));
        public static final TagKey<Item> MUSHROOMS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "mushrooms"));
        public static final TagKey<Item> DRIED_TOBACCO = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "tobaccos/dried"));
        public static final TagKey<Item> TOBACCO = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "tobaccos"));
        public static final TagKey<Item> CORN = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "corn"));
        public static final TagKey<Item> FENCEPOST_CROP = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "fencepost_crops"));
        public static final TagKey<Item> STAT_CROPS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "stat_crops"));
        public static final TagKey<Item> MYSTICAL_TIER_1 = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "mystical_tier_1"));
        public static final TagKey<Item> MYSTICAL_TIER_2 = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "mystical_tier_2"));
        public static final TagKey<Item> MYSTICAL_TIER_3 = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "mystical_tier_3"));
        public static final TagKey<Item> MYSTICAL_TIER_4 = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "mystical_tier_4"));
        public static final TagKey<Item> MYSTICAL_TIER_5 = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "mystical_tier_5"));
        public static final TagKey<Item> MYSTICAL_TIER_6 = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "mystical_tier_6"));
    }

    public static class Biomes {
        public static final TagKey<Biome> HAS_FLOWERS = TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "has_flowers"));
    }

    public static final TagKey<EntityType<?>> FISH_FARM_ENTITIES = TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "fish_farm_entities"));

    // Storage blocks (crates of food)
    public static final TagKey<Block> STORAGE_BLOCKS_POTATO = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("c", "storage_blocks/potato"));

    public static TagKey<PoiType> SALT_LICK_POI_TAG = TagKey.create(Registries.POINT_OF_INTEREST_TYPE, Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "salt_lick"));
}
