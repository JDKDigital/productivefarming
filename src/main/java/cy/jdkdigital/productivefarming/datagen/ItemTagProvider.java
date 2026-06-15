package cy.jdkdigital.productivefarming.datagen;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import cy.jdkdigital.productivefarming.registry.ModTags;
import cy.jdkdigital.productivefarming.util.CropConfig;
import cy.jdkdigital.productivefarming.util.FishConfig;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagCopyingItemTagProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ItemTagProvider extends BlockTagCopyingItemTagProvider
{
    public ItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> future, CompletableFuture<TagsProvider.TagLookup<Block>> provider) {
        super(output, future, provider, ProductiveFarming.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        copy(BlockTags.SMALL_FLOWERS, ItemTags.SMALL_FLOWERS);
        copy(BlockTags.create(Identifier.withDefaultNamespace("tall_flowers")), ItemTags.create(Identifier.withDefaultNamespace("tall_flowers")));
        copy(ModTags.Blocks.FARM_WALL_BLOCKS, ModTags.Items.FARM_WALL_BLOCKS);
        tag(ItemTags.DURABILITY_ENCHANTABLE).add(FarmingRegistrator.CORN_COB_PIPE.get());
        tag(ModTags.Items.FERTILIZERS).add(Items.BONE_MEAL);
        tag(ModTags.Items.MUSHROOMS).add(Items.BROWN_MUSHROOM, Items.RED_MUSHROOM, Items.CRIMSON_FUNGUS, Items.WARPED_FUNGUS);
        tag(ModTags.Items.DRIED_TOBACCO).add(FarmingRegistrator.DRIED_TOBACCO.get());
        tag(ModTags.Items.TOBACCO).addTag(ModTags.Items.DRIED_TOBACCO);

        tag(ModTags.Items.VANILLA_SEEDS).add(Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS, Items.POTATO, Items.CARROT, Items.RED_MUSHROOM, Items.BROWN_MUSHROOM, Items.CRIMSON_FUNGUS, Items.WARPED_FUNGUS);

        tag(ModTags.Items.EXTERNAL_SEEDS)
                .add(Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS, Items.POTATO, Items.CARROT)
                .addOptionalTag(ItemTags.create(Identifier.fromNamespaceAndPath("mysticalagriculture", "seeds")))
                .addOptionalTag(ItemTags.create(Identifier.fromNamespaceAndPath("c", "seeds")));

        var beeFood = tag(ItemTags.create(Identifier.withDefaultNamespace("bee_food")));
        FarmingRegistrator.FLOWERS.forEach(flowerConfig -> beeFood.add(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, flowerConfig.name()))));

//        tag(ModTags.Items.CRAB_FOOD)

        for (CropConfig crop: FarmingRegistrator.HERBS) {
            var herbTag = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "herbs/" + crop.name()));
            tag(herbTag).add(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name())));
            tag(ModTags.Items.HERBS).addTag(herbTag);
            addSeed(crop);
            addParentTag("herbs", "herbs/" + crop.name());
        }
        for (CropConfig crop: FarmingRegistrator.BERRIES) {
            var tag = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "berries/" + crop.name()));
            tag(tag).add(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name())));
            tag(ModTags.Items.BERRIES).addTag(tag);
            addSeed(crop);
        }
        for (CropConfig crop: FarmingRegistrator.CROPS) {
            var tag = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "crops/" + crop.name()));
            addParentTag("crops", "crops/" + crop.name());
            tag(tag).add(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name())));
            addSeed(crop);
        }
        for (CropConfig crop: FarmingRegistrator.TRELLIS) {
            var tag = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "crops/" + crop.name()));
            tag(tag).add(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name())));
            addParentTag("crops", "crops/" + crop.name());
            addSeed(crop);
            tag(ModTags.Items.FENCEPOST_CROP).add(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name())));
        }
        for (CropConfig crop: FarmingRegistrator.VERTICAL_TRELLIS) {
            var tag = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "crops/" + crop.name()));
            addParentTag("crops", "crops/" + crop.name());
            tag(tag).add(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name())));
            addSeed(crop);
            tag(ModTags.Items.FENCEPOST_CROP).add(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name())));
        }
        for (CropConfig crop: FarmingRegistrator.GRAPES) {
            var tag = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "crops/" + crop.name()));
            addParentTag("crops", "crops/" + crop.name());
            tag(tag).add(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name())));
            addSeed(crop);
            tag(ModTags.Items.FENCEPOST_CROP).add(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name() + "_seeds")));
        }
        for (CropConfig crop: FarmingRegistrator.STEMS) {
            var tag = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "crops/" + crop.name()));
            addParentTag("crops", "crops/" + crop.name());
            tag(tag).add(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name())));
            addSeed(crop);
        }
        for (CropConfig crop: FarmingRegistrator.SHROOMS) {
            tag(ModTags.Items.MUSHROOMS).add(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name())));
        }
        for (FishConfig fish: FarmingRegistrator.FISHIES) {
            var tag = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", fish.name()));
            if (fish.hasBlock()) {
                tag(tag).add(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, fish.name())));
            } else {
                tag(tag).add(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "raw_" + fish.name())));
            }
            tag(ModTags.Items.FISHES).addTag(tag);
        }

        FarmingRegistrator.CRATED_CROPS.forEach(resourceLocation -> {
            var bTagKey = BlockTags.create(Identifier.fromNamespaceAndPath("c", "storage_blocks/" + resourceLocation.getPath()));
            var tagKey = ItemTags.create(Identifier.fromNamespaceAndPath("c", "storage_blocks/" + resourceLocation.getPath()));
            copy(bTagKey, tagKey);
            tag(Tags.Items.STORAGE_BLOCKS).addTag(tagKey);
        });

        FarmingRegistrator.SEED_BAGS.forEach(resourceLocation -> {
            var tagKey = ItemTags.create(Identifier.fromNamespaceAndPath("c", "storage_blocks/" + resourceLocation.getPath()));
            tag(tagKey).add(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, resourceLocation.withPath(p -> p + "_bag").getPath())));
            tag(Tags.Items.STORAGE_BLOCKS).addTag(tagKey);
        });

        addGeneralTag("leafy_greens", "arugula", "bok_choy", "butterhead_lettuce", "celery", "chard", "collard", "endive", "iceberg_lettuce", "kale", "romain_lettuce", "spinach", "watercress");
        addGeneralTag("tomatoes", "roma_tomato", "beefsteak_tomato", "black_beauty_tomato", "blue_beauty_tomato", "white_wonder_tomato");
        addGeneralTag("cherry_tomatoes", "cherry_tomato", "chocolate_pear_tomato", "yellow_pear_tomato", "sungold_tomato");
        addGeneralTag("vegetables", "arrowroot", "green_bell_pepper", "yellow_bell_pepper", "orange_bell_pepper", "red_bell_pepper", "black_bell_pepper", "purple_bell_pepper", "white_bell_pepper", "broccoli", "brussels_sprout", "burdock_root", "cauliflower", "daikon", "eddoe", "eggplant", "endive", "kohlrabi", "leek", "onion", "parsnip", "peas", "pineapple", "radish", "rhubarb", "rutabaga", "salsify", "sugar_beet", "turnip", "ulluco", "wasabi", "peanuts", "ginger", "green_bean", "spring_onion", "sweet_potato", "wintergreen", "pitaya", "monstera_deliciosa", "black_aztec_corn", "blue_jade_corn", "rainbow_corn", "sugar_pearl_corn", "yellow_dent_corn", "tomatillo", "konjac", "okra", "asparagus", "artichoke", "malanga", "yam", "water_chestnut", "water_caltrop");
        addGeneralTag("corn", "black_aztec_corn", "blue_jade_corn", "rainbow_corn", "sugar_pearl_corn", "yellow_dent_corn");

        buildStatCrops();

        addMysticalTier(ModTags.Items.MYSTICAL_TIER_1, "deepslate_seeds", "dirt_seeds", "ice_seeds", "inferium_seeds", "stone_seeds", "wood_seeds");
        addMysticalTier(ModTags.Items.MYSTICAL_TIER_2, "aluminum_seeds", "amethyst_seeds", "apatite_seeds", "armadillo_seeds", "basalt_seeds", "chicken_seeds", "coal_seeds", "coral_seeds", "cow_seeds", "dye_seeds", "fish_seeds", "grains_of_infinity_seeds", "honey_seeds", "limestone_seeds", "marble_seeds", "menril_seeds", "mystical_flower_seeds", "nature_seeds", "nether_seeds", "pig_seeds", "rubber_seeds", "saltpeter_seeds", "sheep_seeds", "silicon_seeds", "slime_seeds", "squid_seeds", "sulfur_seeds", "turtle_seeds");
        addMysticalTier(ModTags.Items.MYSTICAL_TIER_3, "amethyst_bronze_seeds", "aquamarine_seeds", "basalz_seeds", "blitz_seeds", "blizz_seeds", "brass_seeds", "bronze_seeds", "certus_quartz_seeds", "conductive_alloy_seeds", "copper_alloy_seeds", "copper_seeds", "creeper_seeds", "glowstone_seeds", "graphite_seeds", "iron_seeds", "ironwood_seeds", "lead_seeds", "manasteel_seeds", "nether_quartz_seeds", "obsidian_seeds", "phantom_seeds", "pig_iron_seeds", "prismarine_seeds", "quartz_enriched_iron_seeds", "rabbit_seeds", "redstone_alloy_seeds", "redstone_seeds", "sculk_seeds", "silver_seeds", "skeleton_seeds", "sky_stone_seeds", "slimesteel_seeds", "spider_seeds", "steeleaf_seeds", "tin_seeds", "zinc_seeds", "zombie_seeds");
        addMysticalTier(ModTags.Items.MYSTICAL_TIER_4, "blaze_seeds", "blazing_crystal_seeds", "breeze_seeds", "cobalt_seeds", "compressed_iron_seeds", "constantan_seeds", "dark_steel_seeds", "electrum_seeds", "elementium_seeds", "end_seeds", "enderman_seeds", "energetic_alloy_seeds", "energized_steel_seeds", "experience_seeds", "fiery_ingot_seeds", "fluix_seeds", "fluorite_seeds", "flux_infused_ingot_seeds", "ghast_seeds", "gold_seeds", "hop_graphite_seeds", "invar_seeds", "knightmetal_seeds", "lapis_lazuli_seeds", "lumium_seeds", "nickel_seeds", "osmium_seeds", "peridot_seeds", "pulsating_alloy_seeds", "refined_glowstone_seeds", "refined_obsidian_seeds", "rose_gold_seeds", "ruby_seeds", "sapphire_seeds", "signalum_seeds", "soularium_seeds", "soulium_seeds", "starmetal_seeds", "steel_seeds", "uranium_seeds");
        addMysticalTier(ModTags.Items.MYSTICAL_TIER_5, "cyanite_seeds", "diamond_seeds", "draconium_seeds", "emerald_seeds", "end_steel_seeds", "enderium_seeds", "flux_infused_gem_seeds", "hepatizon_seeds", "iridium_seeds", "manyullyn_seeds", "netherite_seeds", "niotic_crystal_seeds", "platinum_seeds", "queens_slime_seeds", "rock_crystal_seeds", "spirited_crystal_seeds", "terrasteel_seeds", "uraninite_seeds", "vibrant_alloy_seeds", "wither_skeleton_seeds", "yellorium_seeds");
        addMysticalTier(ModTags.Items.MYSTICAL_TIER_6);

        addParentTag("leafy_green", "leafy_greens");
        addParentTag("tomatoes", "cherry_tomatoes");
        addParentTag("tomato", "tomatoes");
        addParentTag("cherry_tomato", "cherry_tomatoes");
        addParentTag("vegetable", "vegetables");
    }

    private void buildStatCrops() {
        var appender = tag(ModTags.Items.STAT_CROPS);
        appender.addTag(ModTags.Items.EXTERNAL_SEEDS);
        List<List<CropConfig>> ownCrops = List.of(FarmingRegistrator.CROPS, FarmingRegistrator.HERBS, FarmingRegistrator.BERRIES, FarmingRegistrator.TRELLIS, FarmingRegistrator.VERTICAL_TRELLIS, FarmingRegistrator.GRAPES, FarmingRegistrator.STEMS);
        for (List<CropConfig> list : ownCrops) {
            for (CropConfig crop : list) {
                String path = crop.hasSeed() ? crop.name() + "_seeds" : crop.name();
                appender.add(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, path)));
            }
        }
        for (CropConfig crop : FarmingRegistrator.SHROOMS) {
            appender.add(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name())));
        }
        appender.add(Items.BROWN_MUSHROOM, Items.RED_MUSHROOM, Items.CRIMSON_FUNGUS, Items.WARPED_FUNGUS);
    }

    private void addMysticalTier(TagKey<Item> tierTag, String... seeds) {
        var builder = getOrCreateRawBuilder(tierTag);
        for (String seed : seeds) {
            builder.addOptionalElement(Identifier.fromNamespaceAndPath("mysticalagriculture", seed));
        }
    }

    private void addParentTag(String parentTag, String tagName) {
        var parent = tag(ItemTags.create(Identifier.fromNamespaceAndPath("c", parentTag)));
        var tag = ItemTags.create(Identifier.fromNamespaceAndPath("c", tagName));
        parent.addTag(tag);
    }

    private void addGeneralTag(String tagName, String... crops) {
        var tag = tag(ItemTags.create(Identifier.fromNamespaceAndPath("c", tagName)));
        for (String crop : crops) {
            tag.add(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, crop)));
        }
    }

    private void addSeed(CropConfig crop) {
        if (crop.hasSeed()) {
            var seedTag = tag((Tags.Items.SEEDS));
            var cropSeedTag = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "seeds/" + crop.name()));
            tag(cropSeedTag).add(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name() + "_seeds")));
            tag(ItemTags.VILLAGER_PLANTABLE_SEEDS).addTag(cropSeedTag);
            seedTag.addTag(cropSeedTag);
        }
    }

    @Override
    public String getName() {
        return "Productive Farming Item Tags Provider";
    }
}
