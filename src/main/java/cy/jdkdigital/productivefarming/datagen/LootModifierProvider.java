package cy.jdkdigital.productivefarming.datagen;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.loot.CropTraitsLootModifier;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import cy.jdkdigital.productivelib.loot.ItemLootModifier;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LootModifierProvider extends GlobalLootModifierProvider
{
    public LootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, ProductiveFarming.MODID);
    }

    @Override
    protected void start() {
//        Map<String, Integer> weights = new HashMap<>() {{
//            put("anchovy", 35);
//            put("sturgeon", 10);
//            put("tuna", 5);
//            put("koi", 0);
//        }};
//        add("fishing", new WeightedIngredientModifier(anyOfConditions( "gameplay/fishing/fish", "gameplay/fishing"), list, 0.6f, true));

        add("pipe", new ItemLootModifier(anyOfConditions( "chests/village/village_cartographer", "chests/village/village_shepherd"), 0, new ItemStackTemplate(FarmingRegistrator.CORN_COB_PIPE.get()), 0.05f));

        add("external_crop_traits", new CropTraitsLootModifier(new LootItemCondition[0], 0));
    }

    private LootItemCondition[] lootTableConditions(String... rLoc) {
        var list = new ArrayList<LootItemCondition>();
        for (String s : rLoc) {
            list.add(LootTableIdCondition.builder(Identifier.parse(s)).build());
        }
        return list.toArray(new LootItemCondition[0]);
    }

    private LootItemCondition[] anyOfConditions(String... rLoc) {
        var list = new ArrayList<LootItemCondition.Builder>();
        for (String s : rLoc) {
            list.add(LootTableIdCondition.builder(Identifier.parse(s)));
        }
        return List.of(AnyOfCondition.anyOf(list.toArray(new LootItemCondition.Builder[0])).build()).toArray(new LootItemCondition[0]);
    }
}
