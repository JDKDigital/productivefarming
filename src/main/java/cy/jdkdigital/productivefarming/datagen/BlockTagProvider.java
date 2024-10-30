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

import java.util.concurrent.CompletableFuture;

public class BlockTagProvider extends BlockTagsProvider
{
    public BlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, ExistingFileHelper helper) {
        super(output, provider, ProductiveFarming.MODID, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModTags.FARMLAND).add(Blocks.FARMLAND);
        tag(ModTags.FARM_BLOCKS).add(Blocks.STONE_BRICKS, FarmingRegistrator.FARM_CONTROLLER.get(), FarmingRegistrator.FARM_BLOCK.get(), FarmingRegistrator.FARM_HATCH.get());

        tag(BlockTags.create(ResourceLocation.parse("utilitarian:farmland_cansurvive"))).addTag(Tags.Blocks.FENCES);

        FarmingRegistrator.CROPS.forEach(crop -> {
            var block = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, crop.name()));
            if (block instanceof VineLeafBlock) {
                tag(BlockTags.FENCES).add(block);
            }
        });

        FarmingRegistrator.FISHIES.forEach(fishConfig -> {
            if (fishConfig.hasBlock()) {
                tag(ModTags.FARMABLE_FISH_BLOCKS).add(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, fishConfig.name())));
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
