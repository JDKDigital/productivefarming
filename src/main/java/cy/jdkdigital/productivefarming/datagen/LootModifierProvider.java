package cy.jdkdigital.productivefarming.datagen;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import cy.jdkdigital.productivelib.loot.ItemLootModifier;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
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
//        List<WeightedIngredientModifier.WeightedIngredient> list = FarmingRegistrator.FISHIES.stream().filter(fishConfig -> !fishConfig.hasBlock()).map(fishConfig -> new WeightedIngredientModifier.WeightedIngredient(Ingredient.of(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "raw_" + fishConfig.name()))), weights.getOrDefault(fishConfig.name(), 25))).toList();
//        add("fishing", new WeightedIngredientModifier(anyOfConditions( "gameplay/fishing/fish", "gameplay/fishing"), list, 0.6f, true));

        add("pipe", new ItemLootModifier(anyOfConditions( "chests/village/village_cartographer", "chests/village/village_shepherd"), new ItemStack(FarmingRegistrator.CORN_COB_PIPE.get()), 0.05f));
    }

    private LootItemCondition[] lootTableConditions(String... rLoc) {
        var list = new ArrayList<LootItemCondition>();
        for (String s : rLoc) {
            list.add(LootTableIdCondition.builder(ResourceLocation.parse(s)).build());
        }
        return list.toArray(new LootItemCondition[0]);
    }

    private LootItemCondition[] anyOfConditions(String... rLoc) {
        var list = new ArrayList<LootItemCondition.Builder>();
        for (String s : rLoc) {
            list.add(LootTableIdCondition.builder(ResourceLocation.parse(s)));
        }
        return List.of(AnyOfCondition.anyOf(list.toArray(new LootItemCondition.Builder[0])).build()).toArray(new LootItemCondition[0]);
    }
}
