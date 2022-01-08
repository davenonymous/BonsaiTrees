package com.davenonymous.bonsaitrees3.libnonymous.gui.framework.widgets;


import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;

import java.awt.*;

public class WidgetColorDisplay extends Widget {
	private Color colorA;
	private Color colorB;
	private boolean horizontal;

	public WidgetColorDisplay(Color color) {
		this.colorA = color;
		this.colorB = color;
		this.horizontal = false;
	}

	public WidgetColorDisplay(Color primary, Color secondary, boolean horizontal) {
		this.colorA = primary;
		this.colorB = secondary;
		this.horizontal = horizontal;
	}

	public Color getColor() {
		return colorA;
	}

	public Color getSecondaryColor() {
		return colorB;
	}

	public boolean isHorizontal() {
		return horizontal;
	}

	public WidgetColorDisplay setColor(Color color) {
		this.colorA = color;
		return this;
	}

	public WidgetColorDisplay setSecondaryColor(Color color) {
		this.colorB = color;
		return this;
	}

	public WidgetColorDisplay setHorizontal(boolean horizontal) {
		this.horizontal = horizontal;
		return this;
	}

	@Override
	public void draw(PoseStack pPoseStack, Screen screen) {
		if(isHorizontal()) {
			drawHorizontalGradientRect(pPoseStack, 0, 0, width, height, colorA, colorB);
		} else {
			drawVerticalGradientRect(pPoseStack, 0, 0, width, height, colorA, colorB);
		}
	}

	/**
	 * Draws a rectangle with a horizontal gradient between the specified colors.
	 * x2 and y2 are not included.
	 * <p>
	 * Copied from McJtyLib
	 * https://github.com/McJtyMods/McJtyLib/blob/91606b81e1dace3d6e913505b74704f4e236b3f2/src/main/java/mcjty/lib/client/RenderHelper.java#L339
	 */
	private static void drawHorizontalGradientRect(PoseStack pPoseStack, int x1, int y1, int x2, int y2, Color primary, Color secondary) {
		float zLevel = 0.0f;

		float[] pColors = primary.getRGBColorComponents(null);
		float pA = 1.0f;
		float pR = pColors[0];
		float pG = pColors[1];
		float pB = pColors[2];

		float[] sColors = secondary.getRGBColorComponents(null);
		float sA = 1.0f;
		float sR = sColors[0];
		float sG = sColors[1];
		float sB = sColors[2];

/*
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        RenderSystem.shadeModel(GL11.GL_SMOOTH);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(x1, y1, zLevel).color(pR, pG, pB, pA).endVertex();
        buffer.pos(x1, y2, zLevel).color(pR, pG, pB, pA).endVertex();
        buffer.pos(x2, y2, zLevel).color(sR, sG, sB, sA).endVertex();
        buffer.pos(x2, y1, zLevel).color(sR, sG, sB, sA).endVertex();
        tessellator.draw();
        RenderSystem.shadeModel(GL11.GL_FLAT);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
        */

	}

	/**
	 * Draws a rectangle with a vertical gradient between the specified colors.
	 * x2 and y2 are not included.
	 * <p>
	 * Copied from McJtyLib
	 * https://github.com/McJtyMods/McJtyLib/blob/91606b81e1dace3d6e913505b74704f4e236b3f2/src/main/java/mcjty/lib/client/RenderHelper.java#L303
	 */
	private static void drawVerticalGradientRect(PoseStack pPoseStack, int x1, int y1, int x2, int y2, Color primary, Color secondary) {
		float zLevel = 0.0f;

		float[] pColors = primary.getRGBColorComponents(null);
		float pA = 1.0f;
		float pR = pColors[0];
		float pG = pColors[1];
		float pB = pColors[2];

		float[] sColors = secondary.getRGBColorComponents(null);
		float sA = 1.0f;
		float sR = sColors[0];
		float sG = sColors[1];
		float sB = sColors[2];

        /*
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.shadeModel(GL11.GL_SMOOTH);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(x2, y1, zLevel).color(pR, pG, pB, pA).endVertex();
        buffer.pos(x1, y1, zLevel).color(pR, pG, pB, pA).endVertex();
        buffer.pos(x1, y2, zLevel).color(sR, sG, sB, sA).endVertex();
        buffer.pos(x2, y2, zLevel).color(sR, sG, sB, sA).endVertex();
        tessellator.draw();

        RenderSystem.shadeModel(GL11.GL_FLAT);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
         */
	}
}
