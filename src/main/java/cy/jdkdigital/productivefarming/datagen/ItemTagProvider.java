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
        tag(ModTags.FERTILIZERS).add(Items.BONE_MEAL);

        tag(ModTags.CRAB_FOOD)
                .add(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "shrimp")))
                .add(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "mussel")))
                .add(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "anchovy")));

        for (CropConfig crop: FarmingRegistrator.HERBS) {
            var tag = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "herbs/" + crop.name()));
            tag(tag).add(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name())));
            tag(ModTags.HERBS).addTag(tag);

        }
        for (CropConfig crop: FarmingRegistrator.BERRIES) {
            var tag = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "berries/" + crop.name()));
            tag(tag).add(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name())));
            tag(ModTags.BERRIES).addTag(tag);
        }
        for (CropConfig crop: FarmingRegistrator.CROPS) {
            var tag = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", crop.name()));
            tag(tag).add(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name())));
            if (crop.hasSeed()) {
                var seedTag = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "seeds/" + crop.name()));
                tag(seedTag).add(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name() + "_seeds")));
                tag(ItemTags.VILLAGER_PLANTABLE_SEEDS).addTag(seedTag);
                tag(Tags.Items.SEEDS).addTag(seedTag);
            }
        }

        for (FishConfig fish: FarmingRegistrator.FISHIES) {
            var tag = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", fish.name()));
            if (fish.hasBlock()) {
                tag(tag).add(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, fish.name())));
            } else {
                tag(tag).add(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "raw_" + fish.name())));
            }
            tag(ModTags.FISHES).addTag(tag);
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

    @Override
    public String getName() {
        return "Productive Farming Item Tags Provider";
    }
}
