package cy.jdkdigital.productivefarming.integrations.jei;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.recipe.CropMutationRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

public class CropMutationRecipeCategory extends AbstractRecipeCategory<RecipeHolder<CropMutationRecipe>>
{
    private final IDrawable background;

    public CropMutationRecipeCategory(IGuiHelper guiHelper) {
        super(
                ProductiveFarmingJeiPlugin.CROP_MUTATION_TYPE,
                Component.translatable("jei.productivefarming.crop_mutation"),
                guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "blue_beauty_tomato")))),
                130, 60
        );
        Identifier location = Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "textures/gui/jei/crop_mutation.png");
        this.background = guiHelper.drawableBuilder(location, 0, 0, 130, 60).setTextureSize(256, 256).build();
    }

    @Override
    public void draw(RecipeHolder<CropMutationRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        this.background.draw(guiGraphics, 0, 0);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<CropMutationRecipe> recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 13, 27)
                .addItemStack(BuiltInRegistries.ITEM.getValue(recipe.value().pollenCrop()).getDefaultInstance())
                .setSlotName("pollenCrop");
        builder.addSlot(RecipeIngredientRole.INPUT, 56, 27)
                .addItemStack(BuiltInRegistries.ITEM.getValue(recipe.value().targetCrop()).getDefaultInstance())
                .setSlotName("targetCrop");

        builder.addSlot(RecipeIngredientRole.OUTPUT, 109, 27)
                .addItemStack(BuiltInRegistries.ITEM.getValue(recipe.value().mutation()).getDefaultInstance())
                .setSlotName("result");
    }
}
