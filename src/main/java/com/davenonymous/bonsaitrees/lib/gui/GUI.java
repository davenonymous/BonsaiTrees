package com.davenonymous.bonsaitrees.lib.gui;


import com.davenonymous.bonsaitrees.BonsaiTrees;
import com.davenonymous.bonsaitrees.lib.gui.widgets.IValueProvider;
import com.davenonymous.bonsaitrees.lib.gui.widgets.Widget;
import com.davenonymous.bonsaitrees.lib.gui.widgets.WidgetPanel;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class GUI extends WidgetPanel {
	public static ResourceLocation tabIcons = BonsaiTrees.resource("textures/gui/tabicons.png");
	public static ResourceLocation defaultButtonTexture = BonsaiTrees.resource("textures/gui/button_background.png");

	public boolean hasTabs = false;
	private final Map<ResourceLocation, IValueProvider> valueMap = new HashMap<>();
	private WidgetContainer container;

	public GUI(int x, int y, int width, int height) {
		this.setX(x);
		this.setY(y);
		this.setWidth(width);
		this.setHeight(height);
	}

	public void findValueWidgets() {
		this.findValueWidgets(this);
	}

	public void registerValueWidget(ResourceLocation id, IValueProvider widget) {
		this.valueMap.put(id, widget);
	}

	public Object getValue(ResourceLocation id) {
		if(id == null || !valueMap.containsKey(id)) {
			return null;
		}

		return valueMap.get(id).getValue();
	}

	public void drawGUI(GuiGraphics pGuiGraphics, Screen screen) {
		this.setX((screen.width - this.width) / 2);
		this.setY((screen.height - this.height) / 2);

		this.shiftAndDraw(pGuiGraphics, screen);
	}

	@Override
	public void drawBeforeShift(GuiGraphics pGuiGraphics, Screen screen) {
		//screen.drawDefaultBackground();

		super.drawBeforeShift(pGuiGraphics, screen);
	}

	@Override
	public void draw(GuiGraphics pGuiGraphics, Screen screen) {
		drawWindow(pGuiGraphics, screen);
		super.draw(pGuiGraphics, screen);
	}

	protected void drawWindow(GuiGraphics pGuiGraphics, Screen screen) {
		int texOffsetY = 11;
		int texOffsetX = 64;

		int width = this.width;
		int xOffset = 0;

		if(hasTabs) {
			width -= 32;
			xOffset += 32;
		}

		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, tabIcons);

		// Top Left corner
		pGuiGraphics.blit(tabIcons, xOffset, 0, texOffsetX, texOffsetY, 4, 4);

		// Top right corner
		pGuiGraphics.blit(tabIcons, xOffset + width - 4, 0, texOffsetX + 4 + 64, texOffsetY, 4, 4);

		// Bottom Left corner
		pGuiGraphics.blit(tabIcons, xOffset, this.height - 4, texOffsetX, texOffsetY + 4 + 64, 4, 4);

		// Bottom Right corner
		pGuiGraphics.blit(tabIcons, xOffset + width - 4, this.height - 4, texOffsetX + 4 + 64, texOffsetY + 4 + 64, 4, 4);


		// Top edge
		GUIHelper.drawStretchedTexture(pGuiGraphics, xOffset + 4, 0, width - 8, 4, texOffsetX + 4, texOffsetY, 64, 4);

		// Bottom edge
		GUIHelper.drawStretchedTexture(pGuiGraphics, xOffset + 4, this.height - 4, width - 8, 4, texOffsetX + 4, texOffsetY + 4 + 64, 64, 4);

		// Left edge
		GUIHelper.drawStretchedTexture(pGuiGraphics, xOffset, 4, 4, this.height - 8, texOffsetX, texOffsetY + 4, 4, 64);

		// Right edge
		GUIHelper.drawStretchedTexture(pGuiGraphics, xOffset + width - 4, 4, 4, this.height - 8, texOffsetX + 64 + 4, texOffsetY + 3, 4, 64);

		GUIHelper.drawStretchedTexture(pGuiGraphics, xOffset + 4, 4, width - 8, this.height - 8, texOffsetX + 4, texOffsetY + 4, 64, 64);
	}

	public void drawTooltips(GuiGraphics pGuiGraphics, Screen screen, int mouseX, int mouseY) {
		Widget hoveredWidget = getHoveredWidget(mouseX, mouseY);
		if(hoveredWidget == null || hoveredWidget.getTooltip() == null) {
			return;
		}
		if(hoveredWidget.getTooltip().isEmpty()) {
			return;
		}

		Font font = screen.getMinecraft().font;
		pGuiGraphics.renderComponentTooltipFromElements(font, hoveredWidget.getTooltipFormatted(), mouseX, mouseY, ItemStack.EMPTY);
	}

	public void drawSlot(GuiGraphics pGuiGraphics, Screen screen, Slot slot, int guiLeft, int guiTop) {
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

		if(slot instanceof WidgetSlot) {
			if(!slot.allowModification(screen.getMinecraft().player)) {
				RenderSystem.setShaderColor(1.0f, 0.3f, 0.3f, 1.0f);
			}
		}

		float offsetX = guiLeft - 1;
		float offsetY = guiTop - 1;

		pGuiGraphics.pose().pushPose();
		pGuiGraphics.pose().translate(offsetX, offsetY, 0.0f);
		int texOffsetY = 84;
		int texOffsetX = 84;

		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, tabIcons);
		pGuiGraphics.blit(tabIcons, slot.x, slot.y, texOffsetX, texOffsetY, 18, 18);

		//RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
		pGuiGraphics.pose().popPose();
	}

	public void setContainer(WidgetContainer container) {
		this.container = container;
	}

	public WidgetContainer getContainer() {
		return container;
	}
}