package cy.jdkdigital.productivefarming.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class CropFruitingRecipe implements Recipe<RecipeInput>
{
    public final Ingredient crop;
    public final ItemStack result;

    public CropFruitingRecipe(Ingredient crop, ItemStack result) {
        this.crop = crop;
        this.result = result;
    }

    @Override
    public boolean matches(RecipeInput container, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(RecipeInput input, HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.result.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return FarmingRegistrator.CROP_FRUITING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return FarmingRegistrator.CROP_FRUITING_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<CropFruitingRecipe>
    {
        private static final MapCodec<CropFruitingRecipe> CODEC = RecordCodecBuilder.mapCodec(
                builder -> builder.group(
                                Ingredient.CODEC.fieldOf("tree").forGetter(recipe -> recipe.crop),
                                ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.result)
                        )
                        .apply(builder, CropFruitingRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, CropFruitingRecipe> STREAM_CODEC = StreamCodec.of(
                CropFruitingRecipe.Serializer::toNetwork, CropFruitingRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<CropFruitingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CropFruitingRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        public static CropFruitingRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            return new CropFruitingRecipe(Ingredient.CONTENTS_STREAM_CODEC.decode(buffer), ItemStack.STREAM_CODEC.decode(buffer));
        }

        public static void toNetwork(RegistryFriendlyByteBuf buffer, CropFruitingRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.crop);
            ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
        }
    }
}
