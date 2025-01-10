package com.davenonymous.bonsaitrees.client.multiblock;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.BitSet;

@OnlyIn(Dist.CLIENT)
public class MultiBlockAmbientOcclusionFace {
	public final float[] brightness = new float[4];
	public final int[] lightmap = new int[4];

	public MultiBlockAmbientOcclusionFace() {
	}

	public void calculate(BlockAndTintGetter level, BlockState state, BlockPos pos, Direction direction, float[] shape, BitSet shapeFlags, boolean shade) {
		BlockPos blockpos = shapeFlags.get(0) ? pos.relative(direction) : pos;
		ModelBlockRenderer.AdjacencyInfo adjacent = ModelBlockRenderer.AdjacencyInfo.fromFacing(direction);
		BlockPos.MutableBlockPos nowPos = new BlockPos.MutableBlockPos();
		nowPos.setWithOffset(blockpos, adjacent.corners[0]);
		BlockState blockstate = level.getBlockState(nowPos);

		int i = LevelRenderer.getLightColor(level, blockstate, nowPos);
		float f = blockstate.getShadeBrightness(level, nowPos);
		nowPos.setWithOffset(blockpos, adjacent.corners[1]);
		BlockState blockstate1 = level.getBlockState(nowPos);
		int j = LevelRenderer.getLightColor(level, blockstate1, nowPos);
		float f1 = blockstate1.getShadeBrightness(level, nowPos);
		nowPos.setWithOffset(blockpos, adjacent.corners[2]);
		BlockState blockstate2 = level.getBlockState(nowPos);
		int k = LevelRenderer.getLightColor(level, blockstate2, nowPos);
		float f2 = blockstate2.getShadeBrightness(level, nowPos);
		nowPos.setWithOffset(blockpos, adjacent.corners[3]);
		BlockState blockstate3 = level.getBlockState(nowPos);
		int l = LevelRenderer.getLightColor(level, blockstate3, nowPos);
		float f3 = blockstate3.getShadeBrightness(level, nowPos);
		BlockState blockstate4 = level.getBlockState(nowPos.setWithOffset(blockpos, adjacent.corners[0]));
		boolean flag = !blockstate4.isViewBlocking(level, nowPos) || blockstate4.getLightBlock(level, nowPos) == 0;
		BlockState blockstate5 = level.getBlockState(nowPos.setWithOffset(blockpos, adjacent.corners[1]));
		boolean flag1 = !blockstate5.isViewBlocking(level, nowPos) || blockstate5.getLightBlock(level, nowPos) == 0;
		BlockState blockstate6 = level.getBlockState(nowPos.setWithOffset(blockpos, adjacent.corners[2]));
		boolean flag2 = !blockstate6.isViewBlocking(level, nowPos) || blockstate6.getLightBlock(level, nowPos) == 0;
		BlockState blockstate7 = level.getBlockState(nowPos.setWithOffset(blockpos, adjacent.corners[3]));
		boolean flag3 = !blockstate7.isViewBlocking(level, nowPos) || blockstate7.getLightBlock(level, nowPos) == 0;
		float f4;
		int i1;
		if(!flag2 && !flag) {
			f4 = f;
			i1 = i;
		} else {
			nowPos.setWithOffset(blockpos, adjacent.corners[0]).move(adjacent.corners[2]);
			BlockState blockstate8 = level.getBlockState(nowPos);
			f4 = blockstate8.getShadeBrightness(level, nowPos);
			i1 = LevelRenderer.getLightColor(level, blockstate8, nowPos);
			;
		}

		int j1;
		float f5;
		if(!flag3 && !flag) {
			f5 = f;
			j1 = i;
		} else {
			nowPos.setWithOffset(blockpos, adjacent.corners[0]).move(adjacent.corners[3]);
			BlockState blockstate10 = level.getBlockState(nowPos);
			f5 = blockstate10.getShadeBrightness(level, nowPos);
			j1 = LevelRenderer.getLightColor(level, blockstate10, nowPos);
			;
		}

		int k1;
		float f6;
		if(!flag2 && !flag1) {
			f6 = f;
			k1 = i;
		} else {
			nowPos.setWithOffset(blockpos, adjacent.corners[1]).move(adjacent.corners[2]);
			BlockState blockstate11 = level.getBlockState(nowPos);
			f6 = blockstate11.getShadeBrightness(level, nowPos);
			k1 = LevelRenderer.getLightColor(level, blockstate11, nowPos);
			;
		}

		int l1;
		float f7;
		if(!flag3 && !flag1) {
			f7 = f;
			l1 = i;
		} else {
			nowPos.setWithOffset(blockpos, adjacent.corners[1]).move(adjacent.corners[3]);
			BlockState blockstate12 = level.getBlockState(nowPos);
			f7 = blockstate12.getShadeBrightness(level, nowPos);
			l1 = LevelRenderer.getLightColor(level, blockstate12, nowPos);
		}

		int i3 = LevelRenderer.getLightColor(level, state, nowPos);
		nowPos.setWithOffset(pos, direction);
		BlockState blockstate9 = level.getBlockState(nowPos);
		if(shapeFlags.get(0) || !blockstate9.isSolidRender(level, nowPos)) {
			i3 = LevelRenderer.getLightColor(level, blockstate9, nowPos);
		}
		i3 = 0;

		float f8 = shapeFlags.get(0)
				   ? level.getBlockState(blockpos).getShadeBrightness(level, blockpos)
				   : level.getBlockState(pos).getShadeBrightness(level, pos);
		ModelBlockRenderer.AmbientVertexRemap vertexRemap = ModelBlockRenderer.AmbientVertexRemap.fromFacing(direction);
		float f30;
		float f10;
		float f11;
		float f12;

		f30 = (f3 + f + f5 + f8) * 0.25F;
		f10 = (f2 + f + f4 + f8) * 0.25F;
		f11 = (f2 + f1 + f6 + f8) * 0.25F;
		f12 = (f3 + f1 + f7 + f8) * 0.25F;
		this.lightmap[vertexRemap.vert0] = this.blend(l, i, j1, i3);
		this.lightmap[vertexRemap.vert1] = this.blend(k, i, i1, i3);
		this.lightmap[vertexRemap.vert2] = this.blend(k, j, k1, i3);
		this.lightmap[vertexRemap.vert3] = this.blend(l, j, l1, i3);
		this.brightness[vertexRemap.vert0] = f30;
		this.brightness[vertexRemap.vert1] = f10;
		this.brightness[vertexRemap.vert2] = f11;
		this.brightness[vertexRemap.vert3] = f12;

		//		f30 = level.getShade(direction, shade);
		//
		//		for(int j3 = 0; j3 < this.brightness.length; ++j3) {
		//			this.brightness[j3] *= f30;
		//		}

	}

	private int blend(int lightColor0, int lightColor1, int lightColor2, int lightColor3) {
		if(lightColor0 == 0) {
			lightColor0 = lightColor3;
		}

		if(lightColor1 == 0) {
			lightColor1 = lightColor3;
		}

		if(lightColor2 == 0) {
			lightColor2 = lightColor3;
		}

		return lightColor0 + lightColor1 + lightColor2 + lightColor3 >> 2 & 16711935;
	}
}