package com.davenonymous.bonsaitrees.lib.gui.widgets;


import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

import java.awt.*;

public class WidgetColorDisplay extends Widget {
	private int colorA;
	private int alpha = 255;

	public WidgetColorDisplay(Color color) {
		this.setColor(color);
	}

	public WidgetColorDisplay(ChatFormatting color) {
		this.setColor(color);
	}

	public WidgetColorDisplay(int color) {
		this.setColor(color);
	}

	public int getColor() {
		return colorA;
	}

	public WidgetColorDisplay setColor(Color color) {
		this.colorA = (alpha << 24) + color.getRGB();
		return this;
	}

	public WidgetColorDisplay setColor(ChatFormatting color) {
		this.colorA = (alpha << 24) + color.getColor();
		return this;
	}

	public WidgetColorDisplay setColor(int color) {
		this.colorA = (alpha << 24) + (color & 0xFFFFFF);
		return this;
	}

	@Override
	public void draw(GuiGraphics pGuiGraphics, Screen screen) {
		pGuiGraphics.fill(0, 0, width, height, colorA);
	}

}
