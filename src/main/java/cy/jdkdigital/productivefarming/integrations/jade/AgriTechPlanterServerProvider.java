package cy.jdkdigital.productivefarming.integrations.jade;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.registry.FarmingDataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.fml.ModList;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.StreamServerDataProvider;

public class AgriTechPlanterServerProvider implements StreamServerDataProvider<BlockAccessor, CropServerProvider.Data>
{
    public static final Identifier UID = Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "agritech_planter");
    public static final AgriTechPlanterServerProvider INSTANCE = new AgriTechPlanterServerProvider();

    @Override
    public boolean shouldRequestData(BlockAccessor accessor) {
        return !readSeed(accessor.getBlockEntity()).isEmpty();
    }

    @Override
    public CropServerProvider.Data streamData(BlockAccessor accessor) {
        ItemStack seed = readSeed(accessor.getBlockEntity());
        if (seed.isEmpty() || !hasTraits(seed)) {
            return empty();
        }
        return new CropServerProvider.Data(
                seed.getOrDefault(FarmingDataComponents.GROWTH, 0),
                seed.getOrDefault(FarmingDataComponents.YIELD, 0),
                seed.getOrDefault(FarmingDataComponents.RESISTANCE, 0),
                seed.getOrDefault(FarmingDataComponents.MUTABILITY, 0),
                false,
                Identifier.parse(""));
    }

    private static ItemStack readSeed(BlockEntity blockEntity) {
        if (ModList.get().isLoaded("agritechtwo")) {
            ItemStack seed = AgriTechTwoPlanterReader.readSeed(blockEntity);
            if (!seed.isEmpty()) {
                return seed;
            }
        }
        if (ModList.get().isLoaded("agritechevolved")) {
            ItemStack seed = AgriTechEvolvedPlanterReader.readSeed(blockEntity);
            if (!seed.isEmpty()) {
                return seed;
            }
        }
        return ItemStack.EMPTY;
    }

    private static CropServerProvider.Data empty() {
        return new CropServerProvider.Data(-1, 0, 0, 0, false, Identifier.parse(""));
    }

    private static boolean hasTraits(ItemStack seed) {
        return seed.has(FarmingDataComponents.GROWTH)
                || seed.has(FarmingDataComponents.YIELD)
                || seed.has(FarmingDataComponents.RESISTANCE)
                || seed.has(FarmingDataComponents.MUTABILITY);
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, CropServerProvider.Data> streamCodec() {
        return CropServerProvider.Data.STREAM_CODEC;
    }

    @Override
    public Identifier getUid() {
        return UID;
    }
}
