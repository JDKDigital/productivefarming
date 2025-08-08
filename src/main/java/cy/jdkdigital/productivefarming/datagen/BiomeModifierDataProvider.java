package cy.jdkdigital.productivefarming.datagen;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.init.ModTags;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.biome.BiomeData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;

public class BiomeModifierDataProvider
{
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder().add(Registries.BIOME, BiomeData::bootstrap);

    public static final ResourceKey<BiomeModifier> ADD_AMARYLLIS = ResourceKey.create(
            NeoForgeRegistries.Keys.BIOME_MODIFIERS,
            ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "add_amaryllis")
    );

    public static void bootstrap(BootstrapContext<BiomeModifier> context) {
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);

//        context.register(ADD_AMARYLLIS,
//                new BiomeModifiers.AddFeaturesBiomeModifier(
//                        biomes.getOrThrow(ModTags.BEEBEE_SPAWN_BIOMES),
//                        List.of(
//                                new (ModEntities.CONFIGURABLE_BEE.get(), 10, 1, 1)
//                        )
//                )
//        );
    }
}
