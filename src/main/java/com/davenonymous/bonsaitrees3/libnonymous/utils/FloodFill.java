package com.davenonymous.bonsaitrees3.libnonymous.utils;


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FloodFill {
	private List<Block> ignoredBlocks = Arrays.asList(new Block[]{Blocks.DIRT, Blocks.STONE, Blocks.COBBLESTONE, Blocks.GRASS, Blocks.DIRT_PATH, Blocks.GRAVEL, Blocks.SAND, Blocks.SANDSTONE, Blocks.WATER, Blocks.BEDROCK, Blocks.END_STONE});
	private int MAX_SEARCH_DEPTH = 2048;
	private int MAX_BLOCKS = 4196;

	private LevelReader world;
	private BlockPos startingPosition;
	private Map<BlockPos, BlockState> result;
	private boolean normalized = true;

	public FloodFill(LevelReader world, BlockPos startingPosition) {
		this.world = world;
		this.startingPosition = startingPosition;
	}

	private static Map<BlockPos, BlockState> normalizeBlockPosMap(Map<BlockPos, BlockState> input) {
		int minY = Integer.MAX_VALUE;
		int minZ = Integer.MAX_VALUE;
		int minX = Integer.MAX_VALUE;
		for(BlockPos pos : input.keySet()) {
			if(pos.getY() < minY) {
				minY = pos.getY();
			}
			if(pos.getZ() < minZ) {
				minZ = pos.getZ();
			}
			if(pos.getX() < minX) {
				minX = pos.getX();
			}
		}

		Map<BlockPos, BlockState> result = new HashMap<>();
		for(Map.Entry<BlockPos, BlockState> blockInfo : input.entrySet()) {
			result.put(blockInfo.getKey().offset(-minX, -minY, -minZ), blockInfo.getValue());
		}

		return result;
	}

	public Map<BlockPos, BlockState> getConnectedBlocks() {
		result = new HashMap<>();
		floodFill(world, startingPosition, 0);
		if(normalized) {
			return normalizeBlockPosMap(result);
		}

		return result;
	}

	private void floodFill(LevelReader world, BlockPos pos, int depth) {
		if(depth > MAX_SEARCH_DEPTH) {
			return;
		}

		if(result.size() > MAX_BLOCKS) {
			return;
		}

		if(result.containsKey(pos)) {
			return;
		}

		BlockState state = world.getBlockState(pos);
		if(state.isAir()) {
			return;
		}

		if(ignoredBlocks.contains(state.getBlock())) {
			return;
		}

		result.put(pos, state);

		for(int x = -1; x < 2; x++) {
			for(int y = -1; y < 2; y++) {
				for(int z = -1; z < 2; z++) {
					if(x == 0 && y == 0 && z == 0) {
						continue;
					}

					floodFill(world, pos.offset(x, y, z), depth + 1);
				}
			}
		}
	}
}