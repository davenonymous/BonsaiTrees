package com.davenonymous.bonsaitrees.lib.gui.widgets;


import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

public class WidgetCheckbox extends WidgetSelectButton<Boolean> {
	public WidgetCheckbox() {
		this.addChoice(true, false);
		this.setWidth(10);
		this.setHeight(10);

		this.addClickListener();
	}

	@Override
	protected void drawButtonContent(GuiGraphics pGuiGraphics, Screen screen) {
		if(this.getValue()) {
			drawString(pGuiGraphics, screen, "x");
		}
	}
}