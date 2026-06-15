package cy.jdkdigital.productivefarming.inventory.screen;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.inventory.FeedingTroughContainer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class FeedingTroughScreen extends AbstractContainerScreen<FeedingTroughContainer>
{
    private static final Identifier GUI_TEXTURE = Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "textures/gui/container/feeding_trough.png");

    public FeedingTroughScreen(FeedingTroughContainer container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.extractBackground(guiGraphics, mouseX, mouseY, partialTicks);

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, this.getLeftPos(), this.getTopPos(), 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        guiGraphics.text(font, this.title, 8, 6, 4210752, false);
        guiGraphics.text(font, this.playerInventoryTitle, 8, (this.imageHeight - 96 + 2), 4210752, false);
    }
}
