package cy.jdkdigital.productivefarming.integrations.botanypots;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.integrations.botanypots.itemdrops.ProductiveDropProvider;
import net.darkhax.botanypots.common.api.BotanyPotsPlugin;
import net.darkhax.botanypots.common.api.data.itemdrops.ItemDropProviderType;
import net.minecraft.resources.Identifier;

public class BotanyPotsCompat implements BotanyPotsPlugin
{
    @Override
    public void registerDropProviders() {
        ItemDropProviderType.register(
                Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "productive_drop"),
                ProductiveDropProvider.CODEC, ProductiveDropProvider.STREAM
        );
    }
}
