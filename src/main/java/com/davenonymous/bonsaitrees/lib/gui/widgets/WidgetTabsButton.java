package com.davenonymous.bonsaitrees.lib.gui.widgets;

import com.davenonymous.bonsaitrees.lib.gui.GUIHelper;
import com.davenonymous.bonsaitrees.lib.gui.event.MouseClickEvent;
import com.davenonymous.bonsaitrees.lib.gui.event.TabChangedEvent;
import com.davenonymous.bonsaitrees.lib.gui.event.WidgetEventResult;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;


public class WidgetTabsButton extends WidgetPanel {
	public WidgetTabsPanel parent;
	public WidgetPanel page;
	public WidgetTabsPanel.TabDockEdge edge;

	public Widget buttonImage;

	public WidgetTabsButton(WidgetTabsPanel parent, WidgetPanel page, Widget buttonImage, WidgetTabsPanel.TabDockEdge edge) {
		this.parent = parent;
		this.page = page;
		this.buttonImage = buttonImage;
		this.edge = edge;

		this.addClickListener();
	}

	public void addClickListener() {
		this.addListener(
			MouseClickEvent.class, (event, widget) -> {
				setActive(true);
				return WidgetEventResult.HANDLED;
			}
		);
	}

	@Override
	public void clear() {
		super.clear();
	}

	public void setActive(boolean fireEvent) {
		parent.activePanel.setVisible(false);
		page.setVisible(true);
		WidgetPanel tmpOld = parent.activePanel;
		parent.activePanel = page;

		if(fireEvent) {
			this.parent.fireEvent(new TabChangedEvent(tmpOld, page));
		}
	}

	private boolean isActive() {
		return this.parent.activePanel == this.page;
	}

	private boolean isFirst() {
		return this.parent.pages.indexOf(this.page) == 0;
	}

	@Override
	public void draw(GuiGraphics pGuiGraphics, Screen screen) {
		pGuiGraphics.pose().pushPose();

		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, GUIHelper.tabIcons);
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

		// Defaults are for the West edge
		int buttonWidth = 32;
		if(!isActive()) {
			buttonWidth = 28;
		}

		int buttonHeight = 28;

		int textureY = isFirst() ? 28 : 28 * 2;
		int textureX = isActive() ? 32 : 0;

		int x = 0;
		int y = 0;

		int iconX = 9;
		int iconY = 5;

		if(edge == WidgetTabsPanel.TabDockEdge.NORTH) {
			buttonHeight = 31;
			buttonWidth = 31;

			if(isActive()) {
				textureY = 104;
				textureX = 0;
			} else {
				textureY = 104;
				textureX = 31;
			}

			iconX = 7;
			iconY = 7;
		}

		if(!isActive()) {
			iconY += 2;
		}

		pGuiGraphics.blit(GUIHelper.tabIcons, x, y, textureX, textureY, buttonWidth, buttonHeight);

		buttonImage.setPosition(iconX, iconY);
		buttonImage.shiftAndDraw(pGuiGraphics, screen);

		//		if(pageStack != null) {
		//			pGuiGraphics.renderItem(pageStack, iconX, iconY);
		//		} else if(image != null) {
		//			pGuiGraphics.blitInscribed(this.image, 0, 0, this.width, this.height, this.textureWidth, this.textureHeight, true, true);
		//		}

		pGuiGraphics.pose().popPose();
	}
}
