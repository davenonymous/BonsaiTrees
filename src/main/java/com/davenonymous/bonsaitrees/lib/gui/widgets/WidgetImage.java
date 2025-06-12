package com.davenonymous.bonsaitrees.lib.gui.widgets;


import com.davenonymous.bonsaitrees.lib.gui.DynamicImageResources;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.util.Size2i;

public class WidgetImage extends Widget {
	ResourceLocation image;
	float textureWidth = 16.0f;
	float textureHeight = 16.0f;
	int color = 0xFFFFFF;
	float alpha = 1.0f;
	float scale = 1.0f;
	Size2i offset = new Size2i(0, 0);

	public WidgetImage(ResourceLocation image) {
		this.image = image;
	}

	public WidgetImage(DynamicImageResources.DynTexture logo) {
		this.image = logo.resource();
		this.textureWidth = logo.image().getWidth();
		this.textureHeight = logo.image().getHeight();
	}

	public WidgetImage setTextureSize(float width, float height) {
		this.textureWidth = width;
		this.textureHeight = height;
		return this;
	}

	public WidgetImage setAlpha(float alpha) {
		this.alpha = alpha;
		return this;
	}

	public WidgetImage setScale(float scale) {
		this.scale = scale;
		return this;
	}

	public WidgetImage setOffset(Size2i offset) {
		this.offset = offset;
		return this;
	}

	public WidgetImage setOffset(int x, int y) {
		return setOffset(new Size2i(x, y));
	}

	public WidgetImage setColor(int color) {
		this.alpha = (color >> 24 & 0xFF) / 255.0F;
		this.color = color & 0x00FFFFFF;
		return this;
	}

	public WidgetImage resetColor() {
		this.alpha = 1.0f;
		this.color = 0xFFFFFF;
		return this;
	}

	public WidgetImage setImage(ResourceLocation image) {
		this.image = image;
		return this;
	}

	public float textureHeight() {
		return textureHeight;
	}

	public float textureWidth() {
		return textureWidth;
	}

	@Override
	public void draw(GuiGraphics pGuiGraphics, Screen screen) {
		if(visible && areAllParentsVisible()) {
			RenderSystem.enableBlend();
			float r = (color >> 16 & 0xFF) / 255.0F;
			float g = (color >> 8 & 0xFF) / 255.0F;
			float b = (color & 0xFF) / 255.0F;

			RenderSystem.setShaderColor(r, g, b, alpha);
			pGuiGraphics.pose().pushPose();
			pGuiGraphics.pose().scale(scale, scale, 1);
			pGuiGraphics.blitInscribed(this.image, offset.width, offset.height, this.width, this.height, (int) this.textureWidth, (int) this.textureHeight, true, true);
			RenderSystem.setShaderColor(1, 1, 1, 1);
			pGuiGraphics.pose().popPose();
		}
	}
}
