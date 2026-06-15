package cy.jdkdigital.productivefarming.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class CropFruitingRecipe implements Recipe<RecipeInput>
{
    public final Ingredient crop;
    public final ItemStack result;

    public CropFruitingRecipe(Ingredient crop, ItemStack result) {
        this.crop = crop;
        this.result = result;
    }

    public static final MapCodec<CropFruitingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                            Ingredient.CODEC.fieldOf("crop").forGetter(recipe -> recipe.crop),
                            ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.result)
                    )
                    .apply(builder, CropFruitingRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, CropFruitingRecipe> STREAM_CODEC = StreamCodec.of(
            CropFruitingRecipe::toNetwork, CropFruitingRecipe::fromNetwork
    );

    public static final RecipeSerializer<CropFruitingRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    @Override
    public boolean matches(RecipeInput container, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(RecipeInput input) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<CropFruitingRecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<CropFruitingRecipe> getType() {
        return FarmingRegistrator.CROP_FRUITING_TYPE.get();
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public boolean showNotification() {
        return true;
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    public static CropFruitingRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
        return new CropFruitingRecipe(Ingredient.CONTENTS_STREAM_CODEC.decode(buffer), ItemStack.STREAM_CODEC.decode(buffer));
    }

    public static void toNetwork(RegistryFriendlyByteBuf buffer, CropFruitingRecipe recipe) {
        Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.crop);
        ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
    }
}
