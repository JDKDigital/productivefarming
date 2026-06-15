package cy.jdkdigital.productivefarming.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public record CropMutationRecipe(Identifier targetCrop, Identifier pollenCrop, Identifier mutation, float chance) implements Recipe<RecipeInput>
{
    public static final MapCodec<CropMutationRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                            Identifier.CODEC.fieldOf("target").forGetter(recipe -> recipe.targetCrop),
                            Identifier.CODEC.fieldOf("pollen").forGetter(recipe -> recipe.pollenCrop),
                            Identifier.CODEC.fieldOf("mutation").forGetter(recipe -> recipe.mutation),
                            Codec.FLOAT.fieldOf("chance").orElse(1.0f).forGetter(recipe -> recipe.chance)
                    )
                    .apply(builder, CropMutationRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, CropMutationRecipe> STREAM_CODEC = StreamCodec.of(
            CropMutationRecipe::toNetwork, CropMutationRecipe::fromNetwork
    );

    public static final RecipeSerializer<CropMutationRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean matches(RecipeInput container, Level level) {
        return false;
    }

    public boolean matches(Identifier targetCrop, Identifier pollenCrop) {
        if (this.targetCrop.equals(targetCrop) && this.pollenCrop.equals(pollenCrop)) {
            return true;
        }
        // if we have vanilla crops enabled, check anything with default namespace as mod namespace instead
        if (Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, this.targetCrop.getPath()).equals(targetCrop)) {
            return this.pollenCrop.equals(pollenCrop) || Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, this.pollenCrop.getPath()).equals(pollenCrop);
        }
        if (Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, this.pollenCrop.getPath()).equals(targetCrop)) {
            return this.targetCrop.equals(pollenCrop) || Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, this.targetCrop.getPath()).equals(pollenCrop);
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
    public ItemStack assemble(RecipeInput container) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<CropMutationRecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<CropMutationRecipe> getType() {
        return FarmingRegistrator.CROP_MUTATION_TYPE.get();
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

    public static CropMutationRecipe fromNetwork(@Nonnull RegistryFriendlyByteBuf buffer) {
        return new CropMutationRecipe(Identifier.STREAM_CODEC.decode(buffer), Identifier.STREAM_CODEC.decode(buffer), Identifier.STREAM_CODEC.decode(buffer), buffer.readFloat());
    }

    public static void toNetwork(@Nonnull RegistryFriendlyByteBuf buffer, CropMutationRecipe recipe) {
        Identifier.STREAM_CODEC.encode(buffer, recipe.targetCrop);
        Identifier.STREAM_CODEC.encode(buffer, recipe.pollenCrop);
        Identifier.STREAM_CODEC.encode(buffer, recipe.mutation);
        buffer.writeFloat(recipe.chance);
    }
}
