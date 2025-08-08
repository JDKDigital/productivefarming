package cy.jdkdigital.productivefarming.util;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.registry.FarmingDataComponents;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class TraitsHelper
{
    public static final String GROWTH = "growth"; // the speed at which the plant grows, affects random tick growth and when applying bonemeal
    public static final String YIELD = "yield"; // how many crops you get when harvested
    public static final String RESISTANCE = "resistance"; // increases chance of dropping seeds with improved stats
    public static final String MUTABILITY = "mutability"; // affects how easily the crop can be mutated, max stat can make the crop mutate spontaneously

    static Map<String, List<String>> TRAIT_VALUES = new HashMap<>() {{
        put(GROWTH, List.of("low", "medium", "high", "very_high"));
        put(YIELD, List.of("low", "medium", "high", "very_high"));
        put(RESISTANCE, List.of("none", "low", "medium", "high", "very_high"));
        put(MUTABILITY, List.of("none", "low", "medium", "high", "very_high"));
    }};

    public static Component getValueName(String trait, int value) {
        if (!TRAIT_VALUES.containsKey(trait) || TRAIT_VALUES.get(trait).size() <= value) {
            return Component.literal("Invalid trait value " + value + " for " + trait);
        }
        return Component.translatable(ProductiveFarming.MODID + ".trait_value." + TRAIT_VALUES.get(trait).get(value)).withStyle(getStyle(trait, value));
    }

    private static ChatFormatting getStyle(String trait, int value) {
        return switch (TRAIT_VALUES.get(trait).get(value)) {
            case "low" -> ChatFormatting.GREEN;
            case "medium" -> ChatFormatting.BLUE;
            case "high" -> ChatFormatting.LIGHT_PURPLE;
            case "very_high" -> ChatFormatting.RED;
            default -> ChatFormatting.DARK_GRAY;
        };
    }

    public static int getMaxValue(String trait) {
        if (TRAIT_VALUES.containsKey(trait)) {
            return TRAIT_VALUES.get(trait).size() - 1;
        }
        return 0;
    }

    public static void setDefaultsOnItem(ItemStack stack) {
        stack.set(FarmingDataComponents.GROWTH, getDefaultTrait(stack.getItemHolder(), FarmingDataComponents.GROWTH));
        stack.set(FarmingDataComponents.YIELD, getDefaultTrait(stack.getItemHolder(), FarmingDataComponents.YIELD));
        stack.set(FarmingDataComponents.RESISTANCE, getDefaultTrait(stack.getItemHolder(), FarmingDataComponents.RESISTANCE));
        stack.set(FarmingDataComponents.MUTABILITY, getDefaultTrait(stack.getItemHolder(), FarmingDataComponents.MUTABILITY));
    }

    public static int getDefaultTrait(Holder<Item> item, Supplier<DataComponentType<Integer>> trait) {
        var data = item.getData(FarmingRegistrator.CROP_TRAITS);
        if (data != null) {
            String traitName = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(trait.get()).getPath();
            return switch (traitName) {
                case GROWTH -> data.growth();
                case YIELD -> data.yield();
                case RESISTANCE -> data.resistance();
                case MUTABILITY -> data.mutability();
                default ->
                        throw new IllegalStateException("Unexpected value: " + traitName);
            };
        }
        return 0;
    }
}
