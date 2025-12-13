package cy.jdkdigital.productivefarming.integrations.jei;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.recipe.CropFruitingRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Arrays;
import java.util.List;

public class CropFruitingRecipeCategory extends AbstractRecipeCategory<RecipeHolder<CropFruitingRecipe>>
{
    private final IDrawable background;

    public CropFruitingRecipeCategory(IGuiHelper guiHelper) {
        super(
                ProductiveFarmingJeiPlugin.CROP_FRUITING_TYPE,
                Component.translatable("jei.productivefarming.crop_fruiting"),
                guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "blackberry")))),
                130, 60
        );
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "textures/gui/jei/crop_fruiting.png");
        this.background = guiHelper.createDrawable(location, 0, 0, 130, 60);
    }

    @Override
    public void draw(RecipeHolder<CropFruitingRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.background.draw(guiGraphics);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<CropFruitingRecipe> recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 28, 27)
                .addItemStacks(Arrays.asList(recipe.value().crop.getItems()))
                .setSlotName("targetCrop");

        builder.addSlot(RecipeIngredientRole.OUTPUT, 94, 27)
                .addItemStacks(List.of(recipe.value().result))
                .setSlotName("result");
    }
}
