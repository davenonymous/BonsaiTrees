package com.davenonymous.bonsaitrees.lib.gui.widgets;

import com.davenonymous.bonsaitrees.lib.gui.GUIHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

public class WidgetSprite extends Widget {
	ResourceLocation spriteSheet;
	int u;
	int v;
	int width;
	int height;

	public WidgetSprite(int u, int v, int width, int height) {
		this(GUIHelper.tabIcons, u, v, width, height);
	}

	public WidgetSprite(ResourceLocation spriteSheet, int u, int v, int width, int height) {
		this.spriteSheet = spriteSheet;
		this.u = u;
		this.v = v;
		this.width = width;
		this.height = height;
	}

	public WidgetSprite applyFrom(WidgetSprite other) {
		this.spriteSheet = other.spriteSheet;
		this.u = other.u;
		this.v = other.v;
		this.width = other.width;
		this.height = other.height;
		return this;
	}

	@Override
	public void draw(GuiGraphics guiGraphics, Screen screen) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, spriteSheet);
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
		guiGraphics.blit(spriteSheet, 0, 0, u, v, width, height);
	}

	public static WidgetSprite activeRedstoneTorch = new WidgetSprite(36, 84, 4, 11);
	public static WidgetSprite downButton = new WidgetSprite(124, 84, 11, 7);
	public static WidgetSprite upButton = new WidgetSprite(135, 84, 11, 7);
	public static WidgetSprite downButtonHover = new WidgetSprite(146, 84, 11, 7);
	public static WidgetSprite upButtonHover = new WidgetSprite(157, 84, 11, 7);

	public static WidgetSprite leftButton = new WidgetSprite(124, 95, 7, 11);
	public static WidgetSprite rightButton = new WidgetSprite(131, 95, 7, 11);
	public static WidgetSprite leftButtonHover = new WidgetSprite(146, 95, 7, 11);
	public static WidgetSprite rightButtonHover = new WidgetSprite(151, 95, 7, 11);
}
