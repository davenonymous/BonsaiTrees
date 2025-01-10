package com.davenonymous.bonsaitrees.client.multiblock;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.BitSet;
import java.util.List;

public class MultiBlockRenderer extends ModelBlockRenderer {
	BlockAndTintGetter realLevel;
	BlockPos lightAndTintPosition;
	boolean isItemRender = false;

	public MultiBlockRenderer(BlockColors pBlockColors, BlockAndTintGetter realLevel, BlockPos lightAndTintPosition, boolean isItemRender) {
		super(pBlockColors);
		this.realLevel = realLevel;
		this.lightAndTintPosition = lightAndTintPosition;
		this.isItemRender = isItemRender;
	}

	@Override
	public void putQuadData(BlockAndTintGetter level, BlockState state, BlockPos pos, VertexConsumer consumer, PoseStack.Pose pose, BakedQuad quad, float brightness0, float brightness1, float brightness2, float brightness3, int lightmap0, int lightmap1, int lightmap2, int lightmap3, int packedOverlay) {
		if(quad instanceof MultiBlockBakedQuad q) {
			float r = 1.0f;
			float g = 1.0f;
			float b = 1.0f;
			if(quad.isTinted()) {
				int i = this.blockColors.getColor(q.state, level, pos, quad.getTintIndex());
				r = (float) (i >> 16 & 0xFF) / 255.0F;
				g = (float) (i >> 8 & 0xFF) / 255.0F;
				b = (float) (i & 0xFF) / 255.0F;
			}
			consumer.putBulkData(
				pose, quad, new float[]{brightness0, brightness1, brightness2, brightness3}, r, g, b, 1.0F, new int[]{lightmap0, lightmap1, lightmap2, lightmap3}, packedOverlay,
				true
			);
			return;
		}

		super.putQuadData(
			level, state, pos,
			consumer, pose,
			quad,
			brightness0, brightness1, brightness2, brightness3,
			lightmap0, lightmap1, lightmap2, lightmap3,
			packedOverlay
		);
	}

	@Override
	public void renderModelFaceAO(BlockAndTintGetter pLevel, BlockState pState, BlockPos pPos, PoseStack pPoseStack, VertexConsumer pConsumer, List<BakedQuad> pQuads, float[] pShape, BitSet pShapeFlags, AmbientOcclusionFace pAoFace, int pPackedOverlay) {
		float[] shape = new float[12];
		BitSet shapeFlags = new BitSet(3);
		shapeFlags.set(1, false);

		int levelColor = LevelRenderer.getLightColor(realLevel, lightAndTintPosition);
		for(BakedQuad bakedquad : pQuads) {
			BlockState state = pState;
			BlockPos pos = pPos;
			float[] brightness = pAoFace.brightness;
			int[] lightmap = pAoFace.lightmap;
			if(bakedquad instanceof MultiBlockBakedQuad q) {
				state = q.state;
				pos = q.pos;

				if(q.aoFace == null) {
					MultiBlockAmbientOcclusionFace face = new MultiBlockAmbientOcclusionFace();
					face.calculate(pLevel, state, pos, bakedquad.getDirection(), shape, shapeFlags, bakedquad.isShade());
					q.aoFace = face;
				}
				brightness = q.aoFace.brightness;
				lightmap = q.aoFace.lightmap;
			}


			var b1 = Math.min(1.0f, brightness[0] + 0.2f);
			var b2 = Math.min(1.0f, brightness[1] + 0.2f);
			var b3 = Math.min(1.0f, brightness[2] + 0.2f);
			var b4 = Math.min(1.0f, brightness[3] + 0.2f);
			var l1 = lightmap[0] + levelColor >> 1;
			var l2 = lightmap[1] + levelColor >> 1;
			var l3 = lightmap[2] + levelColor >> 1;
			var l4 = lightmap[3] + levelColor >> 1;
			if(isItemRender) {
				l1 = 0xFF00FF;
				l2 = 0xFF00FF;
				l3 = 0xFF00FF;
				l4 = 0xFF00FF;
			}
			this.putQuadData(
				pLevel, state, pos, pConsumer, pPoseStack.last(), bakedquad,
				b1, b2, b3, b4,
				l1, l2, l3, l4,
				pPackedOverlay
			);
		}
	}

	@Override
	public void renderModelFaceFlat(BlockAndTintGetter pLevel, BlockState pState, BlockPos pPos, int pPackedLight, int pPackedOverlay, boolean pRepackLight, PoseStack pPoseStack, VertexConsumer pConsumer, List<BakedQuad> pQuads, BitSet pShapeFlags) {
		float[] shape = new float[12];
		BitSet shapeFlags = new BitSet(3);

		for(BakedQuad bakedquad : pQuads) {
			var state = pState;
			var pos = pPos;
			if(bakedquad instanceof MultiBlockBakedQuad q) {
				state = q.state;
				pos = q.pos;
			}

			if(pRepackLight) {
				this.calculateShape(pLevel, state, pos, bakedquad.getVertices(), bakedquad.getDirection(), shape, shapeFlags);
				BlockPos blockpos = shapeFlags.get(0) ? pos.relative(bakedquad.getDirection()) : pos;
				pPackedLight = LevelRenderer.getLightColor(pLevel, state, blockpos);
			}

			float f = pLevel.getShade(bakedquad.getDirection(), bakedquad.isShade());
			this.putQuadData(pLevel, state, pos, pConsumer, pPoseStack.last(), bakedquad, f, f, f, f, pPackedLight, pPackedLight, pPackedLight, pPackedLight, pPackedOverlay);
		}

	}
}