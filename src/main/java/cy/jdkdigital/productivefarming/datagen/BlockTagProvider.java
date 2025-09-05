package cy.jdkdigital.productivefarming.datagen;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.common.block.VineLeafBlock;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import cy.jdkdigital.productivefarming.registry.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class BlockTagProvider extends BlockTagsProvider
{
    public BlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, ExistingFileHelper helper) {
        super(output, provider, ProductiveFarming.MODID, helper);
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
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "cantaloupe")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "honeydew_melon"))
        );
        // Utilitarian compat
        tag(BlockTags.create(ResourceLocation.parse("utilitarian:farmland_cansurvive"))).addTag(Tags.Blocks.FENCES).add(
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "cantaloupe")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "honeydew_melon"))
        );

        // Flowers from bonemeal
        tag(ModTags.Blocks.CAN_SPAWN_FROM_BONEMEAL).add(
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "amaryllis")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "anemone")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "balloon_flower")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "black_bearded_iris")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "cattail")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "cape_leadwort")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "chrysanthemum")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "dahlia")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "great_lobelia")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "hellebore")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "himalayan_poppy")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "hydrangea")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "motherwort")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "sea_holly")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "skullcap")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "stinging_nettle")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "valerian_root")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "zinnia"))
        );

        tag(ModTags.Blocks.PLAINS_VILLAGE_FARM_CROPS).add(
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "bok_choy")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "broccoli")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "green_bell_pepper")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "brussels_sprout")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "butterhead_lettuce")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "cabbage")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "cauliflower")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "celery")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "eggplant")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "kale")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "onion")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "radish")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "spinach")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "turnip")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "spring_onion"))
        );
        tag(ModTags.Blocks.SAVANNA_VILLAGE_FARM_CROPS).add(
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "kale")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "chard")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "leek")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "broccoli")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "spinach")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "cabbage")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "butterhead_lettuce")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "turnip")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "brussels_sprout")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "rutabaga")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "collards"))
        );
        tag(ModTags.Blocks.SNOWY_VILLAGE_FARM_CROPS).add(
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "kale")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "chard")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "leek")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "broccoli")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "spinach")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "cabbage")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "butterhead_lettuce")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "turnip")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "brussels_sprout")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "rutabaga")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "collards"))
        );
        tag(ModTags.Blocks.TAIGA_VILLAGE_FARM_CROPS).add(
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "kale")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "chard")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "leek")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "broccoli")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "spinach")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "cabbage")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "butterhead_lettuce")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "turnip")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "brussels_sprout")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "rutabaga")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "collards"))
        );
        tag(ModTags.Blocks.DESERT_VILLAGE_FARM_CROPS).add(
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "eggplant")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "squash")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "lentils")),
                BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "mustard"))
        );

        FarmingRegistrator.FLOWERS.forEach(flowerConfig -> {
            if (flowerConfig.isDouble()) {
                tag(BlockTags.TALL_FLOWERS).add(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, flowerConfig.name())));
            } else {
                tag(BlockTags.SMALL_FLOWERS).add(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, flowerConfig.name())));
            }
        });
        FarmingRegistrator.VINES.forEach(flowerConfig -> {
            tag(BlockTags.FLOWERS).add(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, flowerConfig.name())));
        });
        tag(ModTags.Blocks.POLLINATABLE).addTag(BlockTags.FLOWERS).addTag(BlockTags.CROPS);

        Arrays.stream(FarmingRegistrator.getAllCrops()).forEach(crop -> {
            tag(BlockTags.CROPS).add(crop);
        });
        FarmingRegistrator.GRAPES.forEach(crop -> {
            var block = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name() + "_leaves"));
            tag(BlockTags.FENCES).add(block);
        });
        FarmingRegistrator.TRELLIS.forEach(crop -> {
            var block = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name() + "_leaves"));
            tag(BlockTags.FENCES).add(block);
        });
        FarmingRegistrator.VERTICAL_TRELLIS.forEach(crop -> {
            var block = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name()));
            tag(BlockTags.FENCES).add(block);
        });

        FarmingRegistrator.FISHIES.forEach(fishConfig -> {
            if (fishConfig.hasBlock()) {
                tag(ModTags.Blocks.FARMABLE_FISH_BLOCKS).add(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, fishConfig.name())));
            }
        });

        FarmingRegistrator.CRATED_CROPS.forEach(resourceLocation -> {
            var tagKey = BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/" + resourceLocation.getPath()));
            tag(tagKey).add(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, resourceLocation.withPath(p -> p + "_crate").getPath())));
            tag(Tags.Blocks.STORAGE_BLOCKS).addTag(tagKey);
        });
    }

    @Override
    public String getName() {
        return "Productive Farming Block Tags Provider";
    }
}
