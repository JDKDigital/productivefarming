package cy.jdkdigital.productivefarming.integrations.jei;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

import java.util.Collection;

public class FarmingRecipeSync
{
    private static RecipeMap MAP = RecipeMap.EMPTY;

    public static <I extends RecipeInput, T extends Recipe<I>> Collection<RecipeHolder<T>> byType(RecipeType<T> type) {
        return MAP.byType(type);
    }

    @EventBusSubscriber(modid = ProductiveFarming.MODID)
    public static class Server
    {
        @SubscribeEvent
        public static void onDatapackSync(OnDatapackSyncEvent event) {
            event.sendRecipes(
                    FarmingRegistrator.CROP_FRUITING_TYPE.get(),
                    FarmingRegistrator.CROP_MUTATION_TYPE.get()
            );
        }
    }

    @EventBusSubscriber(modid = ProductiveFarming.MODID, value = Dist.CLIENT)
    public static class Client
    {
        @SubscribeEvent
        public static void onRecipesReceived(RecipesReceivedEvent event) {
            MAP = event.getRecipeMap();
        }
    }
}
