package cy.jdkdigital.productivefarming.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class CropPollinationRecipe implements Recipe<RecipeInput>
{
    public final ResourceLocation targetCrop;
    public final ResourceLocation pollenCrop;
    public final ResourceLocation mutation;
    public final float chance;

    public CropPollinationRecipe(ResourceLocation targetCrop, ResourceLocation pollenCrop, ResourceLocation mutation, float chance) {
        this.targetCrop = targetCrop;
        this.pollenCrop = pollenCrop;
        this.mutation = mutation;
        this.chance = chance;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean matches(RecipeInput container, Level level) {
        return false;
    }

    public boolean matches(ResourceLocation targetCrop, ResourceLocation pollenCrop) {
        return this.targetCrop.equals(targetCrop) && this.pollenCrop.equals(pollenCrop);
    }

    @Override
    public ItemStack assemble(RecipeInput container, HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return FarmingRegistrator.CROP_POLLINATION.get();
    }

    @Override
    public RecipeType<?> getType() {
        return FarmingRegistrator.CROP_POLLINATION_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<CropPollinationRecipe>
    {
        private static final MapCodec<CropPollinationRecipe> CODEC = RecordCodecBuilder.mapCodec(
                builder -> builder.group(
                                ResourceLocation.CODEC.fieldOf("target").forGetter(recipe -> recipe.targetCrop),
                                ResourceLocation.CODEC.fieldOf("pollen").forGetter(recipe -> recipe.pollenCrop),
                                ResourceLocation.CODEC.fieldOf("mutation").forGetter(recipe -> recipe.mutation),
                                Codec.FLOAT.fieldOf("chance").orElse(1.0f).forGetter(recipe -> recipe.chance)
                        )
                        .apply(builder, CropPollinationRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, CropPollinationRecipe> STREAM_CODEC = StreamCodec.of(
                CropPollinationRecipe.Serializer::toNetwork, CropPollinationRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<CropPollinationRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CropPollinationRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        public static CropPollinationRecipe fromNetwork(@Nonnull RegistryFriendlyByteBuf buffer) {
            try {
                return new CropPollinationRecipe(ResourceLocation.STREAM_CODEC.decode(buffer), ResourceLocation.STREAM_CODEC.decode(buffer), ResourceLocation.STREAM_CODEC.decode(buffer), buffer.readFloat());
            } catch (Exception e) {
                throw e;
            }
        }

        public static void toNetwork(@Nonnull RegistryFriendlyByteBuf buffer, CropPollinationRecipe recipe) {
            try {
                ResourceLocation.STREAM_CODEC.encode(buffer, recipe.targetCrop);
                ResourceLocation.STREAM_CODEC.encode(buffer, recipe.pollenCrop);
                ResourceLocation.STREAM_CODEC.encode(buffer, recipe.mutation);
                buffer.writeFloat(recipe.chance);
            } catch (Exception e) {
                throw e;
            }
        }
    }
}
