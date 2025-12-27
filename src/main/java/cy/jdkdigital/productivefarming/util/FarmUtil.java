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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
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
    public static Map<ResourceLocation, Integer> VANILLA_FLOWER_COLORS = new HashMap<>() {{
        put(ResourceLocation.parse("minecraft:rose_bush"), 0xffff4540);
        put(ResourceLocation.parse("minecraft:lilac"), 0xffde93f1);
        put(ResourceLocation.parse("minecraft:peony"), 0xffde93f1);
        put(ResourceLocation.parse("minecraft:poppy"), 0xffff4540);
        put(ResourceLocation.parse("minecraft:cornflower"), 0xff728ff1);
        put(ResourceLocation.parse("minecraft:alium"), 0xffe8cffe);
        put(ResourceLocation.parse("minecraft:azure_bluet"), 0xfff7f7f7);
        put(ResourceLocation.parse("minecraft:blue_orchid"), 0xff2fcefd);
        put(ResourceLocation.parse("minecraft:dandelion"), 0xffffec4f);
        put(ResourceLocation.parse("minecraft:lily_of_the_valley"), 0xffffffff);
        put(ResourceLocation.parse("minecraft:oxeye_daisy"), 0xffffffff);
        put(ResourceLocation.parse("minecraft:white_tulip"), 0xfff7f7f7);
        put(ResourceLocation.parse("minecraft:red_tulip"), 0xffff4540);
        put(ResourceLocation.parse("minecraft:pink_tulip"), 0xfff6e2ff);
        put(ResourceLocation.parse("minecraft:orange_tulip"), 0xfff19d25);
        put(ResourceLocation.parse("minecraft:torchflower"), 0xfffce257);
        put(ResourceLocation.parse("minecraft:wither_rose"), 0xff42352e);
    }};
    // TODO move to lib?
    public static Map<Integer, ResourceLocation> DYE_COLORS = new HashMap<>() {{
        put(-6447721, ResourceLocation.parse("minecraft:light_gray_dye"));
        put(-12103854, ResourceLocation.parse("minecraft:gray_dye"));
        put(-393218, ResourceLocation.parse("minecraft:white_dye"));
        put(-3715395, ResourceLocation.parse("minecraft:magenta_dye"));
        put(-14869215, ResourceLocation.parse("minecraft:black_dye"));
        put(-8337633, ResourceLocation.parse("minecraft:lime_dye"));
        put(-425955, ResourceLocation.parse("minecraft:orange_dye"));
        put(-12930086, ResourceLocation.parse("minecraft:light_blue_dye"));
        put(-816214, ResourceLocation.parse("minecraft:pink_dye"));
        put(-8170446, ResourceLocation.parse("minecraft:brown_dye"));
        put(-10585066, ResourceLocation.parse("minecraft:green_dye"));
        put(-15295332, ResourceLocation.parse("minecraft:cyan_dye"));
        put(-12827478, ResourceLocation.parse("minecraft:blue_dye"));
        put(-7785800, ResourceLocation.parse("minecraft:purple_dye"));
        put(-47808, ResourceLocation.parse("minecraft:red_dye"));
        put(-75715, ResourceLocation.parse("minecraft:yellow_dye"));
    }};
    public static Map<Integer, ResourceLocation> DYENAMICS_DYE_COLORS = new HashMap<>() {{
        put(-2997991, ResourceLocation.parse("dyenamics:persimmon_dye"));
        put(-19386, ResourceLocation.parse("dyenamics:honey_dye"));
        put(-13861505, ResourceLocation.parse("dyenamics:aquamarine_dye"));
        put(-560698, ResourceLocation.parse("dyenamics:bubblegum_dye"));
        put(-16672561, ResourceLocation.parse("dyenamics:cherenkov_dye"));
        put(-7345409, ResourceLocation.parse("dyenamics:icy_blue_dye"));
        put(-4220813, ResourceLocation.parse("dyenamics:peach_dye"));
        put(-1119818, ResourceLocation.parse("dyenamics:fluorescent_dye"));
        put(-4531627, ResourceLocation.parse("dyenamics:conifer_dye"));
        put(-9298341, ResourceLocation.parse("dyenamics:wine_dye"));
        put(-11122177, ResourceLocation.parse("dyenamics:ultramarine_dye"));
        put(-55195, ResourceLocation.parse("dyenamics:rose_dye"));
        put(-7739208, ResourceLocation.parse("dyenamics:mint_dye"));
        put(-14931855, ResourceLocation.parse("dyenamics:navy_dye"));
        put(-2919655, ResourceLocation.parse("dyenamics:amber_dye"));
        put(-3932263, ResourceLocation.parse("dyenamics:spring_green_dye"));
        put(-7274496, ResourceLocation.parse("dyenamics:maroon_dye"));
        put(-2254337, ResourceLocation.parse("dyenamics:lavender_dye"));
    }};

    public static String getLatinTranslationKey(Item item) {
        var key = BuiltInRegistries.ITEM.getKey(item).getPath().replace("_seeds", "").replace("_stem", "");
        return "tooltip." + ProductiveFarming.MODID + "." + key + ".latin";
    }

    public static void pollinateCrops(ServerLevel level, BlockPos pos, int distance, boolean isSpecialPollinator, List<ResourceLocation> uniqueCrops) {
        List<BlockPos> crops = BlockPos.betweenClosedStream(pos.offset(-distance, -distance, -distance), pos.offset(distance, distance, distance)).map(BlockPos::immutable).toList();
        // Build permutation map
        Map<ResourceLocation, BlockPos> flowerMap = new HashMap<>();
        Map<ResourceLocation, BlockPos> cropMap = new HashMap<>();
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
                ArrayList<ResourceLocation> keys = new ArrayList<>(flowerMap.keySet());
                Collections.shuffle(keys);
                List<ResourceLocation> randomFlowers = keys.subList(0, 2);
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
                    var newFlower = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, pBeeFlower)).defaultBlockState();
                    if (newFlower.is(BlockTags.FLOWERS)) {
                        Player fakePlayer = FakePlayerFactory.get(level, new GameProfile(PLAYER_UUID, "flower_master"));

                        // Find valid block to place it on
                        var list = BlockPos.betweenClosedStream(pos.offset(-distance, -distance + 2, -distance), pos.offset(distance, distance - 2, distance)).map(BlockPos::immutable).collect(Collectors.toList());
                        Collections.shuffle(list);
                        for (BlockPos blockPos : list) {
                            if (level.getBlockState(blockPos).canBeReplaced() && (!(newFlower.getBlock() instanceof TallFlowerBlock) || level.getBlockState(blockPos.above()).canBeReplaced()) && newFlower.canSurvive(level, blockPos) && !EventHooks.onBlockPlace(fakePlayer, BlockSnapshot.create(level.dimension(), level, blockPos), fakePlayer.getDirection())) {
                                if (level.setBlock(blockPos, newFlower, CropBlock.UPDATE_ALL_IMMEDIATE)) {
                                    // Set color
                                    if (level.getBlockEntity(blockPos) instanceof ColorfulFlowerBlockEntity flowerBlockEntity) {
                                        flowerBlockEntity.setColor(ColorUtil.blend(colorA, colorB, Mth.lerp(level.random.nextFloat(), 0.3f, 0.7f)));
                                        // for double flower
                                        if (newFlower.is(BlockTags.TALL_FLOWERS)) {
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
            Map<RecipeHolder<CropMutationRecipe>, Pair<ResourceLocation, ResourceLocation>> matchedRecipes = new HashMap<>();
            var allRecipes = level.getRecipeManager().getAllRecipesFor(FarmingRegistrator.CROP_MUTATION_TYPE.get());
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
                RecipeHolder<CropMutationRecipe> pickedRecipe = (RecipeHolder<CropMutationRecipe>) matchedRecipes.keySet().toArray()[level.random.nextInt(matchedRecipes.size())];

                BlockPos targetPos = cropMap.get(pickedRecipe.value().targetCrop());
                if (targetPos == null) {
                    // check if it was a _leaves block
                    targetPos = cropMap.get(pickedRecipe.value().targetCrop().withPath(p -> p + "_leaves"));
                }
                if (targetPos == null) {
                    // check if it was a vanilla converted crop
                    targetPos = cropMap.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, pickedRecipe.value().targetCrop().getPath()));
                }

                if (targetPos != null && level.random.nextFloat() <= (pickedRecipe.value().chance() * (isSpecialPollinator ? 5 : 1))) {
                    if (level.getBlockEntity(targetPos) instanceof CropBlockEntity cropBlockEntity) {
                        cropBlockEntity.setMutation(pickedRecipe.value().mutation());
                    }
                }
            }
        }
    }

    public static ItemStack getPollen(ResourceLocation crop) {
        var pollenStack = new ItemStack(FarmingRegistrator.POLLEN.get());
        pollenStack.set(FarmingDataComponents.POLLEN_BLOCK_COMPONENT, crop);
        return pollenStack;
    }

    public static ItemStack getDyeFromColor(int color) {
        float bestMatch = 0;
        Map<Integer, ResourceLocation> COLOR_MAP = FarmUtil.DYE_COLORS;
        if (ModList.get().isLoaded("dyenamics")) {
            COLOR_MAP.putAll(FarmUtil.DYENAMICS_DYE_COLORS);
        }

        ResourceLocation matchedColor = null;
        for (Map.Entry<Integer, ResourceLocation> entry : COLOR_MAP.entrySet()) {
            if (bestMatch == 0 || colorDiff(entry.getKey(), color) < bestMatch) {
                bestMatch = colorDiff(entry.getKey(), color);
                matchedColor = entry.getValue();
            }
        }

        return BuiltInRegistries.ITEM.get(matchedColor).getDefaultInstance();
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
