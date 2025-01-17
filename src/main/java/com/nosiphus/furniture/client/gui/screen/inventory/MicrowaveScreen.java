package com.nosiphus.furniture.client.gui.screen.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.nosiphus.furniture.Reference;
import com.nosiphus.furniture.blockentity.MicrowaveBlockEntity;
import com.nosiphus.furniture.inventory.container.MicrowaveMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MicrowaveScreen extends AbstractContainerScreen<MicrowaveMenu> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/gui/microwave.png");
    public MicrowaveBlockEntity blockEntity;

    public MicrowaveScreen(MicrowaveMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        this.imageWidth = 176;
        this.imageHeight = 174;
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int startX = (this.width - this.imageWidth) / 2;
        int startY = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(TEXTURE, startX, startY, 0, 0, this.imageWidth, this.imageHeight);
        renderProgressBar(guiGraphics, startX, startY);
    }

    private void renderProgressBar(GuiGraphics guiGraphics, int x, int y) {
        if(menu.isCooking()) {
            guiGraphics.blit(TEXTURE, x + 120, y + 26, 176, 0, menu.getScaledProgress(), 5);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Component title = this.title;
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, (this.imageHeight - 96 + 2), 4210752);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

}
