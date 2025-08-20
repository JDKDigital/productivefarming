package cy.jdkdigital.productivefarming.integrations.ponder;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class FarmingPonderPlugin implements PonderPlugin {

    @Override
    public @NotNull String getModId() {
        return ProductiveFarming.MODID;
    }

    @Override
    public void registerScenes(@NotNull PonderSceneRegistrationHelper<ResourceLocation> helper) {
        FarmingPonderScenes.register(helper);
    }

    @Override
    public void registerTags(@NotNull PonderTagRegistrationHelper<ResourceLocation> helper) {
        FarmingPonderTags.register(helper);
    }
}
