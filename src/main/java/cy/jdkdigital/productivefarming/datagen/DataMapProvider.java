package cy.jdkdigital.productivefarming.datagen;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.common.datamap.CropTrait;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.datamaps.builtin.Compostable;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

import java.util.concurrent.CompletableFuture;

public class DataMapProvider extends net.neoforged.neoforge.common.data.DataMapProvider
{
    protected DataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        final var traits = builder(FarmingRegistrator.CROP_TRAITS);

        traits.add(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "blue_jade_corn"), new CropTrait(0, 2, 0, 0), false);

        final var compostables = builder(NeoForgeDataMaps.COMPOSTABLES);
        ProductiveFarming.ITEMS.getEntries().forEach(holder -> {
            var stack = holder.get().getDefaultInstance();
            if (stack.getFoodProperties(null) != null) {
                compostables.add(holder, new Compostable(0.65f, false), false);
            } else if (stack.is(Tags.Items.SEEDS)) {
                compostables.add(holder, new Compostable(0.3f, true), false);
            }
        });
    }
}
