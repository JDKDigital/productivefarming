package cy.jdkdigital.productivefarming.util;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.registry.FarmingDataComponents;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

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

    public static final String[] STAT_NAMES = {GROWTH, YIELD, RESISTANCE, MUTABILITY};

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

    public static void setDefaultsOnStack(ItemStack stack) {
        stack.set(FarmingDataComponents.GROWTH, getDefaultTrait(stack.typeHolder(), FarmingDataComponents.GROWTH));
        stack.set(FarmingDataComponents.YIELD, getDefaultTrait(stack.typeHolder(), FarmingDataComponents.YIELD));
        stack.set(FarmingDataComponents.RESISTANCE, getDefaultTrait(stack.typeHolder(), FarmingDataComponents.RESISTANCE));
        stack.set(FarmingDataComponents.MUTABILITY, getDefaultTrait(stack.typeHolder(), FarmingDataComponents.MUTABILITY));
    }

    public static void copyTraitsToStack(ItemStack in, ItemStack stack) {
        stack.set(FarmingDataComponents.GROWTH, in.getOrDefault(FarmingDataComponents.GROWTH, 0));
        stack.set(FarmingDataComponents.YIELD, in.getOrDefault(FarmingDataComponents.YIELD, 0));
        stack.set(FarmingDataComponents.RESISTANCE, in.getOrDefault(FarmingDataComponents.RESISTANCE, 0));
        stack.set(FarmingDataComponents.MUTABILITY, in.getOrDefault(FarmingDataComponents.MUTABILITY, 0));
    }

    public static String rollIncreasedStat(RandomSource random, Holder<Item> seed) {
        if (random.nextFloat() >= getIncreaseChance(seed)) {
            return null;
        }
        return STAT_NAMES[random.nextInt(STAT_NAMES.length)];
    }

    public static double getIncreaseChance(Holder<Item> seed) {
        Double chance = seed == null ? null : seed.getData(FarmingRegistrator.STAT_INCREASE_CHANCE);
        return chance != null ? chance : 0.0;
    }

    public static ItemStack applyTraits(ItemStack stack, int growth, int yield, int resistance, int mutability) {
        stack.set(FarmingDataComponents.GROWTH, growth);
        stack.set(FarmingDataComponents.YIELD, yield);
        stack.set(FarmingDataComponents.RESISTANCE, resistance);
        stack.set(FarmingDataComponents.MUTABILITY, mutability);
        return stack;
    }

    public static ItemStack mutatedSeed(Identifier mutation) {
        ItemStack seed = BuiltInRegistries.ITEM.getValue(mutation.withPath(p -> p + "_seeds")).getDefaultInstance();
        if (seed.is(Items.AIR)) {
            seed = BuiltInRegistries.ITEM.getValue(mutation).getDefaultInstance();
        }
        return seed;
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
