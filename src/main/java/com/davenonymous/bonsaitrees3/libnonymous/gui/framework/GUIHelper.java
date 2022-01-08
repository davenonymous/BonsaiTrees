package com.davenonymous.bonsaitrees3.libnonymous.gui.framework;


import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GUIHelper {
	public static void drawStringCentered(PoseStack pPoseStack, String str, Screen screen, float x, float y, int color) {
		Font renderer = screen.getMinecraft().font;
		float xPos = x - ((float) renderer.width(str) / 2.0f);
		var old = RenderSystem.getShader();
		renderer.draw(pPoseStack, str, xPos, y, color);
		RenderSystem.setShader(() -> old);
	}

	public static void drawSplitStringCentered(PoseStack pPoseStack, String str, Screen screen, int x, int y, int width, int color) {
		Font renderer = screen.getMinecraft().font;
		int yOffset = 0;

		for(FormattedText row : renderer.getSplitter().splitLines(str, width, Style.EMPTY)) {
			drawStringCentered(pPoseStack, row.getString(), screen, x + width / 2, y + yOffset, color);
			yOffset += renderer.lineHeight;
		}
	}

	public static void drawColoredRectangle(PoseStack pPoseStack, int x, int y, int width, int height, int argb) {
		int a = (argb >> 24) & 0xFF;
		int r = (argb >> 16) & 0xFF;
		int g = (argb >> 8) & 0xFF;
		int b = (argb & 0xFF);
		drawColoredRectangle(pPoseStack, x, y, width, height, r, g, b, a);
	}

	public static void drawColoredRectangle(PoseStack pPoseStack, int x, int y, int width, int height, int red, int green, int blue, int alpha) {
		float zLevel = 0.0f;

		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.disableDepthTest();
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

		var tesselator = Tesselator.getInstance();
		var builder = tesselator.getBuilder();

		builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		Matrix4f matrix = pPoseStack.last().pose();

		builder.vertex(matrix, (x + 0), (y + 0), zLevel).color(red, green, blue, alpha).endVertex();
		builder.vertex(matrix, (x + 0), (y + height), zLevel).color(red, green, blue, alpha).endVertex();
		builder.vertex(matrix, (x + width), (y + height), zLevel).color(red, green, blue, alpha).endVertex();
		builder.vertex(matrix, (x + width), (y + 0), zLevel).color(red, green, blue, alpha).endVertex();

		tesselator.end();

		RenderSystem.disableBlend();
		RenderSystem.enableDepthTest();
		RenderSystem.enableTexture();
	}

	public static void drawStretchedTexture(PoseStack pPoseStack, int x, int y, int width, int height, int textureX, int textureY, int textureWidth, int textureHeight) {
		final float uScale = 1f / 0x100;
		final float vScale = 1f / 0x100;

		float zLevel = 0.0f;

		var tesselator = Tesselator.getInstance();
		var builder = tesselator.getBuilder();

		builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		Matrix4f matrix = pPoseStack.last().pose();

		builder.vertex(matrix, (float) x, (float) (y + height), zLevel).uv((float) textureX * uScale, (float) (textureY + textureHeight) * vScale).endVertex();
		builder.vertex(matrix, (float) (x + width), (float) (y + height), zLevel).uv((float) (textureX + textureWidth) * uScale, (float) (textureY + textureHeight) * vScale).endVertex();
		builder.vertex(matrix, (float) (x + width), (float) y, zLevel).uv((float) (textureX + textureWidth) * uScale, (float) textureY * vScale).endVertex();
		builder.vertex(matrix, (float) x, (float) y, zLevel).uv((float) textureX * uScale, (float) textureY * vScale).endVertex();
		tesselator.end();
	}

	public static void drawModalRectWithCustomSizedTexture(PoseStack pPoseStack, int x, int y, float u, float v, int width, int height, float textureWidth, float textureHeight) {
		float f = 1.0F / textureWidth;
		float f1 = 1.0F / textureHeight;

		var tesselator = Tesselator.getInstance();
		var builder = tesselator.getBuilder();

		builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		Matrix4f matrix = pPoseStack.last().pose();

		builder.vertex(matrix, (float) x, (float) (y + height), 0.0f).uv((u * f), ((v + (float) height) * f1)).endVertex();
		builder.vertex(matrix, (float) (x + width), (float) (y + height), 0.0f).uv(((u + (float) width) * f), ((v + (float) height) * f1)).endVertex();
		builder.vertex(matrix, (float) (x + width), (float) y, 0.0f).uv(((u + (float) width) * f), (v * f1)).endVertex();
		builder.vertex(matrix, (float) x, (float) y, 0.0f).uv((u * f), (v * f1)).endVertex();

		tesselator.end();
	}

	public static void renderGuiItem(ItemStack pStack, int pX, int pY, boolean blackOut) {
		var pBakedmodel = Minecraft.getInstance().getItemRenderer().getModel(pStack, (Level) null, (LivingEntity) null, 0);
		Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);
		RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		PoseStack posestack = RenderSystem.getModelViewStack();
		posestack.pushPose();
		posestack.translate((double) pX, (double) pY, (double) (100.0F));
		posestack.translate(8.0D, 8.0D, 0.0D);
		posestack.scale(1.0F, -1.0F, 1.0F);
		posestack.scale(16.0F, 16.0F, 16.0F);
		RenderSystem.applyModelViewMatrix();
		PoseStack posestack1 = new PoseStack();
		MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
		boolean flag = !pBakedmodel.usesBlockLight();
		if(flag) {
			Lighting.setupForFlatItems();
		}

		Minecraft.getInstance().getItemRenderer().render(pStack, ItemTransforms.TransformType.GUI, false, posestack1, multibuffersource$buffersource, blackOut ? 0 : 15728880, OverlayTexture.NO_OVERLAY, pBakedmodel);
		multibuffersource$buffersource.endBatch();
		RenderSystem.enableDepthTest();
		if(flag) {
			Lighting.setupFor3DItems();
		}

		posestack.popPose();
		RenderSystem.applyModelViewMatrix();
	}


	/**
	 * <p>Fills a specified area on the screen with the provided {@link TextureAtlasSprite}.</p>
	 *
	 * @param icon   The {@link TextureAtlasSprite} to be displayed
	 * @param x      The X coordinate to start drawing from
	 * @param y      The Y coordinate to start drawing form
	 * @param width  The width of the provided icon to draw on the screen
	 * @param height The height of the provided icon to draw on the screen
	 */
	public static void fillAreaWithIcon(PoseStack pPoseStack, TextureAtlasSprite icon, int x, int y, int width, int height) {
		Tesselator t = Tesselator.getInstance();
		BufferBuilder b = t.getBuilder();

		b.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

		float zLevel = 0.0f;

		int iconWidth = icon.getWidth();
		int iconHeight = icon.getHeight();

		// number of rows & cols of full size icons
		int fullCols = width / iconWidth;
		int fullRows = height / iconHeight;

		float minU = icon.getU0();
		float maxU = icon.getU1();
		float minV = icon.getV0();
		float maxV = icon.getV1();

		int excessWidth = width % iconWidth;
		int excessHeight = height % iconHeight;

		// interpolated max u/v for the excess row / col
		float partialMaxU = minU + (maxU - minU) * ((float) excessWidth / iconWidth);
		float partialMaxV = minV + (maxV - minV) * ((float) excessHeight / iconHeight);

		int xNow;
		int yNow;
		for(int row = 0; row < fullRows; row++) {
			yNow = y + row * iconHeight;
			for(int col = 0; col < fullCols; col++) {
				// main part, only full icons
				xNow = x + col * iconWidth;
				drawRect(pPoseStack, xNow, yNow, iconWidth, iconHeight, zLevel, minU, minV, maxU, maxV);
			}
			if(excessWidth != 0) {
				// last not full width column in every row at the end
				xNow = x + fullCols * iconWidth;
				drawRect(pPoseStack, xNow, yNow, iconWidth, iconHeight, zLevel, minU, minV, maxU, maxV);
			}
		}
		if(excessHeight != 0) {
			// last not full height row
			for(int col = 0; col < fullCols; col++) {
				xNow = x + col * iconWidth;
				yNow = y + fullRows * iconHeight;
				drawRect(pPoseStack, xNow, yNow, iconWidth, excessHeight, zLevel, minU, minV, maxU, partialMaxV);
			}
			if(excessWidth != 0) {
				// missing quad in the bottom right corner of neither full height nor full width
				xNow = x + fullCols * iconWidth;
				yNow = y + fullRows * iconHeight;
				drawRect(pPoseStack, xNow, yNow, excessWidth, excessHeight, zLevel, minU, minV, partialMaxU, partialMaxV);
			}
		}

		t.end();
	}


	private static void drawRect(PoseStack pPoseStack, float x, float y, float width, float height, float z, float u, float v, float maxU, float maxV) {
		BufferBuilder b = Tesselator.getInstance().getBuilder();

        /*
        b.pos(x, y + height, z).tex(u, maxV).endVertex();
        b.pos(x + width, y + height, z).tex(maxU, maxV).endVertex();
        b.pos(x + width, y, z).tex(maxU, v).endVertex();
        b.pos(x, y, z).tex(u, v).endVertex();
         */
	}
}
