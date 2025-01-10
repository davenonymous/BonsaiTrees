package com.davenonymous.bonsaitrees.lib.gui.widgets;

import com.davenonymous.bonsaitrees.lib.gui.GUI;
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
		this(GUI.tabIcons, u, v, width, height);
	}

	public WidgetSprite(ResourceLocation spriteSheet, int u, int v, int width, int height) {
		this.spriteSheet = spriteSheet;
		this.u = u;
		this.v = v;
		this.width = width;
		this.height = height;
	}

	@Override
	public void draw(GuiGraphics guiGraphics, Screen screen) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, spriteSheet);
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
		guiGraphics.blit(spriteSheet, 0, 0, u, v, width, height);
	}
}