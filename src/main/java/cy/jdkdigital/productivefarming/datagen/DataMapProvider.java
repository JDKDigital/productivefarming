package cy.jdkdigital.productivefarming.datagen;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.common.datamap.CropTrait;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.CompletableFuture;

public class DataMapProvider extends net.neoforged.neoforge.common.data.DataMapProvider
{
    protected DataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        final var fuels = builder(FarmingRegistrator.CROP_TRAITS);

        fuels.add(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "blue_jade_corn"), new CropTrait(0, 2, 0, 0), false);
    }
}
