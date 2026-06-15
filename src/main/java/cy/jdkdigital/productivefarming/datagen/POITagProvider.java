package cy.jdkdigital.productivefarming.datagen;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.PoiTypeTagsProvider;

import java.util.concurrent.CompletableFuture;

public class POITagProvider extends PoiTypeTagsProvider
{
    public POITagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> future) {
        super(output, future, ProductiveFarming.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
//        tag(ModTags.SALT_LICK_POI_TAG).add(FarmingRegistrator.SALT_LICK_POI.getKey());
    }

    @Override
    public String getName() {
        return "Productive Farming POI Type Tag Provider";
    }
}
