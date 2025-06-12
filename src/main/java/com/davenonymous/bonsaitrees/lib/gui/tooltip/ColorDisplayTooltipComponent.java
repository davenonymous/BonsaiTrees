package com.davenonymous.bonsaitrees.lib.gui.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public class ColorDisplayTooltipComponent implements TooltipComponent, ClientTooltipComponent {
	private final int color;
	private final int width;
	private final int height;

	public ColorDisplayTooltipComponent(int color, int width, int height) {
		this.color = color;
		this.height = height;
		this.width = width;
	}

	public int getColor() {
		return color;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getWidth(Font font) {
		return width;
	}

	@Override
	public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
		// color is rgba, so we need to mask out the alpha channel
		guiGraphics.fill(x, y, x + this.width, y + this.height - 2, color + 0xFF000000);
	}

}
