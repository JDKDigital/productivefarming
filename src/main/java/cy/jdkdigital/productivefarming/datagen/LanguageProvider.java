package cy.jdkdigital.productivefarming.datagen;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.MobBucketItem;

public class LanguageProvider extends net.neoforged.neoforge.common.data.LanguageProvider
{
    public LanguageProvider(PackOutput output) {
        super(output, ProductiveFarming.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("itemGroup." + ProductiveFarming.MODID, "Productive Farming");
        add("jei." + ProductiveFarming.MODID + ".crop_fruiting", "Crop Fruiting");
        add(ProductiveFarming.MODID + ".screen.progress", "Progress: %s");
        add(ProductiveFarming.MODID + ".message.farm_formed", "Farm structure assembled");
        add(ProductiveFarming.MODID + ".message.farm_invalid", "Farm structure invalid. %s");

        add("block." + ProductiveFarming.MODID + ".nutrient_water", "Nutrient Water");

        ProductiveFarming.BLOCKS.getEntries().forEach(itemRegistryObject -> {
            add(itemRegistryObject.get(), capName(BuiltInRegistries.BLOCK.getKey(itemRegistryObject.get()).getPath()));
        });
        ProductiveFarming.ITEMS.getEntries().forEach(itemRegistryObject -> {
            if (itemRegistryObject.get() instanceof MobBucketItem) {
                add(itemRegistryObject.get(), "Bucket of " + capName(BuiltInRegistries.ITEM.getKey(itemRegistryObject.get()).getPath().replace("_bucket", "")));
            } else if (!(itemRegistryObject.get() instanceof BlockItem) || itemRegistryObject.get() instanceof ItemNameBlockItem) {
                add(itemRegistryObject.get(), capName(BuiltInRegistries.ITEM.getKey(itemRegistryObject.get()).getPath()));
            }
        });
        ProductiveFarming.ENTITY_TYPES.getEntries().forEach(entityTypeRegistryObject -> {
            add(entityTypeRegistryObject.get(), capName(BuiltInRegistries.ENTITY_TYPE.getKey(entityTypeRegistryObject.get()).getPath()));
        });
    }

    @Override
    public String getName() {
        return "Productive Farming translation provider";
    }

    private String capName(String name) {
        String[] nameParts = name.split("_");

        for (int i = 0; i < nameParts.length; i++) {
            nameParts[i] = nameParts[i].substring(0, 1).toUpperCase() + nameParts[i].substring(1);
        }

        return String.join(" ", nameParts);
    }
}
