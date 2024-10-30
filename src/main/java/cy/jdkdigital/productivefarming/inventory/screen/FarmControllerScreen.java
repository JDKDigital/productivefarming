package cy.jdkdigital.productivefarming.inventory.screen;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.inventory.FarmControllerContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FarmControllerScreen extends AbstractContainerScreen<FarmControllerContainer>
{
    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "textures/gui/container/feeding_trough.png");

    public FarmControllerScreen(FarmControllerContainer container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);
    }

    @Override
    public void render(GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack, mouseX, mouseY, partialTicks);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(font, this.title, 8, 6, 4210752, false);
        guiGraphics.drawString(font, this.playerInventoryTitle, 8, (this.getYSize() - 96 + 2), 4210752, false);

//        if (isHovering(75, 33, 18, 18, mouseX, mouseY)) {
//            List<FormattedCharSequence> tooltipList = new ArrayList<>();
//            tooltipList.add(Component.translatable(ProductiveFarming.MODID + ".screen.progress", this.menu.blockEntity.progress + "/200").getVisualOrderText());
//
//            guiGraphics.renderTooltip(font, tooltipList, mouseX - getGuiLeft(), mouseY - getGuiTop());
//        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        // Draw main screen
        guiGraphics.blit(GUI_TEXTURE, this.getGuiLeft(), this.getGuiTop(), 0, 0, this.getXSize(), this.getYSize());

        // Draw progress
//        int progress = (int) (18f * ((float) this.menu.blockEntity.progress  / (float) this.menu.blockEntity.recipeTime));
//        guiGraphics.blit(GUI_TEXTURE, this.getGuiLeft() + 75, this.getGuiTop() + 33, 176, 0, progress, 18);
    }
}
