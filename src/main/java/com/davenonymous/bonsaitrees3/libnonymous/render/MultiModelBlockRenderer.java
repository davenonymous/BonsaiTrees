package com.davenonymous.bonsaitrees3.libnonymous.render;

import com.davenonymous.bonsaitrees3.libnonymous.serialization.MultiblockBlockModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;

import javax.annotation.Nullable;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class MultiModelBlockRenderer {
	static final Direction[] DIRECTIONS = Direction.values();
	private final BlockColors blockColors;

	public static void renderMultiBlockModel(MultiblockBlockModel model, BlockAndTintGetter pRealWorld, VertexConsumer buffer, PoseStack pose, int pPackedLight) {
		var baked = MultiblockBakedModel.of(model);
		MultiBlockBlockColors blockColors = new MultiBlockBlockColors(model);
		MultiBlockBlockAndTintGetter fakeLevel = new MultiBlockBlockAndTintGetter(model, pRealWorld);

		MultiModelBlockRenderer renderer = new MultiModelBlockRenderer(blockColors);

		renderer.tesselateWithoutAO(fakeLevel, baked, Blocks.DIRT.defaultBlockState(), BlockPos.ZERO, pose, buffer, pPackedLight, new Random(), OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
	}

	public MultiModelBlockRenderer(BlockColors pBlockColors) {
		this.blockColors = pBlockColors;
	}


	public boolean tesselateWithoutAO(BlockAndTintGetter pLevel, BakedModel pModel, BlockState pState, BlockPos pPos, PoseStack pPoseStack, VertexConsumer pConsumer, int pPackedLight, Random pRandom, int pPackedOverlay, net.minecraftforge.client.model.data.IModelData modelData) {
		BitSet bitset = new BitSet(3);

		List<BakedQuad> list1 = pModel.getQuads(pState, (Direction) null, pRandom, modelData);
		if(!list1.isEmpty()) {
			this.renderModelFaceFlat(pLevel, pState, pPos, pPackedLight, pPackedOverlay, false, pPoseStack, pConsumer, list1, bitset);
			return true;
		}

		return false;
	}

	private void putQuadData(BlockAndTintGetter pLevel, BlockState pState, BlockPos pPos, VertexConsumer pConsumer, PoseStack.Pose pPose, BakedQuad pQuad, float pBrightness0, float pBrightness1, float pBrightness2, float pBrightness3, int pLightmap0, int pLightmap1, int pLightmap2, int pLightmap3, int pPackedOverlay) {
		float f;
		float f1;
		float f2;
		if(pQuad.isTinted()) {
			int i;
			if(pQuad instanceof TintedBakedQuad tintedBakedQuad) {
				i = this.blockColors.getColor(tintedBakedQuad.state, pLevel, tintedBakedQuad.pos, pQuad.getTintIndex());
			} else {
				i = this.blockColors.getColor(pState, pLevel, pPos, pQuad.getTintIndex());
			}
			f = (float) (i >> 16 & 255) / 255.0F;
			f1 = (float) (i >> 8 & 255) / 255.0F;
			f2 = (float) (i & 255) / 255.0F;
		} else {
			f = 1.0F;
			f1 = 1.0F;
			f2 = 1.0F;
		}

		pConsumer.putBulkData(pPose, pQuad, new float[]{pBrightness0, pBrightness1, pBrightness2, pBrightness3}, f, f1, f2, new int[]{pLightmap0, pLightmap1, pLightmap2, pLightmap3}, pPackedOverlay, true);
	}

	/**
	 * Calculates the shape and corresponding flags for the specified {@code direction} and {@code vertices}, storing the
	 * resulting shape in the specified {@code shape} array and the shape flags in {@code shapeFlags}.
	 *
	 * @param pShape      the array, of length 12, to store the shape bounds in, or {@code null} to only calculate shape flags
	 * @param pShapeFlags the bit set to store the shape flags in. The first bit will be {@code true} if the face should
	 *                    be offset, and the second if the face is less than a block in width and height.
	 */
	private void calculateShape(BlockAndTintGetter pLevel, BlockState pState, BlockPos pPos, int[] pVertices, Direction pDirection, @Nullable float[] pShape, BitSet pShapeFlags) {
		float f = 32.0F;
		float f1 = 32.0F;
		float f2 = 32.0F;
		float f3 = -32.0F;
		float f4 = -32.0F;
		float f5 = -32.0F;

		for(int i = 0; i < 4; ++i) {
			float f6 = Float.intBitsToFloat(pVertices[i * 8]);
			float f7 = Float.intBitsToFloat(pVertices[i * 8 + 1]);
			float f8 = Float.intBitsToFloat(pVertices[i * 8 + 2]);
			f = Math.min(f, f6);
			f1 = Math.min(f1, f7);
			f2 = Math.min(f2, f8);
			f3 = Math.max(f3, f6);
			f4 = Math.max(f4, f7);
			f5 = Math.max(f5, f8);
		}

		if(pShape != null) {
			pShape[Direction.WEST.get3DDataValue()] = f;
			pShape[Direction.EAST.get3DDataValue()] = f3;
			pShape[Direction.DOWN.get3DDataValue()] = f1;
			pShape[Direction.UP.get3DDataValue()] = f4;
			pShape[Direction.NORTH.get3DDataValue()] = f2;
			pShape[Direction.SOUTH.get3DDataValue()] = f5;
			int j = DIRECTIONS.length;
			pShape[Direction.WEST.get3DDataValue() + j] = 1.0F - f;
			pShape[Direction.EAST.get3DDataValue() + j] = 1.0F - f3;
			pShape[Direction.DOWN.get3DDataValue() + j] = 1.0F - f1;
			pShape[Direction.UP.get3DDataValue() + j] = 1.0F - f4;
			pShape[Direction.NORTH.get3DDataValue() + j] = 1.0F - f2;
			pShape[Direction.SOUTH.get3DDataValue() + j] = 1.0F - f5;
		}

		float f9 = 1.0E-4F;
		float f10 = 0.9999F;
		switch(pDirection) {
			case DOWN:
				pShapeFlags.set(1, f >= 1.0E-4F || f2 >= 1.0E-4F || f3 <= 0.9999F || f5 <= 0.9999F);
				pShapeFlags.set(0, f1 == f4 && (f1 < 1.0E-4F || pState.isCollisionShapeFullBlock(pLevel, pPos)));
				break;
			case UP:
				pShapeFlags.set(1, f >= 1.0E-4F || f2 >= 1.0E-4F || f3 <= 0.9999F || f5 <= 0.9999F);
				pShapeFlags.set(0, f1 == f4 && (f4 > 0.9999F || pState.isCollisionShapeFullBlock(pLevel, pPos)));
				break;
			case NORTH:
				pShapeFlags.set(1, f >= 1.0E-4F || f1 >= 1.0E-4F || f3 <= 0.9999F || f4 <= 0.9999F);
				pShapeFlags.set(0, f2 == f5 && (f2 < 1.0E-4F || pState.isCollisionShapeFullBlock(pLevel, pPos)));
				break;
			case SOUTH:
				pShapeFlags.set(1, f >= 1.0E-4F || f1 >= 1.0E-4F || f3 <= 0.9999F || f4 <= 0.9999F);
				pShapeFlags.set(0, f2 == f5 && (f5 > 0.9999F || pState.isCollisionShapeFullBlock(pLevel, pPos)));
				break;
			case WEST:
				pShapeFlags.set(1, f1 >= 1.0E-4F || f2 >= 1.0E-4F || f4 <= 0.9999F || f5 <= 0.9999F);
				pShapeFlags.set(0, f == f3 && (f < 1.0E-4F || pState.isCollisionShapeFullBlock(pLevel, pPos)));
				break;
			case EAST:
				pShapeFlags.set(1, f1 >= 1.0E-4F || f2 >= 1.0E-4F || f4 <= 0.9999F || f5 <= 0.9999F);
				pShapeFlags.set(0, f == f3 && (f3 > 0.9999F || pState.isCollisionShapeFullBlock(pLevel, pPos)));
		}

	}

	/**
	 * @param pRepackLight {@code true} if packed light should be re-calculated
	 * @param pShapeFlags  the bit set to store the shape flags in. The first bit will be {@code true} if the face should
	 *                     be offset, and the second if the face is less than a block in width and height.
	 */
	private void renderModelFaceFlat(BlockAndTintGetter pLevel, BlockState pState, BlockPos pPos, int pPackedLight, int pPackedOverlay, boolean pRepackLight, PoseStack pPoseStack, VertexConsumer pConsumer, List<BakedQuad> pQuads, BitSet pShapeFlags) {
		for(BakedQuad bakedquad : pQuads) {
			if(pRepackLight) {
				this.calculateShape(pLevel, pState, pPos, bakedquad.getVertices(), bakedquad.getDirection(), (float[]) null, pShapeFlags);
				BlockPos blockpos = pShapeFlags.get(0) ? pPos.relative(bakedquad.getDirection()) : pPos;
				pPackedLight = LevelRenderer.getLightColor(pLevel, pState, blockpos);
			}

			float f = pLevel.getShade(bakedquad.getDirection(), bakedquad.isShade());
			this.putQuadData(pLevel, pState, pPos, pConsumer, pPoseStack.last(), bakedquad, f, f, f, f, pPackedLight, pPackedLight, pPackedLight, pPackedLight, pPackedOverlay);
		}

	}
}