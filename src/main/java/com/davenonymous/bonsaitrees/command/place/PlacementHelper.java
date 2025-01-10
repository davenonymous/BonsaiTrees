package com.davenonymous.bonsaitrees.command.place;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public class PlacementHelper<T extends BlockEntity> {
	public BlockState background = Blocks.BLACK_CONCRETE.defaultBlockState();
	public int extent = 32;
	public float aspectRatio = 16f / 9f;

	public boolean ascendingHeight = false;
	public int total;
	public BlockPos pos;
	public ServerLevel world;

	private int index = 0;
	private int columns;
	private int rows;

	public PlacementHelper(BlockPos pos, ServerLevel world, int total) {
		this.pos = pos;
		this.world = world;
		this.total = total;
		this.columns = (int) Math.ceil(Math.sqrt(total * aspectRatio));
		this.rows = (int) Math.ceil(total / (float) columns);

		if(columns <= 0) {
			columns = 1;
		}
		if(rows <= 0) {
			rows = 1;
		}
	}

	public void placeArena() {
		for(int column = -extent; column < columns + extent; column++) {
			for(int row = -extent; row < rows + extent; row++) {
				BlockPos displayPos = pos.offset(column, ascendingHeight ? row : 0, row);
				world.setBlock(displayPos, background, Block.UPDATE_ALL);
			}
		}

	}

	public void placeNextBlock(BlockState state, Consumer<T> processEntity) {
		int column = index % columns;
		int row = index / columns;

		BlockPos placePos = pos.offset(column, ascendingHeight ? row : 0, row);
		world.setBlock(placePos.above(), state, Block.UPDATE_ALL);
		BlockEntity blockEntity = world.getBlockEntity(placePos.above());
		//noinspection unchecked
		processEntity.accept((T) blockEntity);
		index++;

		if(ascendingHeight) {
			for(int height = row - 1; height >= 0; height--) {
				BlockPos displayPos = pos.offset(column, height, row);
				world.setBlock(displayPos, background, Block.UPDATE_ALL);
			}
		}
	}


}
