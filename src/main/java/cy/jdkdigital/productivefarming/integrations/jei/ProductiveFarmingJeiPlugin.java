package cy.jdkdigital.productivefarming.integrations.jei;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.recipe.CropFruitingRecipe;
import cy.jdkdigital.productivefarming.recipe.CropMutationRecipe;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;

import javax.annotation.Nonnull;
import java.util.ArrayList;

@JeiPlugin
public class ProductiveFarmingJeiPlugin implements IModPlugin
{
    private static final Identifier pluginId = Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, ProductiveFarming.MODID);

    public static final RecipeType<RecipeHolder<CropFruitingRecipe>> CROP_FRUITING_TYPE = RecipeType.createRecipeHolderType(FarmingRegistrator.CROP_FRUITING_TYPE.getId());
    public static final RecipeType<RecipeHolder<CropMutationRecipe>> CROP_MUTATION_TYPE = RecipeType.createRecipeHolderType(FarmingRegistrator.CROP_MUTATION_TYPE.getId());

    @Nonnull
    @Override
    public Identifier getPluginUid() {
        return pluginId;
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
//        registration.addRecipeCatalyst(new ItemStack(Items.WHEAT), CROP_FRUITING_TYPE);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

        registration.addRecipeCategories(new CropFruitingRecipeCategory(guiHelper));
        registration.addRecipeCategories(new CropMutationRecipeCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(CROP_FRUITING_TYPE, new ArrayList<>(FarmingRecipeSync.byType(FarmingRegistrator.CROP_FRUITING_TYPE.get())));
        registration.addRecipes(CROP_MUTATION_TYPE, new ArrayList<>(FarmingRecipeSync.byType(FarmingRegistrator.CROP_MUTATION_TYPE.get())));
    }
}
