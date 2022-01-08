package com.davenonymous.bonsaitrees3.libnonymous.render;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class TintedBakedQuad extends BakedQuad {

	public static TintedBakedQuad of(BakedQuad quad, BlockState state, BlockPos pos) {
		return new TintedBakedQuad(quad.getVertices(), quad.getTintIndex(), quad.getDirection(), quad.getSprite(), quad.isShade(), state, pos);
	}

	public BlockState state;
	public BlockPos pos;

	private TintedBakedQuad(int[] pVertices, int pTintIndex, Direction pDirection, TextureAtlasSprite pSprite, boolean pShade, BlockState state, BlockPos pos) {
		super(pVertices, pTintIndex, pDirection, pSprite, pShade);
		this.state = state;
		this.pos = pos;
	}
}