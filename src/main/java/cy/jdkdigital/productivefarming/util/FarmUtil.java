package cy.jdkdigital.productivefarming.util;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import cy.jdkdigital.productivefarming.Config;
import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.common.block.entity.ColorfulFlowerBlockEntity;
import cy.jdkdigital.productivefarming.common.block.entity.CropBlockEntity;
import cy.jdkdigital.productivefarming.recipe.CropMutationRecipe;
import cy.jdkdigital.productivefarming.registry.FarmingDataComponents;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import cy.jdkdigital.productivefarming.registry.ModTags;
import cy.jdkdigital.productivelib.util.ColorUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.TallFlowerBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.event.EventHooks;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class FarmUtil
{
    static final UUID PLAYER_UUID = UUID.nameUUIDFromBytes("productive_farmer".getBytes(StandardCharsets.UTF_8));

    // TODO move to datamap
    public static Map<Identifier, Integer> VANILLA_FLOWER_COLORS = new HashMap<>() {{
        put(Identifier.parse("minecraft:rose_bush"), 0xffff4540);
        put(Identifier.parse("minecraft:lilac"), 0xffde93f1);
        put(Identifier.parse("minecraft:peony"), 0xffde93f1);
        put(Identifier.parse("minecraft:poppy"), 0xffff4540);
        put(Identifier.parse("minecraft:cornflower"), 0xff728ff1);
        put(Identifier.parse("minecraft:alium"), 0xffe8cffe);
        put(Identifier.parse("minecraft:azure_bluet"), 0xfff7f7f7);
        put(Identifier.parse("minecraft:blue_orchid"), 0xff2fcefd);
        put(Identifier.parse("minecraft:dandelion"), 0xffffec4f);
        put(Identifier.parse("minecraft:lily_of_the_valley"), 0xffffffff);
        put(Identifier.parse("minecraft:oxeye_daisy"), 0xffffffff);
        put(Identifier.parse("minecraft:white_tulip"), 0xfff7f7f7);
        put(Identifier.parse("minecraft:red_tulip"), 0xffff4540);
        put(Identifier.parse("minecraft:pink_tulip"), 0xfff6e2ff);
        put(Identifier.parse("minecraft:orange_tulip"), 0xfff19d25);
        put(Identifier.parse("minecraft:torchflower"), 0xfffce257);
        put(Identifier.parse("minecraft:wither_rose"), 0xff42352e);
    }};
    // TODO move to lib?
    public static Map<Integer, Identifier> DYE_COLORS = new HashMap<>() {{
        put(-6447721, Identifier.parse("minecraft:light_gray_dye"));
        put(-12103854, Identifier.parse("minecraft:gray_dye"));
        put(-393218, Identifier.parse("minecraft:white_dye"));
        put(-3715395, Identifier.parse("minecraft:magenta_dye"));
        put(-14869215, Identifier.parse("minecraft:black_dye"));
        put(-8337633, Identifier.parse("minecraft:lime_dye"));
        put(-425955, Identifier.parse("minecraft:orange_dye"));
        put(-12930086, Identifier.parse("minecraft:light_blue_dye"));
        put(-816214, Identifier.parse("minecraft:pink_dye"));
        put(-8170446, Identifier.parse("minecraft:brown_dye"));
        put(-10585066, Identifier.parse("minecraft:green_dye"));
        put(-15295332, Identifier.parse("minecraft:cyan_dye"));
        put(-12827478, Identifier.parse("minecraft:blue_dye"));
        put(-7785800, Identifier.parse("minecraft:purple_dye"));
        put(-47808, Identifier.parse("minecraft:red_dye"));
        put(-75715, Identifier.parse("minecraft:yellow_dye"));
    }};
    public static Map<Integer, Identifier> DYENAMICS_DYE_COLORS = new HashMap<>() {{
        put(-2997991, Identifier.parse("dyenamics:persimmon_dye"));
        put(-19386, Identifier.parse("dyenamics:honey_dye"));
        put(-13861505, Identifier.parse("dyenamics:aquamarine_dye"));
        put(-560698, Identifier.parse("dyenamics:bubblegum_dye"));
        put(-16672561, Identifier.parse("dyenamics:cherenkov_dye"));
        put(-7345409, Identifier.parse("dyenamics:icy_blue_dye"));
        put(-4220813, Identifier.parse("dyenamics:peach_dye"));
        put(-1119818, Identifier.parse("dyenamics:fluorescent_dye"));
        put(-4531627, Identifier.parse("dyenamics:conifer_dye"));
        put(-9298341, Identifier.parse("dyenamics:wine_dye"));
        put(-11122177, Identifier.parse("dyenamics:ultramarine_dye"));
        put(-55195, Identifier.parse("dyenamics:rose_dye"));
        put(-7739208, Identifier.parse("dyenamics:mint_dye"));
        put(-14931855, Identifier.parse("dyenamics:navy_dye"));
        put(-2919655, Identifier.parse("dyenamics:amber_dye"));
        put(-3932263, Identifier.parse("dyenamics:spring_green_dye"));
        put(-7274496, Identifier.parse("dyenamics:maroon_dye"));
        put(-2254337, Identifier.parse("dyenamics:lavender_dye"));
    }};

    public static String getLatinTranslationKey(Item item) {
        var key = BuiltInRegistries.ITEM.getKey(item).getPath().replace("_seeds", "").replace("_stem", "");
        return "tooltip." + ProductiveFarming.MODID + "." + key + ".latin";
    }

    public static void pollinateCrops(ServerLevel level, BlockPos pos, int distance, boolean isSpecialPollinator, List<Identifier> uniqueCrops) {
        List<BlockPos> crops = BlockPos.betweenClosedStream(pos.offset(-distance, -distance, -distance), pos.offset(distance, distance, distance)).map(BlockPos::immutable).toList();
        // Build permutation map
        Map<Identifier, BlockPos> flowerMap = new HashMap<>();
        Map<Identifier, BlockPos> cropMap = new HashMap<>();
        crops.forEach(blockPos -> {
            var state = level.getBlockState(blockPos);
            if (state.is(ModTags.Blocks.POLLINATABLE)) {
                var stateKey = BuiltInRegistries.BLOCK.getKey(state.getBlock());
                cropMap.put(stateKey, blockPos);
                if (state.is(BlockTags.FLOWERS)) {
                    flowerMap.put(stateKey, blockPos);
                }
                if (!uniqueCrops.contains(stateKey)) {
                    uniqueCrops.add(stateKey);
                }
            }
        });

        if (!uniqueCrops.isEmpty()) {
            // Propagate flowers
            if (level.getRandom().nextFloat() < Config.SERVER.flowerPropagationChance.get() && flowerMap.size() > 1) {
                // Grab two random flowers
                ArrayList<Identifier> keys = new ArrayList<>(flowerMap.keySet());
                Collections.shuffle(keys);
                List<Identifier> randomFlowers = keys.subList(0, 2);
                // Find the colors of the two flowers
                int colorA = VANILLA_FLOWER_COLORS.getOrDefault(randomFlowers.getFirst(), 0);
                if (level.getBlockEntity(flowerMap.get(randomFlowers.getFirst())) instanceof ColorfulFlowerBlockEntity flowerBlockEntity) {
                    colorA = flowerBlockEntity.getColor();
                }
                int colorB = VANILLA_FLOWER_COLORS.getOrDefault(randomFlowers.getLast(), 0);
                if (level.getBlockEntity(flowerMap.get(randomFlowers.getLast())) instanceof ColorfulFlowerBlockEntity flowerBlockEntity) {
                    colorB = flowerBlockEntity.getColor();
                }
                if (colorA == 0) {
                    ProductiveFarming.LOGGER.debug("missing flower color for " + randomFlowers.getFirst());
                }
                if (colorB == 0) {
                    ProductiveFarming.LOGGER.debug("missing flower color for " + randomFlowers.getLast());
                }
                // If we have valid mixable flowers, grab PFarming equivalent of the first flower
                if (colorA != 0 && colorB != 0) {
                    String pBeeFlower = randomFlowers.getFirst().getPath()
                            .replace("red_tulip", "tulip")
                            .replace("orange_tulip", "tulip")
                            .replace("white_tulip", "tulip")
                            .replace("pink_tulip", "tulip");
                    var newFlowerBlock = BuiltInRegistries.BLOCK.get(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, pBeeFlower)).map(Holder::value).orElse(null);
                    var newFlower = newFlowerBlock == null ? null : newFlowerBlock.defaultBlockState();
                    if (newFlower != null && newFlower.is(BlockTags.FLOWERS)) {
                        Player fakePlayer = FakePlayerFactory.get(level, new GameProfile(PLAYER_UUID, "flower_master"));

                        // Find valid block to place it on
                        var list = BlockPos.betweenClosedStream(pos.offset(-distance, -distance + 2, -distance), pos.offset(distance, distance - 2, distance)).map(BlockPos::immutable).collect(Collectors.toList());
                        Collections.shuffle(list);
                        for (BlockPos blockPos : list) {
                            if (level.getBlockState(blockPos).canBeReplaced() && (!(newFlower.getBlock() instanceof TallFlowerBlock) || level.getBlockState(blockPos.above()).canBeReplaced()) && newFlower.canSurvive(level, blockPos) && !EventHooks.onBlockPlace(fakePlayer, BlockSnapshot.create(level.dimension(), level, blockPos), fakePlayer.getDirection())) {
                                if (level.setBlock(blockPos, newFlower, CropBlock.UPDATE_ALL_IMMEDIATE)) {
                                    // Set color
                                    if (level.getBlockEntity(blockPos) instanceof ColorfulFlowerBlockEntity flowerBlockEntity) {
                                        flowerBlockEntity.setColor(ColorUtil.blend(colorA, colorB, Mth.lerp(level.getRandom().nextFloat(), 0.3f, 0.7f)));
                                        // for double flower
                                        if (newFlower.getBlock() instanceof TallFlowerBlock) {
                                            level.setBlockAndUpdate(blockPos.above(), newFlower.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER));
                                            if (level.getBlockEntity(blockPos.above()) instanceof ColorfulFlowerBlockEntity flowerBlockEntityAbove) {
                                                flowerBlockEntityAbove.setColor(flowerBlockEntity.getColor());
                                            }
                                        }
                                    }
                                    return;
                                }
                            }
                        }
                    }
                }
            }

            // Pollinate crops
            Map<RecipeHolder<CropMutationRecipe>, Pair<Identifier, Identifier>> matchedRecipes = new HashMap<>();
            var allRecipes = level.recipeAccess().recipeMap().byType(FarmingRegistrator.CROP_MUTATION_TYPE.get());
            allRecipes.forEach(cropPollinationRecipe -> {
                if (!matchedRecipes.containsKey(cropPollinationRecipe)) {
                    uniqueCrops.forEach(cropA -> {
                        uniqueCrops.forEach(cropB -> {
                            if (cropPollinationRecipe.value().matches(cropA, cropB)) {
                                matchedRecipes.put(cropPollinationRecipe, Pair.of(cropA, cropB));
                            }
                        });
                    });
                }
            });

            if (!matchedRecipes.isEmpty()) {
                RecipeHolder<CropMutationRecipe> pickedRecipe = (RecipeHolder<CropMutationRecipe>) matchedRecipes.keySet().toArray()[level.getRandom().nextInt(matchedRecipes.size())];

                BlockPos targetPos = cropMap.get(pickedRecipe.value().targetCrop());
                if (targetPos == null) {
                    // check if it was a _leaves block
                    targetPos = cropMap.get(pickedRecipe.value().targetCrop().withPath(p -> p + "_leaves"));
                }
                if (targetPos == null) {
                    // check if it was a vanilla converted crop
                    targetPos = cropMap.get(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, pickedRecipe.value().targetCrop().getPath()));
                }

                if (targetPos != null) {
                    var targetState = level.getBlockState(targetPos);
                    Identifier mutation = pickedRecipe.value().mutation();
                    float base = pickedRecipe.value().chance();
                    int special = isSpecialPollinator ? 5 : 1;
                    if (level.getBlockEntity(targetPos) instanceof CropBlockEntity cropBlockEntity) {
                        if (level.getRandom().nextFloat() <= TraitsHelper.mutationChance(base, cropBlockEntity.getMutability()) * special) {
                            cropBlockEntity.setMutation(mutation);
                        }
                    } else if (ExternalCropStats.isEnabled() && ExternalCropStats.isEligible(targetState)) {
                        int mutability = ExternalCropStats.getTrait(level, targetPos, targetState).mutability();
                        if (level.getRandom().nextFloat() <= TraitsHelper.mutationChance(base, mutability) * special) {
                            ExternalCropStats.setMutation(level, targetPos, targetState, mutation);
                        }
                    }
                }
            }
        }
    }

    public static ItemStack getPollen(Identifier crop) {
        var pollenStack = new ItemStack(FarmingRegistrator.POLLEN.get());
        pollenStack.set(FarmingDataComponents.POLLEN_BLOCK_COMPONENT, crop);
        return pollenStack;
    }

    public static ItemStack getDyeFromColor(int color) {
        float bestMatch = 0;
        Map<Integer, Identifier> COLOR_MAP = FarmUtil.DYE_COLORS;
        if (ModList.get().isLoaded("dyenamics")) {
            COLOR_MAP.putAll(FarmUtil.DYENAMICS_DYE_COLORS);
        }

        Identifier matchedColor = null;
        for (Map.Entry<Integer, Identifier> entry : COLOR_MAP.entrySet()) {
            if (bestMatch == 0 || colorDiff(entry.getKey(), color) < bestMatch) {
                bestMatch = colorDiff(entry.getKey(), color);
                matchedColor = entry.getValue();
            }
        }

        return BuiltInRegistries.ITEM.get(matchedColor).map(Holder::value).map(Item::getDefaultInstance).orElse(ItemStack.EMPTY);
    }

    private static float colorDiff(int color1, int color2) {
        var color1Parts = ColorUtil.getCacheColor(color1);
        var color2Parts = ColorUtil.getCacheColor(color2);

        float redDifference = color1Parts[0] - color2Parts[0];
        float greenDifference = color1Parts[1] - color2Parts[1];
        float blueDifference = color1Parts[2] - color2Parts[2];

        return redDifference * redDifference + greenDifference * greenDifference + blueDifference * blueDifference;
    }
}
