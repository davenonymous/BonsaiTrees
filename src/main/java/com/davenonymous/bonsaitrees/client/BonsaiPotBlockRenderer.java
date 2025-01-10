package com.davenonymous.bonsaitrees.client;

import com.davenonymous.bonsaitrees.blocks.BonsaiPotBlock;
import com.davenonymous.bonsaitrees.blocks.BonsaiPotBlockEntity;
import com.davenonymous.bonsaitrees.client.multiblock.MultiBlockFakeLevel;
import com.davenonymous.bonsaitrees.client.multiblock.MultiBlockModel;
import com.davenonymous.bonsaitrees.setup.ModModelLoaders;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Matrix4f;

public class BonsaiPotBlockRenderer implements BlockEntityRenderer<BonsaiPotBlockEntity> {

	public BonsaiPotBlockRenderer(BlockEntityRendererProvider.Context context) {

	}

	@Override
	public void render(BonsaiPotBlockEntity pPotBlock, float pPartialTick, PoseStack poseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
		ModelManager modelManager = Minecraft.getInstance().getModelManager();
		BakedModel potModel = modelManager.getBlockModelShaper().getBlockModel(pPotBlock.getBlockState());
		Vec3 treeOffset = new Vec3(0, 0, 0);
		float treeMaxScale = 1.0f;
		if(potModel instanceof PotModel pot) {
			treeOffset = pot.treeOffset;
			treeMaxScale = pot.treeScale;
		}

		float progress = pPotBlock.getTreeGrowthProgress(pPartialTick);

		ModelData modelData = pPotBlock.getModelData();
		ResourceLocation saplingId = modelData.get(BonsaiPotBlock.SAPLING);
		if(saplingId != null && ModModelLoaders.MODEL_MAP.containsKey(saplingId)) {
			ModelResourceLocation model = ModModelLoaders.MODEL_MAP.get(saplingId);
			if(Minecraft.getInstance().getModelManager().getModel(model) instanceof MultiBlockModel multiBlockModel) {
				MultiBlockFakeLevel level = new MultiBlockFakeLevel(multiBlockModel, pPotBlock.getLevel(), pPotBlock.getBlockPos());
				VertexBuffer vertexBuffer = ModModelLoaders.getVbo(saplingId, multiBlockModel, level, pPotBlock.getBlockPos());
				if(vertexBuffer != null && !vertexBuffer.isInvalid()) {
					vertexBuffer.bind();
					poseStack.pushPose();

					poseStack.translate(treeOffset.x / 16.0, treeOffset.y / 16.0, treeOffset.z / 16.0);
					poseStack.scale(treeMaxScale, treeMaxScale, treeMaxScale);
					poseStack.scale(progress, progress, progress);
					Matrix4f modelMatrix = poseStack.last().pose();

					// Combine the camera transformation with the poseStack transformation
					Matrix4f viewMatrix = new Matrix4f(RenderSystem.getModelViewMatrix());
					viewMatrix.mul(modelMatrix);

					ModModelLoaders.renderTypeForModels.setupRenderState();
					ShaderInstance shader = RenderSystem.getShader();
					shader.setDefaultUniforms(VertexFormat.Mode.QUADS, viewMatrix, RenderSystem.getProjectionMatrix(), Minecraft.getInstance().getWindow());

					Level potLevel = pPotBlock.getLevel();
					float sunBrightness = 1 - ((ClientLevel) potLevel).getStarBrightness(pPartialTick); // Render brightness of the current world
					int skyLight = (pPackedLight >> 20) & 0xF; // 4 bits: sky light between 0 and 15
					int blockLight = (pPackedLight >> 4) & 0xF; // 4 bits: block light between 0 and 15

					// Turn them into a float between 0.0 and 1.0 to make it easier to work with
					float skyBrightness = skyLight / 15.0f;
					float blockBrightness = blockLight / 15.0f;

					// The light level that the block would receive from the sun
					// This skylight is the light level that the block receives from the sky, decreasing e.g. when under a roof
					// Multiply the sun brightness with the sky brightness to get the total brightness from the sun
					float sunReceivedLight = sunBrightness * skyBrightness;

					// If the light received from other blocks is brighter than the sun, use that instead
					float brighterBrightness = Math.max(sunReceivedLight, blockBrightness);

					// Clamp the brightness between 0.1 and 0.9 to avoid too dark or too bright blocks
					float lightLevel = Math.clamp(brighterBrightness, 0.1f, 0.9f);

					// Render the block with the calculated light level
					RenderSystem.setShaderColor(lightLevel, lightLevel, lightLevel, 1.0F);

					RenderSystem.setupShaderLights(shader);
					RenderSystem.enableDepthTest();
					RenderSystem.enableBlend();
					//RenderSystem.depthMask(true);

					Matrix4f projectionMatrix = RenderSystem.getProjectionMatrix();
					vertexBuffer.drawWithShader(viewMatrix, projectionMatrix, shader);

					RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
					VertexBuffer.unbind();
					poseStack.popPose();
				}

			}
		}

	}
}
