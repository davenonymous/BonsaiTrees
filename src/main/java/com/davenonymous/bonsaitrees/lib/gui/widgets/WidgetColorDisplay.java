package com.davenonymous.bonsaitrees.lib.gui.widgets;


import com.davenonymous.bonsaitrees.lib.gui.GUIHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

import java.awt.*;

public class WidgetColorDisplay extends Widget {
	private Color colorA;
	private final Color colorB;
	private final boolean horizontal;

	public WidgetColorDisplay(Color color) {
		this.colorA = color;
		this.colorB = color;
		this.horizontal = false;
	}

	public Color getColor() {
		return colorA;
	}

	public WidgetColorDisplay setColor(Color color) {
		this.colorA = color;
		return this;
	}

	@Override
	public void draw(GuiGraphics pGuiGraphics, Screen screen) {
		GUIHelper.drawColoredRectangle(pGuiGraphics, 0, 0, width, height, colorA.getRGB());
	}

}