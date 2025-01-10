package com.davenonymous.bonsaitrees.lib.gui.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public class SpriteTooltipComponent implements TooltipComponent, ClientTooltipComponent {
	private final ResourceLocation sprite;
	private final int textureX;
	private final int textureY;
	private final int textureWidth;
	private final int textureHeight;
	private final int width;
	private final int height;

	public SpriteTooltipComponent(ResourceLocation sprite, int width, int height, int textureX, int textureY, int textureWidth, int textureHeight) {
		this.height = height;
		this.sprite = sprite;
		this.textureHeight = textureHeight;
		this.textureWidth = textureWidth;
		this.textureX = textureX;
		this.textureY = textureY;
		this.width = width;
	}

	public SpriteTooltipComponent(ResourceLocation sprite, int width, int height) {
		this(sprite, width, height, 0, 0, width, height);
	}

	public SpriteTooltipComponent(ResourceLocation sprite, int width, int height, int textureX, int textureY) {
		this(sprite, width, height, textureX, textureY, width, height);
	}

	public ResourceLocation getSprite() {
		return sprite;
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
		int xOffset = x + (width - textureWidth) / 2;
		int yOffset = y + (height - textureHeight) / 2;
		guiGraphics.blit(sprite, xOffset, yOffset, textureX, textureY, textureWidth, textureHeight);
	}

}
