package cy.jdkdigital.productivefarming.datagen;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.datagen.compat.AgriTechRecipeProvider;
import cy.jdkdigital.productivefarming.gametest.GameTestStructureProvider;
import cy.jdkdigital.productivefarming.gametest.TestEntriesProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = ProductiveFarming.MODID)
public class ProductiveFarmingDataProvider
{
    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();
        CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();

        gen.addProvider(true, new LanguageProvider(output));

        gen.addProvider(true, new ModelProvider(output));

        gen.addProvider(true, new LootDataProvider(output, List.of(new LootTableProvider.SubProviderEntry(LootDataProvider.LootProvider::new, LootContextParamSets.BLOCK)), provider));
        gen.addProvider(true, new RecipeProvider.Runner(output, provider));
        gen.addProvider(true, new AgriTechRecipeProvider.Runner(output, provider));

        FeatureProvider features = new FeatureProvider(output, FeatureProvider.getBuilder(), event.getLookupProvider());
        gen.addProvider(true, features);

        BlockTagProvider blockTags = new BlockTagProvider(output, provider);
        gen.addProvider(true, blockTags);
        gen.addProvider(true, new ItemTagProvider(output, provider, blockTags.contentsGetter()));
        gen.addProvider(true, new EntityTypeTagProvider(output, provider));
        gen.addProvider(true, new BiomeTagProvider(output, provider));
        gen.addProvider(true, new POITagProvider(output, provider));
        gen.addProvider(true, new VillagerTradeTagProvider(output, provider));
        gen.addProvider(true, new LootModifierProvider(output, provider));
        gen.addProvider(true, new DataMapProvider(output, provider));

        gen.addProvider(true, new GameTestStructureProvider(output));
        gen.addProvider(true, new TestEntriesProvider(output));
    }
}
