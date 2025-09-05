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
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ItemTagProvider extends ItemTagsProvider
{
    public ItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> future, CompletableFuture<TagLookup<Block>> provider, ExistingFileHelper helper) {
        super(output, future, provider, ProductiveFarming.MODID, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        copy(BlockTags.SMALL_FLOWERS, ItemTags.SMALL_FLOWERS);
        copy(BlockTags.TALL_FLOWERS, ItemTags.TALL_FLOWERS);
        copy(ModTags.Blocks.FARM_WALL_BLOCKS, ModTags.Items.FARM_WALL_BLOCKS);
        tag(ItemTags.DURABILITY_ENCHANTABLE).add(FarmingRegistrator.CORN_COB_PIPE.get());
        tag(ModTags.Items.FERTILIZERS).add(Items.BONE_MEAL);
        tag(ModTags.Items.MUSHROOMS).add(Items.BROWN_MUSHROOM, Items.RED_MUSHROOM, Items.CRIMSON_FUNGUS, Items.WARPED_FUNGUS);
        tag(ModTags.Items.DRIED_TOBACCO).add(FarmingRegistrator.DRIED_TOBACCO.get());
        tag(ModTags.Items.TOBACCO).addTag(ModTags.Items.DRIED_TOBACCO);
        tag(ModTags.Items.CORN)
                .addTag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "black_aztec_corn")))
                .addTag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "blue_jade_corn")))
                .addTag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "rainbow_corn")))
                .addTag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "sugar_pearl_corn")))
                .addTag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "yellow_dent_corn")));

        tag(ModTags.Items.VANILLA_SEEDS).add(Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS, Items.POTATO, Items.CARROT, Items.RED_MUSHROOM, Items.BROWN_MUSHROOM, Items.CRIMSON_FUNGUS, Items.WARPED_FUNGUS);

//        tag(ModTags.Items.CRAB_FOOD)
//                .add(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "raw_shrimp")))
//                .add(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "mussel")))
//                .add(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "raw_anchovy")));

        for (CropConfig crop: FarmingRegistrator.HERBS) {
            var herbTag = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "herbs/" + crop.name()));
            tag(herbTag).add(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name())));
            tag(ModTags.Items.HERBS).addTag(herbTag);
            addSeed(crop);
            var tag = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", crop.name()));
            tag(tag).add(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name())));
        }
        for (CropConfig crop: FarmingRegistrator.BERRIES) {
            var tag = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "berries/" + crop.name()));
            tag(tag).add(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name())));
            tag(ModTags.Items.BERRIES).addTag(tag);
            addSeed(crop);
        }
        for (CropConfig crop: FarmingRegistrator.CROPS) {
            var tag = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", crop.name()));
            tag(tag).add(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name())));
            addSeed(crop);
        }
        for (CropConfig crop: FarmingRegistrator.TRELLIS) {
            var tag = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", crop.name()));
            tag(tag).add(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name())));
            addSeed(crop);
            tag(ModTags.Items.FENCEPOST_CROP).add(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name())));
        }
        for (CropConfig crop: FarmingRegistrator.VERTICAL_TRELLIS) {
            var tag = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", crop.name()));
            tag(tag).add(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name())));
            addSeed(crop);
            tag(ModTags.Items.FENCEPOST_CROP).add(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name())));
        }
        for (CropConfig crop: FarmingRegistrator.GRAPES) {
            var tag = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", crop.name()));
            tag(tag).add(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name())));
            addSeed(crop);
            tag(ModTags.Items.FENCEPOST_CROP).add(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name())));
        }
        for (CropConfig crop: FarmingRegistrator.STEMS) {
            var tag = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", crop.name()));
            tag(tag).add(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name())));
            addSeed(crop);
        }
        for (CropConfig crop: FarmingRegistrator.SHROOMS) {
            tag(ModTags.Items.MUSHROOMS).add(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name())));
        }
        for (FishConfig fish: FarmingRegistrator.FISHIES) {
            var tag = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", fish.name()));
            if (fish.hasBlock()) {
                tag(tag).add(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, fish.name())));
            } else {
                tag(tag).add(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "raw_" + fish.name())));
            }
            tag(ModTags.Items.FISHES).addTag(tag);
        }

        FarmingRegistrator.CRATED_CROPS.forEach(resourceLocation -> {
            var bTagKey = BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/" + resourceLocation.getPath()));
            var tagKey = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/" + resourceLocation.getPath()));
            copy(bTagKey, tagKey);
            tag(Tags.Items.STORAGE_BLOCKS).addTag(tagKey);
        });

        FarmingRegistrator.SEED_BAGS.forEach(resourceLocation -> {
            var tagKey = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/" + resourceLocation.getPath()));
            tag(tagKey).add(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, resourceLocation.withPath(p -> p + "_bag").getPath())));
            tag(Tags.Items.STORAGE_BLOCKS).addTag(tagKey);
        });
    }

    private void addSeed(CropConfig crop) {
        if (crop.hasSeed()) {
            var seedTag = tag((Tags.Items.SEEDS));
            var cropSeedTag = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "seeds/" + crop.name()));
            tag(cropSeedTag).add(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name() + "_seeds")));
            tag(ItemTags.VILLAGER_PLANTABLE_SEEDS).addTag(cropSeedTag);
            seedTag.addTag(cropSeedTag);
        }
    }

    @Override
    public String getName() {
        return "Productive Farming Item Tags Provider";
    }
}
