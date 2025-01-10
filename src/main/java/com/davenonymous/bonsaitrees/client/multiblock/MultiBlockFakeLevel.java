package com.davenonymous.bonsaitrees.client.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import javax.annotation.Nullable;

public class MultiBlockFakeLevel implements BlockAndTintGetter {
	BlockAndTintGetter realLevel;
	BlockPos lightAndTintPosition;
	MultiBlockModel model;

	public MultiBlockFakeLevel(MultiBlockModel model, BlockAndTintGetter realLevel, BlockPos lightAndTintPosition) {
		this.lightAndTintPosition = lightAndTintPosition;
		this.model = model;
		this.realLevel = realLevel;
	}

	public float getShade(Direction pDirection, boolean pShade) {
		return this.realLevel.getShade(pDirection, pShade);
	}

	public LevelLightEngine getLightEngine() {
		return this.realLevel.getLightEngine();
	}


	public int getBrightness(LightLayer pLightType, BlockPos pBlockPos) {
		return this.lightAndTintPosition.equals(BlockPos.ZERO) ? 255 : this.realLevel.getBrightness(pLightType, this.lightAndTintPosition);
	}

	public int getBlockTint(BlockPos pBlockPos, ColorResolver pColorResolver) {
		return this.realLevel.getBlockTint(this.lightAndTintPosition, pColorResolver);
	}

	public @Nullable BlockEntity getBlockEntity(BlockPos pPos) {
		return null;
	}

	public BlockState getBlockState(BlockPos pPos) {
		var voxel = this.model.blocks.get(pPos);
		if(voxel == null) {
			return Blocks.AIR.defaultBlockState();
		}
		return voxel.state();
	}

	public FluidState getFluidState(BlockPos pPos) {
		return Fluids.EMPTY.defaultFluidState();
	}

	public int getHeight() {
		return this.model.geometry.getSize().getY();
	}

	public int getMinBuildHeight() {
		return 0;
	}

	@Override
	public int getRawBrightness(BlockPos blockPos, int amount) {
		return realLevel.getRawBrightness(this.lightAndTintPosition, amount);
	}

	@Override
	public float getShade(float normalX, float normalY, float normalZ, boolean shade) {
		return realLevel.getShade(normalX, normalY, normalZ, shade);
	}

	@Override
	public boolean canSeeSky(BlockPos blockPos) {
		return realLevel.canSeeSky(this.lightAndTintPosition);
	}
}