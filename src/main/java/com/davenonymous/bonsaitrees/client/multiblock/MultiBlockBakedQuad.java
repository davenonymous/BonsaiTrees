package com.davenonymous.bonsaitrees.client.multiblock;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;

public class MultiBlockBakedQuad extends BakedQuad {

	public static MultiBlockBakedQuad of(BakedQuad quad, BlockState state, BlockPos pos) {
		return new MultiBlockBakedQuad(
			quad.getVertices(),
			quad.getTintIndex(),
			quad.getDirection(),
			quad.getSprite(),
			quad.isShade(),
			state,
			pos
		);
	}

	public static MultiBlockBakedQuad copy(MultiBlockBakedQuad quad) {
		int[] vertices = quad.getVertices();
		return new MultiBlockBakedQuad(
			Arrays.copyOf(vertices, vertices.length),
			quad.getTintIndex(),
			quad.getDirection(),
			quad.getSprite(),
			quad.isShade(),
			quad.state,
			quad.pos
		);
	}

	public BlockState state;
	public BlockPos pos;
	public MultiBlockAmbientOcclusionFace aoFace;

	private MultiBlockBakedQuad(int[] pVertices, int pTintIndex, Direction pDirection, TextureAtlasSprite pSprite, boolean pShade, BlockState state, BlockPos pos) {
		super(pVertices, pTintIndex, pDirection, pSprite, pShade, true);
		this.state = state;
		this.pos = pos;
	}


}