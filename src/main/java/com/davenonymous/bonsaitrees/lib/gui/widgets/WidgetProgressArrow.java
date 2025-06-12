package com.davenonymous.bonsaitrees.lib.gui.widgets;

import com.davenonymous.bonsaitrees.lib.gui.GUIHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

public class WidgetProgressArrow extends WidgetWithRangeValue<Double> {
	private boolean disabled = false;

	public WidgetProgressArrow() {
		this.value = 0D;
		this.setRange(0D, 100D);
		this.setWidth(22);
		this.setHeight(15);
	}

	public boolean disabled() {
		return disabled;
	}

	public WidgetProgressArrow setDisabled(boolean disabled) {
		this.disabled = disabled;
		return this;
	}

	@Override
	public void draw(GuiGraphics pGuiGraphics, Screen screen) {
		int spriteWidth = 22;
		int spriteHeight = 15;
		int spriteY = 84;
		int bgSpriteX = 42;
		int progressSpriteX = 102;

		if(this.disabled) {
			RenderSystem.setShaderColor(0.95f, 0f, 0f, 1.0f);
			pGuiGraphics.blit(GUIHelper.tabIcons, 0, 0, bgSpriteX, spriteY, spriteWidth, spriteHeight);
			RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
		} else {
			pGuiGraphics.blit(GUIHelper.tabIcons, 0, 0, bgSpriteX, spriteY, spriteWidth, spriteHeight);
		}

		if(!this.disabled) {
			int progressWidth = (int) (spriteWidth * (this.value - this.rangeMin) / (this.rangeMax - this.rangeMin));
			pGuiGraphics.blit(GUIHelper.tabIcons, 0, 0, progressSpriteX, spriteY, progressWidth, spriteHeight + 1);
		}
	}
}
