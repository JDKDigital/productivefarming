package cy.jdkdigital.productivefarming.inventory.screen;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.inventory.FarmControllerContainer;
import cy.jdkdigital.productivelib.client.screen.AbstractUpgradeableContainerScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class FarmControllerScreen extends AbstractUpgradeableContainerScreen<FarmControllerContainer>
{
    private static final Identifier GUI_TEXTURE = Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "textures/gui/container/farm_controller.png");

    public FarmControllerScreen(FarmControllerContainer container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.extractBackground(guiGraphics, mouseX, mouseY, partialTicks);

        // Draw main screen
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, this.getLeftPos(), this.getTopPos(), 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
    }
}
