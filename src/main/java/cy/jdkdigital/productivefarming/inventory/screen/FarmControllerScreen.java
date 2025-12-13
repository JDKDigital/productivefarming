package cy.jdkdigital.productivefarming.inventory.screen;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.inventory.FarmControllerContainer;
import cy.jdkdigital.productivelib.client.screen.AbstractUpgradeableContainerScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FarmControllerScreen extends AbstractUpgradeableContainerScreen<FarmControllerContainer>
{
    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "textures/gui/container/farm_controller.png");

    public FarmControllerScreen(FarmControllerContainer container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);
    }

    @Override
    public void render(GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        // Draw main screen
        guiGraphics.blit(GUI_TEXTURE, this.getGuiLeft(), this.getGuiTop(), 0, 0, this.getXSize(), this.getYSize());
    }
}
