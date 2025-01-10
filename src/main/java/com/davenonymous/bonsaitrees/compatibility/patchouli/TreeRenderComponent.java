package com.davenonymous.bonsaitrees.compatibility.patchouli;


import com.davenonymous.bonsaitrees.client.multiblock.MultiBlockFakeLevel;
import com.davenonymous.bonsaitrees.client.multiblock.MultiBlockModel;
import com.davenonymous.bonsaitrees.setup.ModModelLoaders;
import com.davenonymous.bonsaitrees.setup.cache.BonsaiCache;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.opengl.GL11;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.IVariable;

import java.util.function.UnaryOperator;

public class TreeRenderComponent implements ICustomComponent {
	private transient int x, y;
	private transient ItemStack sapling;
	private transient MultiBlockModel treeModel;

	public IVariable item;
	public float scale = 100.0f;

	@Override
	public void build(int componentX, int componentY, int pageNum) {
		this.x = componentX;
		this.y = componentY;
	}

	@Override
	public void render(GuiGraphics guiGraphics, IComponentRenderContext context, float pticks, int mouseX, int mouseY) {
		ResourceLocation modelId = BonsaiCache.BONSAI_BY_ITEM.get(this.sapling.getItem()).model();
		ModelResourceLocation treeModelId = ModModelLoaders.MODEL_MAP.get(modelId);
		MultiBlockModel multiBlockModel = (MultiBlockModel) Minecraft.getInstance().getModelManager().getModel(treeModelId);

		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate(x + 50, y + 100, 100.0f);

		guiGraphics.pose().scale(scale, scale, scale);

		guiGraphics.pose().mulPose(new Quaternionf(new AxisAngle4f((float) Math.toRadians(-25.0f + 180.0f), 1, 0, 0)));

		int rotationDurationTicks = 15 * 20;
		long worldTicks = Minecraft.getInstance().level.getGameTime();
		int smallWorldTicks = (int) (worldTicks % rotationDurationTicks);
		double progressTicks = smallWorldTicks + pticks;
		double percent = progressTicks / rotationDurationTicks;
		guiGraphics.pose().mulPose(new Quaternionf(new AxisAngle4f((float) Math.toRadians(percent * 360), 0, 1, 0)));

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

	@Override
	public void onVariablesAvailable(UnaryOperator<IVariable> lookup, HolderLookup.Provider registries) {
		this.sapling = lookup.apply(item).as(ItemStack.class);
	}
}
