package com.davenonymous.bonsaitrees3.libnonymous.render;

import com.davenonymous.bonsaitrees3.libnonymous.serialization.MultiblockBlockModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

public class MultiBlockBlockAndTintGetter implements BlockAndTintGetter {
	MultiblockBlockModel model;
	BlockAndTintGetter realLevel;

	public MultiBlockBlockAndTintGetter(MultiblockBlockModel model, BlockAndTintGetter realLevel) {
		this.model = model;
		this.realLevel = realLevel;
	}

	@Override
	public float getShade(Direction pDirection, boolean pShade) {
		return realLevel.getShade(pDirection, pShade);
	}

	@Override
	public LevelLightEngine getLightEngine() {
		return realLevel.getLightEngine();
	}

	@Override
	public int getBlockTint(BlockPos pBlockPos, ColorResolver pColorResolver) {
		return realLevel.getBlockTint(pBlockPos, pColorResolver);
	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos pPos) {
		return null;
	}

	@Override
	public BlockState getBlockState(BlockPos pPos) {
		return model.blocks.getOrDefault(pPos, Blocks.AIR.defaultBlockState());
	}

	@Override
	public FluidState getFluidState(BlockPos pPos) {
		return Fluids.EMPTY.defaultFluidState();
	}

	@Override
	public int getHeight() {
		return model.height;
	}

	@Override
	public int getMinBuildHeight() {
		return 0;
	}
}