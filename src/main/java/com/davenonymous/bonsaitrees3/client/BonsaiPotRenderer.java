package com.davenonymous.bonsaitrees3.client;

import com.davenonymous.bonsaitrees3.blocks.BonsaiPotBlockEntity;
import com.davenonymous.bonsaitrees3.libnonymous.render.MultiModelBlockRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class BonsaiPotRenderer implements BlockEntityRenderer<BonsaiPotBlockEntity> {
	public static final ResourceLocation WATER = new ResourceLocation("minecraft", "block/water_still");

	public BonsaiPotRenderer(BlockEntityRendererProvider.Context context) {
	}

	@Override
	public void render(BonsaiPotBlockEntity pPotBlock, float pPartialTick, PoseStack poseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
		if(!pPotBlock.hasSapling() || !pPotBlock.hasSoil()) {
			return;
		}

		if(pPotBlock.getLevel() == null) {
			return;
		}

		var saplingInfo = pPotBlock.getSaplingInfo();
		if(saplingInfo == null) {
			return;
		}

		var multiBlock = TreeModels.get(saplingInfo.getId());
		if(multiBlock == null) {
			return;
		}

		poseStack.pushPose();
		poseStack.translate(0.5f, 0.1f, 0.5f);

		float scale = (float) multiBlock.getScaleRatio(true);
		poseStack.scale(scale, scale, scale);

		float maxSize = 0.9f;
		poseStack.scale(maxSize, maxSize, maxSize);

		float progress = (float) pPotBlock.getProgress(pPartialTick);
		poseStack.scale(progress, progress, progress);

		float rotate = pPotBlock.modelRotation * 90.0f;
		poseStack.mulPose(new Quaternion(Vector3f.YP, rotate, true));

		float translateOffsetX = (float) (multiBlock.width + 1) / 2.0f;
		float translateOffsetY = 0.0f;
		float translateOffsetZ = (float) (multiBlock.depth + 1) / 2.0f;
		poseStack.translate(-translateOffsetX, -translateOffsetY, -translateOffsetZ);

		var buffer = pBufferSource.getBuffer(RenderType.translucent());
		MultiModelBlockRenderer.renderMultiBlockModel(multiBlock, pPotBlock.getLevel(), buffer, poseStack, pPackedLight);

		poseStack.popPose();
	}
}