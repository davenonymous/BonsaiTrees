package com.davenonymous.bonsaitrees.lib.gui.widgets;

import com.davenonymous.bonsaitrees.lib.gui.GUI;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

public class WidgetProgressArrow extends WidgetWithRangeValue<Double> {
	public WidgetProgressArrow() {
		this.value = 0D;
		this.setRange(0D, 100D);
		this.setWidth(22);
		this.setHeight(15);
	}

	@Override
	public void draw(GuiGraphics pGuiGraphics, Screen screen) {
		int spriteWidth = 22;
		int spriteHeight = 15;
		int spriteY = 84;
		int bgSpriteX = 42;
		int progressSpriteX = 102;

		//pGuiGraphics.blitSprite(GUI.tabIcons, spriteWidth, spriteHeight, bgSpriteX, spriteY, this.x, this.y, spriteWidth, spriteHeight);
		pGuiGraphics.blit(GUI.tabIcons, 0, 0, bgSpriteX, spriteY, spriteWidth, spriteHeight);

		int progressWidth = (int) (spriteWidth * (this.value - this.rangeMin) / (this.rangeMax - this.rangeMin));
		pGuiGraphics.blit(GUI.tabIcons, 0, 0, progressSpriteX, spriteY, progressWidth, spriteHeight + 1);
	}
}