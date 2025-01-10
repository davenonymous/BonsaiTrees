package com.davenonymous.bonsaitrees.lib.gui.tooltip;

import com.davenonymous.bonsaitrees.client.multiblock.MultiBlockFakeLevel;
import com.davenonymous.bonsaitrees.client.multiblock.MultiBlockModel;
import com.davenonymous.bonsaitrees.setup.ModModelLoaders;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.opengl.GL11;

public class MultiBlockModelTooltipComponent implements TooltipComponent, ClientTooltipComponent {
	private final ResourceLocation modelId;
	private final MultiBlockModel multiBlockModel;
	private final int width;
	private final int height;

	public MultiBlockModelTooltipComponent(ResourceLocation modelId, int width, int height) {
		this.modelId = modelId;
		ModelResourceLocation treeModelId = ModModelLoaders.MODEL_MAP.get(modelId);
		this.multiBlockModel = (MultiBlockModel) Minecraft.getInstance().getModelManager().getModel(treeModelId);
		this.width = width;
		this.height = height;
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
		guiGraphics.pose().pushPose();

		guiGraphics.pose().translate(x, y, 100.0f);
		guiGraphics.pose().translate(this.width / 2.0f, this.height - 8, 0);
		guiGraphics.pose().scale(32f, 32f, 32f);

		guiGraphics.pose().mulPose(new Quaternionf(new AxisAngle4f((float) Math.toRadians(-25.0f + 180.0f), 1, 0, 0)));

		long gameTicks = Minecraft.getInstance().level.getGameTime();
		float foo = Minecraft.getInstance().getTimer().getGameTimeDeltaTicks();
		guiGraphics.pose().mulPose(new Quaternionf(new AxisAngle4f((float) Math.toRadians((double) gameTicks + foo), 0, 1, 0)));

		MultiBlockFakeLevel fakeLevel = new MultiBlockFakeLevel(multiBlockModel, Minecraft.getInstance().level, BlockPos.ZERO);
		VertexBuffer treeVbo = ModModelLoaders.getVbo(modelId, multiBlockModel, fakeLevel, BlockPos.ZERO);
		if(treeVbo != null && !treeVbo.isInvalid()) {
			Matrix4f viewMatrix = new Matrix4f(RenderSystem.getModelViewMatrix());
			Matrix4f projectionMatrix = RenderSystem.getProjectionMatrix();
			viewMatrix.mul(guiGraphics.pose().last().pose());

			ModModelLoaders.renderTypeForModels.setupRenderState();
			ShaderInstance shader = RenderSystem.getShader();
			shader.setDefaultUniforms(VertexFormat.Mode.QUADS, viewMatrix, projectionMatrix, Minecraft.getInstance().getWindow());
			RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0F);
			RenderSystem.setupShaderLights(shader);
			RenderSystem.enableDepthTest();
			RenderSystem.disableBlend();
			RenderSystem.depthMask(true);

			treeVbo.bind();
			GL11.glFrontFace(GL11.GL_CW);
			treeVbo.drawWithShader(viewMatrix, projectionMatrix, shader);
			GL11.glFrontFace(GL11.GL_CCW);
			VertexBuffer.unbind();
		}

		guiGraphics.pose().popPose();
	}

}
