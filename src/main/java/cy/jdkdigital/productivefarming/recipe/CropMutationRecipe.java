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
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public record CropMutationRecipe(ResourceLocation targetCrop, ResourceLocation pollenCrop, ResourceLocation mutation, float chance) implements Recipe<RecipeInput>
{
    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean matches(RecipeInput container, Level level) {
        return false;
    }

    public boolean matches(ResourceLocation targetCrop, ResourceLocation pollenCrop) {
        if (this.targetCrop.equals(targetCrop) && this.pollenCrop.equals(pollenCrop)) {
            return true;
        }
        // if we have vanilla crops enabled, check anything with default namespace as mod namespace instead
        if (ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, this.targetCrop.getPath()).equals(targetCrop)) {
            return this.pollenCrop.equals(pollenCrop) || ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, this.pollenCrop.getPath()).equals(pollenCrop);
        }
        if (ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, this.pollenCrop.getPath()).equals(targetCrop)) {
            return this.targetCrop.equals(pollenCrop) || ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, this.targetCrop.getPath()).equals(pollenCrop);
        }

        // check for _leaves as well to match grapes and the likes
        if (this.targetCrop.withPath(p -> p + "_leaves").equals(targetCrop)) {
            return this.pollenCrop.equals(pollenCrop) || this.pollenCrop.withPath(p -> p + "_leaves").equals(pollenCrop);
        }
        if (this.pollenCrop.withPath(p -> p + "_leaves").equals(targetCrop)) {
            return this.targetCrop.equals(pollenCrop) || this.targetCrop.withPath(p -> p + "_leaves").equals(pollenCrop);
        }

        return false;
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
        return FarmingRegistrator.CROP_MUTATION.get();
    }

    @Override
    public RecipeType<?> getType() {
        return FarmingRegistrator.CROP_MUTATION_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<CropMutationRecipe>
    {
        private static final MapCodec<CropMutationRecipe> CODEC = RecordCodecBuilder.mapCodec(
                builder -> builder.group(
                                ResourceLocation.CODEC.fieldOf("target").forGetter(recipe -> recipe.targetCrop),
                                ResourceLocation.CODEC.fieldOf("pollen").forGetter(recipe -> recipe.pollenCrop),
                                ResourceLocation.CODEC.fieldOf("mutation").forGetter(recipe -> recipe.mutation),
                                Codec.FLOAT.fieldOf("chance").orElse(1.0f).forGetter(recipe -> recipe.chance)
                        )
                        .apply(builder, CropMutationRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, CropMutationRecipe> STREAM_CODEC = StreamCodec.of(
                CropMutationRecipe.Serializer::toNetwork, CropMutationRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<CropMutationRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CropMutationRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        public static CropMutationRecipe fromNetwork(@Nonnull RegistryFriendlyByteBuf buffer) {
            try {
                return new CropMutationRecipe(ResourceLocation.STREAM_CODEC.decode(buffer), ResourceLocation.STREAM_CODEC.decode(buffer), ResourceLocation.STREAM_CODEC.decode(buffer), buffer.readFloat());
            } catch (Exception e) {
                throw e;
            }
        }

        public static void toNetwork(@Nonnull RegistryFriendlyByteBuf buffer, CropMutationRecipe recipe) {
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
