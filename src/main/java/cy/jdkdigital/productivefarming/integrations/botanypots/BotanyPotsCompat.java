package cy.jdkdigital.productivefarming.integrations.botanypots;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.integrations.botanypots.itemdrops.ProductiveDropProvider;
import net.darkhax.botanypots.common.api.data.itemdrops.ItemDropProviderType;
import net.minecraft.resources.ResourceLocation;

public class BotanyPotsCompat
{
    public static final ItemDropProviderType<ProductiveDropProvider> PRODUCTIVE_PROVIDER_TYPE = ItemDropProviderType.register(
            ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "productive_drop"),
            ProductiveDropProvider.CODEC, ProductiveDropProvider.STREAM
    );
}
