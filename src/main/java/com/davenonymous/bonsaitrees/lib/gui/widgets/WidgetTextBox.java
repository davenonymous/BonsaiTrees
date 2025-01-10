package com.davenonymous.bonsaitrees.lib.gui.widgets;


import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

public class WidgetTextBox extends Widget {
	private String text;
	private int textColor = 0xFFFFFF;
	private boolean dropShadow = false;

	public WidgetTextBox(String text) {
		this.text = text;
		this.setWidth(100);
		this.setHeight(9);
	}

	public WidgetTextBox(String text, int textColor) {
		this.text = text;
		this.textColor = textColor;
		this.setWidth(100);
		this.setHeight(9);
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public int getTextColor() {
		return textColor;
	}

	public void setDropShadow(boolean dropShadow) {
		this.dropShadow = dropShadow;
	}

	public boolean getDropShadow() {
		return dropShadow;
	}

	@Override
	public void draw(GuiGraphics pGuiGraphics, Screen screen) {
		if(text == null) {
			return;
		}

		pGuiGraphics.pose().pushPose();
		RenderSystem.enableBlend();

		int scale = computeGuiScale(screen.getMinecraft());
		int bottomOffset = (int) (((double) (screen.getMinecraft().getWindow().getHeight() / scale) - (getActualY() + height)) * scale);
		int heightTmp = (height * scale) - 1;
		if(heightTmp < 0) {
			heightTmp = 0;
		}

		RenderSystem.enableScissor(getActualX() * scale, bottomOffset + 2, width * scale, heightTmp);
		pGuiGraphics.drawString(screen.getMinecraft().font, text, 0, 0, textColor, dropShadow);
		RenderSystem.disableScissor();

		RenderSystem.disableBlend();
		pGuiGraphics.pose().popPose();
	}
}