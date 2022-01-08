package com.davenonymous.bonsaitrees3.libnonymous.gui.framework;


import com.davenonymous.bonsaitrees3.BonsaiTrees3;
import com.davenonymous.bonsaitrees3.libnonymous.gui.framework.widgets.IValueProvider;
import com.davenonymous.bonsaitrees3.libnonymous.gui.framework.widgets.Widget;
import com.davenonymous.bonsaitrees3.libnonymous.gui.framework.widgets.WidgetPanel;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.client.gui.GuiUtils;

import java.util.HashMap;
import java.util.Map;

public class GUI extends WidgetPanel {
	public static ResourceLocation tabIcons = new ResourceLocation(BonsaiTrees3.MODID, "textures/gui/tabicons.png");
	public static ResourceLocation defaultButtonTexture = new ResourceLocation(BonsaiTrees3.MODID, "textures/gui/button_background.png");

	public boolean hasTabs = false;
	private Map<ResourceLocation, IValueProvider> valueMap = new HashMap<>();
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

	public void drawGUI(PoseStack pPoseStack, Screen screen) {
		this.setX((screen.width - this.width) / 2);
		this.setY((screen.height - this.height) / 2);

		this.shiftAndDraw(pPoseStack, screen);
	}

	@Override
	public void drawBeforeShift(PoseStack pPoseStack, Screen screen) {
		//screen.drawDefaultBackground();

		super.drawBeforeShift(pPoseStack, screen);
	}

	@Override
	public void draw(PoseStack pPoseStack, Screen screen) {
		drawWindow(pPoseStack, screen);
		super.draw(pPoseStack, screen);
	}

	protected void drawWindow(PoseStack pPoseStack, Screen screen) {
		RenderSystem.setShaderTexture(0, tabIcons);

		int texOffsetY = 11;
		int texOffsetX = 64;

		int width = this.width;
		int xOffset = 0;

		if(hasTabs) {
			width -= 32;
			xOffset += 32;
		}

		// Top Left corner
		GuiUtils.drawTexturedModalRect(pPoseStack, xOffset, 0, texOffsetX, texOffsetY, 4, 4, 0.0f);

		// Top right corner
		GuiUtils.drawTexturedModalRect(pPoseStack, xOffset + width - 4, 0, texOffsetX + 4 + 64, texOffsetY, 4, 4, 0.0f);

		// Bottom Left corner
		GuiUtils.drawTexturedModalRect(pPoseStack, xOffset, this.height - 4, texOffsetX, texOffsetY + 4 + 64, 4, 4, 0.0f);

		// Bottom Right corner
		GuiUtils.drawTexturedModalRect(pPoseStack, xOffset + width - 4, this.height - 4, texOffsetX + 4 + 64, texOffsetY + 4 + 64, 4, 4, 0.0f);

		// Top edge
		GUIHelper.drawStretchedTexture(pPoseStack, xOffset + 4, 0, width - 8, 4, texOffsetX + 4, texOffsetY, 64, 4);

		// Bottom edge
		GUIHelper.drawStretchedTexture(pPoseStack, xOffset + 4, this.height - 4, width - 8, 4, texOffsetX + 4, texOffsetY + 4 + 64, 64, 4);

		// Left edge
		GUIHelper.drawStretchedTexture(pPoseStack, xOffset, 4, 4, this.height - 8, texOffsetX, texOffsetY + 4, 4, 64);

		// Right edge
		GUIHelper.drawStretchedTexture(pPoseStack, xOffset + width - 4, 4, 4, this.height - 8, texOffsetX + 64 + 4, texOffsetY + 3, 4, 64);

		GUIHelper.drawStretchedTexture(pPoseStack, xOffset + 4, 4, width - 8, this.height - 8, texOffsetX + 4, texOffsetY + 4, 64, 64);
	}

	public void drawTooltips(PoseStack pPoseStack, Screen screen, int mouseX, int mouseY) {
		Widget hoveredWidget = getHoveredWidget(mouseX, mouseY);
		Font font = screen.getMinecraft().font;

		if(hoveredWidget != null && hoveredWidget.getTooltip() != null) {
			if(hoveredWidget.getTooltip().size() > 0) {
				screen.renderTooltip(pPoseStack, hoveredWidget.getTooltipAsFormattedCharSequence(), mouseX, mouseY, font);
				// GuiUtils.drawHoveringText(hoveredWidget.getTooltipAsString(), mouseX, mouseY, width, height, 180, font);
			}/* else {
                List<String> tooltips = new ArrayList<>();
                tooltips.add(hoveredWidget.toString());
                GuiUtils.drawHoveringText(tooltips, mouseX, mouseY, width, height, 180, font);
            }*/
		}
	}

	public void drawSlot(PoseStack pPoseStack, Screen screen, Slot slot, int guiLeft, int guiTop) {
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

		if(slot instanceof WidgetSlot) {
			if(!slot.allowModification(screen.getMinecraft().player)) {
				RenderSystem.setShaderColor(1.0f, 0.3f, 0.3f, 1.0f);
			}
		}

		float offsetX = guiLeft - 1;
		float offsetY = guiTop - 1;

		pPoseStack.pushPose();
		pPoseStack.translate(offsetX, offsetY, 0.0f);
		int texOffsetY = 84;
		int texOffsetX = 84;

		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, tabIcons);
		GuiUtils.drawTexturedModalRect(pPoseStack, slot.x, slot.y, texOffsetX, texOffsetY, 18, 18, 0.0f);
		//RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
		pPoseStack.popPose();
	}

	public void setContainer(WidgetContainer container) {
		this.container = container;
	}

	public WidgetContainer getContainer() {
		return container;
	}
}
