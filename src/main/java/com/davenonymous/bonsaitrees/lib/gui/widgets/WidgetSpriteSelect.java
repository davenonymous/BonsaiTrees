package com.davenonymous.bonsaitrees.lib.gui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class WidgetSpriteSelect<T> extends WidgetWithChoiceValue<T> {
	Map<T, SpriteData> spriteMap;

	public WidgetSpriteSelect() {
		this.setHeight(16);
		this.setWidth(16);

		this.spriteMap = new HashMap<>();
		this.addClickListener();
	}

	public void mapChoiceToSprite(T choice, SpriteData sprite) {
		spriteMap.put(choice, sprite);
	}

	public void addChoiceWithSprite(T choice, SpriteData sprite) {
		this.addChoice(choice);
		this.mapChoiceToSprite(choice, sprite);
	}

	@Override
	public void draw(GuiGraphics pGuiGraphics, Screen screen) {
		var sprite = spriteMap.get(this.getValue());
		if(sprite == null) {
			return;
		}

		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, sprite.sprite);
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

		// Center the sprite in the widget
		var xOffset = (this.width - sprite.width) / 2;
		var yOffset = (this.height - sprite.height) / 2;

		pGuiGraphics.blit(sprite.sprite, xOffset, yOffset, sprite.u, sprite.v, sprite.width, sprite.height);
	}

	public record SpriteData(ResourceLocation sprite, int u, int v, int width, int height) {
	}
}