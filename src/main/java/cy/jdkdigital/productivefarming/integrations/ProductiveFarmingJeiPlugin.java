package cy.jdkdigital.productivefarming.integrations;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.recipe.CropFruitingRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class ProductiveFarmingJeiPlugin implements IModPlugin
{
    private static final ResourceLocation pluginId = ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, ProductiveFarming.MODID);

    public static final RecipeType<CropFruitingRecipe> TREE_FRUITING_TYPE = RecipeType.create(ProductiveFarming.MODID, "tree_fruiting", CropFruitingRecipe.class);

    @Nonnull
    @Override
    public ResourceLocation getPluginUid() {
        return pluginId;
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
//        registration.addRecipeCatalyst(new ItemStack(FarmingRegistrator.SAWMILL.get()), SAWMILL_TYPE);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

        registration.addRecipeCategories(new CropFruitingRecipeCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        // Crop fruiting recipes
        List<CropFruitingRecipe> fruitingRecipeList = new ArrayList<>();
//        TreeFinder.trees.forEach((resourceLocation, treeObject) -> {
//            if (treeObject.hasFruit()) {
//                fruitingRecipeList.add(new CropFruitingRecipe(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, ""), Ingredient.of(treeObject.getSaplingBlock().get()), treeObject.getFruit().getItem().copy()));
//            }
//        });
        registration.addRecipes(TREE_FRUITING_TYPE, fruitingRecipeList);

    }
}
