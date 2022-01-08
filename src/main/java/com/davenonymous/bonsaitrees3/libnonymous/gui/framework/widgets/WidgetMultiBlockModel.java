package com.davenonymous.bonsaitrees3.libnonymous.gui.framework.widgets;


import com.davenonymous.bonsaitrees3.libnonymous.serialization.MultiblockBlockModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;

public class WidgetMultiBlockModel extends Widget {
	private MultiblockBlockModel model;

	public WidgetMultiBlockModel(MultiblockBlockModel model) {
		this.model = model;
	}

	@Override
	public void draw(PoseStack pPoseStack, Screen screen) {
        /*
        float angle = RenderTickCounter.renderTicks * 45.0f / 128.0f;

        RenderSystem.pushMatrix();

        // Init RenderSystem
        RenderSystem.enableAlphaTest();
        RenderSystem.alphaFunc(GL11.GL_GREATER, 0.1f);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);

        RenderSystem.disableFog();
        RenderSystem.disableLighting();
        RenderHelper.disableStandardItemLighting();

        RenderSystem.enableBlend();
        RenderSystem.enableCull();
        RenderSystem.enableAlphaTest();

        if (Minecraft.isAmbientOcclusionEnabled()) {
            RenderSystem.shadeModel(GL11.GL_SMOOTH);
        } else {
            RenderSystem.shadeModel(GL11.GL_FLAT);
        }

        RenderSystem.disableRescaleNormal();

        // Bring to front
        RenderSystem.translatef(0F, 0F, 216.5F);

        double scaledWidth = this.width * 1.4d;
        double scaledHeight = this.height * 1.4d;

        RenderSystem.translated(scaledWidth / 2.0f, scaledHeight / 2.0f, 0.0d);

        // Shift it a bit down so one can properly see 3d
        RenderSystem.rotatef(-25.0f, 1.0f, 0.0f, 0.0f);

        // Rotate per our calculated time
        RenderSystem.rotatef(angle, 0.0f, 1.0f, 0.0f);

        double scale = model.getScaleRatio(true);
        RenderSystem.scaled(scale, scale, scale);

        RenderSystem.scaled(scaledWidth, scaledWidth, scaledWidth);


        RenderSystem.rotatef(180.0f, 1.0f, 0.0f, 0.0f);

        RenderSystem.translatef(
                (model.width + 1) / -2.0f,
                (model.height + 1) / -2.0f,
                (model.depth + 1) / -2.0f
        );

        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        textureManager.getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmapDirect(false, false);

        GL11.glFrontFace(GL11.GL_CW);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        IRenderTypeBuffer buffer = IRenderTypeBuffer.getImpl(builder);

        // TODO: Do not render with players position
        MultiblockBlockModelRenderer.renderModel(this.model, new MatrixStack(), buffer, 15728880,  OverlayTexture.DEFAULT_LIGHT, Libnonymous.proxy.getClientWorld(), Libnonymous.proxy.getClientPlayer().getPosition());

        textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        textureManager.getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();

        ((IRenderTypeBuffer.Impl) buffer).finish();

        GL11.glFrontFace(GL11.GL_CCW);

        RenderSystem.disableBlend();

        RenderSystem.popMatrix();
         */
	}
}
