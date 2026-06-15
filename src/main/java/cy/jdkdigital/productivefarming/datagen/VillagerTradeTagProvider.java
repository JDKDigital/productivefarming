package cy.jdkdigital.productivefarming.datagen;

import cy.jdkdigital.productivefarming.registry.ModTrades;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.trading.VillagerTrade;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class VillagerTradeTagProvider extends TagsProvider<VillagerTrade>
{
    public VillagerTradeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Registries.VILLAGER_TRADE, lookupProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        appendTier("level_1", ModTrades.level1());
        appendTier("level_2", ModTrades.level2());
        appendTier("level_3", ModTrades.level3());
        appendTier("level_4", ModTrades.level4());
    }

    private void appendTier(String level, List<ModTrades.SeedTrade> trades) {
        TagKey<VillagerTrade> tag = TagKey.create(Registries.VILLAGER_TRADE, Identifier.withDefaultNamespace("farmer/" + level));
        for (ModTrades.SeedTrade trade : trades) {
            getOrCreateRawBuilder(tag).addOptionalElement(trade.key().identifier());
        }
    }
}
