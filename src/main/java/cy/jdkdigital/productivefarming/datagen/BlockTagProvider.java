package cy.jdkdigital.productivefarming.datagen;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import cy.jdkdigital.productivefarming.registry.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class BlockTagProvider extends BlockTagsProvider
{
    public BlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, ProductiveFarming.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(FarmingRegistrator.FARM_CONTROLLER.get(), FarmingRegistrator.FEEDING_TROUGH.get(), FarmingRegistrator.WATERING_TROUGH.get());
        tag(ModTags.Blocks.FARMLAND).add(Blocks.FARMLAND);
        tag(ModTags.Blocks.FARM_WALL_BLOCKS)
                .addTag(BlockTags.STONE_BRICKS)
                .addTag(Tags.Blocks.GLASS_BLOCKS)
                .add(Blocks.HOPPER)
                .add(Blocks.BRICKS, Blocks.MUD_BRICKS, Blocks.QUARTZ_BRICKS, Blocks.PRISMARINE_BRICKS)
                .add(Blocks.DEEPSLATE_BRICKS, Blocks.CRACKED_DEEPSLATE_BRICKS, Blocks.END_STONE_BRICKS)
                .add(Blocks.NETHER_BRICKS, Blocks.CRACKED_NETHER_BRICKS, Blocks.CHISELED_NETHER_BRICKS, Blocks.RED_NETHER_BRICKS)
                .add(Blocks.TUFF_BRICKS, Blocks.CHISELED_TUFF_BRICKS, Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS);
        tag(ModTags.Blocks.FARM_BLOCKS).addTag(ModTags.Blocks.FARM_WALL_BLOCKS).add(FarmingRegistrator.FARM_CONTROLLER.get(), FarmingRegistrator.FARM_HATCH.get());

        tag(BlockTags.MAINTAINS_FARMLAND).addTag(Tags.Blocks.FENCES).add(
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "cantaloupe")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "honeydew_melon"))
        );
        // Utilitarian compat
        tag(BlockTags.create(Identifier.parse("utilitarian:farmland_cansurvive"))).addTag(Tags.Blocks.FENCES).add(
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "cantaloupe")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "honeydew_melon"))
        );

        // FTB Ultimine compat
        var umBlacklist = tag(BlockTags.create(Identifier.parse("ftbultimine:single_crop_harvesting_blacklist")));
        for (Block cropBlock : FarmingRegistrator.getAllCrops()) {
            umBlacklist.add(cropBlock);
        }
        FarmingRegistrator.SHROOMS.forEach(cropConfig -> {
            umBlacklist.add(BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, cropConfig.name() + "_growth")));
        });

        // Flowers from bonemeal
        tag(ModTags.Blocks.CAN_SPAWN_FROM_BONEMEAL).add(
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "amaryllis")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "anemone")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "balloon_flower")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "black_bearded_iris")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "cattail")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "cape_leadwort")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "chrysanthemum")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "dahlia")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "great_lobelia")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "hellebore")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "himalayan_poppy")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "hydrangea")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "motherwort")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "sea_holly")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "skullcap")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "stinging_nettle")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "valerian_root")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "zinnia"))
        );

        tag(ModTags.Blocks.PLAINS_VILLAGE_FARM_CROPS).add(
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "bok_choy")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "broccoli")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "green_bell_pepper")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "brussels_sprout")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "butterhead_lettuce")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "cabbage")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "cauliflower")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "celery")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "eggplant")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "kale")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "onion")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "radish")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "spinach")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "turnip")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "spring_onion"))
        );
        tag(ModTags.Blocks.SAVANNA_VILLAGE_FARM_CROPS).add(
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "kale")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "chard")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "leek")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "broccoli")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "spinach")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "cabbage")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "butterhead_lettuce")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "turnip")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "brussels_sprout")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "rutabaga")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "collards"))
        );
        tag(ModTags.Blocks.SNOWY_VILLAGE_FARM_CROPS).add(
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "kale")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "chard")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "leek")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "broccoli")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "spinach")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "cabbage")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "butterhead_lettuce")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "turnip")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "brussels_sprout")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "rutabaga")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "collards"))
        );
        tag(ModTags.Blocks.TAIGA_VILLAGE_FARM_CROPS).add(
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "kale")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "chard")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "leek")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "broccoli")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "spinach")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "cabbage")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "butterhead_lettuce")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "turnip")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "brussels_sprout")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "rutabaga")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "collards"))
        );
        tag(ModTags.Blocks.DESERT_VILLAGE_FARM_CROPS).add(
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "eggplant")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "squash")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "lentils")),
                BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "mustard"))
        );

        FarmingRegistrator.FLOWERS.forEach(flowerConfig -> {
            if (flowerConfig.isDouble()) {
                tag(BlockTags.create(Identifier.withDefaultNamespace("tall_flowers"))).add(BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, flowerConfig.name())));
            } else {
                tag(BlockTags.SMALL_FLOWERS).add(BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, flowerConfig.name())));
            }
        });
        FarmingRegistrator.VINES.forEach(flowerConfig -> {
            tag(BlockTags.FLOWERS).add(BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, flowerConfig.name())));
        });
        tag(ModTags.Blocks.POLLINATABLE).addTag(BlockTags.FLOWERS).addTag(BlockTags.CROPS).addTag(ModTags.Blocks.EXTERNAL_STAT_CROPS);

        tag(ModTags.Blocks.EXTERNAL_STAT_CROPS)
                .add(Blocks.WHEAT, Blocks.CARROTS, Blocks.POTATOES, Blocks.BEETROOTS)
                .addOptionalTag(BlockTags.create(Identifier.fromNamespaceAndPath("mysticalagriculture", "crops")))
                .addOptionalTag(BlockTags.create(Identifier.fromNamespaceAndPath("croptopia", "crops")));
        getOrCreateRawBuilder(ModTags.Blocks.EXTERNAL_STAT_CROPS)
                .addOptionalElement(Identifier.fromNamespaceAndPath("silentgear", "flax_plant"))
                .addOptionalElement(Identifier.fromNamespaceAndPath("silentgear", "fluffy_plant"));

        Arrays.stream(FarmingRegistrator.getAllCrops()).forEach(crop -> {
            tag(BlockTags.CROPS).add(crop);
        });
        FarmingRegistrator.GRAPES.forEach(crop -> {
            var block = BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name() + "_leaves"));
            tag(BlockTags.FENCES).add(block);
        });
        FarmingRegistrator.TRELLIS.forEach(crop -> {
            var block = BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name() + "_leaves"));
            tag(BlockTags.FENCES).add(block);
        });
        FarmingRegistrator.VERTICAL_TRELLIS.forEach(crop -> {
            var block = BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name()));
            tag(BlockTags.FENCES).add(block);
        });

        FarmingRegistrator.FISHIES.forEach(fishConfig -> {
            if (fishConfig.hasBlock()) {
                tag(ModTags.Blocks.FARMABLE_FISH_BLOCKS).add(BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, fishConfig.name())));
            }
        });

        FarmingRegistrator.CRATED_CROPS.forEach(resourceLocation -> {
            var tagKey = BlockTags.create(Identifier.fromNamespaceAndPath("c", "storage_blocks/" + resourceLocation.getPath()));
            var block = BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, resourceLocation.withPath(p -> p + "_crate").getPath()));
            tag(tagKey).add(block);
            tag(Tags.Blocks.STORAGE_BLOCKS).addTag(tagKey);
            tag(BlockTags.MINEABLE_WITH_AXE).add(block);
        });
    }

    @Override
    public String getName() {
        return "Productive Farming Block Tags Provider";
    }
}
