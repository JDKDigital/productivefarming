package cy.jdkdigital.productivefarming.integrations.jei;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.recipe.CropFruitingRecipe;
import cy.jdkdigital.productivefarming.recipe.CropMutationRecipe;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import cy.jdkdigital.productivefarming.util.RecipeHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@JeiPlugin
public class ProductiveFarmingJeiPlugin implements IModPlugin
{
    private static final ResourceLocation pluginId = ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, ProductiveFarming.MODID);

    public static final RecipeType<RecipeHolder<CropFruitingRecipe>> CROP_FRUITING_TYPE = RecipeType.createRecipeHolderType(FarmingRegistrator.CROP_FRUITING_TYPE.getId());
    public static final RecipeType<RecipeHolder<CropMutationRecipe>> CROP_MUTATION_TYPE = RecipeType.createRecipeHolderType(FarmingRegistrator.CROP_MUTATION_TYPE.getId());

    @Nonnull
    @Override
    public ResourceLocation getPluginUid() {
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
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        // Crop fruiting recipes
        List<RecipeHolder<CropFruitingRecipe>> fruitingRecipeList = new ArrayList<>();
        FarmingRegistrator.CROPS.forEach(cropConfig -> {
        });
        FarmingRegistrator.HERBS.forEach(cropConfig -> {
        });
        FarmingRegistrator.BERRIES.forEach(cropConfig -> {
        });
        FarmingRegistrator.GRAPES.forEach(cropConfig -> {
        });
        FarmingRegistrator.STEMS.forEach(cropConfig -> {
        });
        FarmingRegistrator.TRELLIS.forEach(cropConfig -> {
        });
        FarmingRegistrator.VERTICAL_TRELLIS.forEach(cropConfig -> {
        });
        registration.addRecipes(CROP_FRUITING_TYPE, fruitingRecipeList);
        registration.addRecipes(CROP_FRUITING_TYPE, recipeManager.getAllRecipesFor(FarmingRegistrator.CROP_FRUITING_TYPE.get()));
        registration.addRecipes(CROP_MUTATION_TYPE, recipeManager.getAllRecipesFor(FarmingRegistrator.CROP_MUTATION_TYPE.get()));

    }
}
