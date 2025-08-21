package cy.jdkdigital.productivefarming.datagen;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import net.minecraft.core.Cloner;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = ProductiveFarming.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ProductiveFarmingDataProvider
{
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput output = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> provider = CompletableFuture.supplyAsync(() -> getProvider().full());
        ExistingFileHelper helper = event.getExistingFileHelper();

        gen.addProvider(event.includeClient(), new LanguageProvider(output));

        gen.addProvider(event.includeClient(), new ModelProvider(output));

        gen.addProvider(event.includeServer(), new LootDataProvider(output, List.of(new LootTableProvider.SubProviderEntry(LootDataProvider.LootProvider::new, LootContextParamSets.BLOCK)), provider));
        gen.addProvider(event.includeServer(), new RecipeProvider(output, provider));
        gen.addProvider(event.includeServer(), new FeatureProvider(output, FeatureProvider.getBuilder(), provider));

        BlockTagProvider blockTags = new BlockTagProvider(output, provider, helper);
        gen.addProvider(event.includeServer(), blockTags);
        gen.addProvider(event.includeServer(), new ItemTagProvider(output, provider, blockTags.contentsGetter(), helper));
        gen.addProvider(event.includeServer(), new EntityTypeTagProvider(output, provider));
        gen.addProvider(event.includeServer(), new BiomeTagProvider(output, provider, helper));
        gen.addProvider(event.includeServer(), new POITagProvider(output, provider, helper));
        gen.addProvider(event.includeServer(), new LootModifierProvider(output, provider));
        gen.addProvider(event.includeServer(), new DataMapProvider(output, provider));
    }

    private static RegistrySetBuilder.PatchedRegistries getProvider() {
        final RegistrySetBuilder registryBuilder = new RegistrySetBuilder();

        registryBuilder.add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, BiomeModifierDataProvider::bootstrap);
        registryBuilder.add(Registries.BIOME, $ -> {});

        RegistryAccess.Frozen regAccess = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
        Cloner.Factory cloner$factory = new Cloner.Factory();

        net.neoforged.neoforge.registries.DataPackRegistriesHooks.getDataPackRegistriesWithDimensions().forEach(data -> data.runWithArguments(cloner$factory::addCodec));

        return registryBuilder.buildPatch(regAccess, VanillaRegistries.createLookup(), cloner$factory);
    }
}
