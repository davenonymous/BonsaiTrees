package com.davenonymous.bonsaitrees3.libnonymous.serialization;

import com.davenonymous.bonsaitrees3.libnonymous.helper.BlockStateSerializationHelper;
import com.davenonymous.bonsaitrees3.libnonymous.serialization.packetbuffer.PacketBufferFieldHandlers;
import com.davenonymous.bonsaitrees3.libnonymous.utils.FloodFill;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class MultiblockBlockModel {
	public Map<BlockPos, BlockState> blocks;
	public Map<BlockState, List<BlockPos>> reverseBlocks;
	public Map<BlockState, Character> refMap;
	public char[][][] blocksAsArray;
	public char[][][] blocksAsArray90;
	public char[][][] blocksAsArray180;
	public char[][][] blocksAsArray270;
	public ResourceLocation id;
	private static final Logger LOGGER = LogManager.getLogger();

	public int width = 0;
	public int height = 0;
	public int depth = 0;

	static {
		PacketBufferFieldHandlers.addIOHandler(MultiblockBlockModel.class, MultiblockBlockModel::new, MultiblockBlockModel::writeToBuffer);
	}

	public MultiblockBlockModel(ResourceLocation id) {
		this.id = id;
	}

	public MultiblockBlockModel(FriendlyByteBuf buffer) {
		// TODO: Be smarter! Send a refmap first, then only references.
		this.id = buffer.readResourceLocation();
		int size = buffer.readInt();
		Map<BlockPos, BlockState> blocks = new HashMap<>();
		for(int i = 0; i < size; i++) {
			BlockPos pos = buffer.readBlockPos();
			BlockState state = BlockStateSerializationHelper.deserializeBlockState(buffer);
			blocks.put(pos, state);
		}
		this.setBlocks(blocks);
	}

	public void writeToBuffer(FriendlyByteBuf buffer) {
		buffer.writeResourceLocation(this.id);
		buffer.writeInt(this.blocks.size());
		for(Map.Entry<BlockPos, BlockState> entry : this.blocks.entrySet()) {
			buffer.writeBlockPos(entry.getKey());
			BlockStateSerializationHelper.serializeBlockState(buffer, entry.getValue());
		}
	}

	public void removeBlockState(BlockState state) {
		Map<BlockPos, BlockState> newBlocks = new HashMap<>();
		for(Map.Entry<BlockPos, BlockState> entry : this.blocks.entrySet()) {
			if(entry.getValue().equals(state)) {
				continue;
			}

			newBlocks.put(entry.getKey(), entry.getValue());
		}

		this.setBlocks(newBlocks);
	}

	public void setBlocksShifted(Map<BlockPos, BlockState> blocks) {
		int lowestX = Integer.MAX_VALUE;
		int lowestY = Integer.MAX_VALUE;
		int lowestZ = Integer.MAX_VALUE;

		for(BlockPos pos : blocks.keySet()) {
			var state = blocks.get(pos);
			if(state.getBlock() == Blocks.DIRT) {
				continue;
			}

			if(pos.getX() < lowestX) {
				lowestX = pos.getX();
			}
			if(pos.getY() < lowestY) {
				lowestY = pos.getY();
			}
			if(pos.getZ() < lowestZ) {
				lowestZ = pos.getZ();
			}
		}

		Map<BlockPos, BlockState> shifted = new HashMap<>();
		for(BlockPos pos : blocks.keySet()) {
			BlockPos newPos = pos.offset(-lowestX, -lowestY, -lowestZ);
			if(shifted.containsKey(newPos)) {
				continue;
			}
			var state = blocks.get(pos);
			if(state.getBlock() == Blocks.DIRT) {
				continue;
			}

			shifted.put(newPos, state);
		}

		setBlocks(shifted);
	}

	public void setBlocks(Map<BlockPos, BlockState> blocks) {
		this.blocks = blocks;
		width = 0;
		height = 0;
		depth = 0;
		for(BlockPos pos : blocks.keySet()) {
			if(pos.getX() > width) {
				width = pos.getX();
			}
			if(pos.getY() > height) {
				height = pos.getY();
			}
			if(pos.getZ() > depth) {
				depth = pos.getZ();
			}
		}

		char refChar = 'a';
		refMap = new HashMap<>();
		blocksAsArray = new char[width + 1][height + 1][depth + 1];

		for(BlockPos pos : blocks.keySet().stream().sorted().toList()) {
			BlockState state = blocks.get(pos);

			char stateChar = refMap.getOrDefault(state, refChar++);
			if(!refMap.containsKey(state)) {
				refMap.put(state, stateChar);
			}

			blocksAsArray[pos.getX()][pos.getY()][pos.getZ()] = stateChar;
		}

		blocksAsArray90 = rotateMapCW(blocksAsArray);
		blocksAsArray180 = rotateMapCW(blocksAsArray90);
		blocksAsArray270 = rotateMapCW(blocksAsArray180);
		createReverseMap();
	}

	private void createReverseMap() {
		this.reverseBlocks = new HashMap<>();
		for(Map.Entry<BlockPos, BlockState> entry : blocks.entrySet()) {
			if(!this.reverseBlocks.containsKey(entry.getValue())) {
				this.reverseBlocks.put(entry.getValue(), new ArrayList<>());
			}

			this.reverseBlocks.get(entry.getValue()).add(entry.getKey());
		}
	}

	private char[][][] rotateMapCW(char[][][] map) {
		char[][][] ret = new char[map.length][][];
		for(int y = 0; y < map.length; y++) {
			final int M = map[y].length;
			final int N = map[y][0].length;
			char[][] slice = new char[N][M];
			for(int r = 0; r < M; r++) {
				for(int c = 0; c < N; c++) {
					slice[c][M - 1 - r] = map[y][r][c];
				}
			}
			ret[y] = slice;
		}

		return ret;

	}

	public void setBlocksByFloodFill(LevelReader world, BlockPos pos) {
		FloodFill floodFill = new FloodFill(world, pos);
		Map<BlockPos, BlockState> connectedBlocks = floodFill.getConnectedBlocks();
		if(connectedBlocks.size() == 0) {
			return;
		}

		this.setBlocks(connectedBlocks);
	}

	public int getBlockCount() {
		return this.blocks.keySet().size();
	}

	public Set<BlockPos> getRelevantPositions() {
		return this.blocks.keySet();
	}

	public double getScaleRatio(boolean inclHeight) {
		int dim = Math.max(width, depth);
		if(inclHeight || height > 10) {
			dim = Math.max(height, dim);
		}

		dim += 1;
		if(height > 6 || dim <= 4) {
			dim = Math.max(6, dim);
		}

		return 1.0d / (double) dim;
	}

	public String serializePretty() {
		if(width == 0 || height == 0 || depth == 0) {
			LOGGER.warn("Can not serialize model for type: '%s', invalid dimensions: w=%d, h=%d, d=%d", id, width, height, depth);
			return "";
		}

		char[][][] map = new char[width + 1][height + 1][depth + 1];
		StringBuilder refMapBuilder = new StringBuilder();
		refMapBuilder.append("  \"ref\": {\n");
		char nextRef = 'a';
		Map<String, Character> refMap = new HashMap<>();
		for(Map.Entry<BlockPos, BlockState> entry : this.blocks.entrySet()) {
			BlockPos pos = entry.getKey();
			BlockState state = entry.getValue();

			JsonObject json = BlockStateSerializationHelper.serializeBlockState(state);
			String jsonString = json.toString();

			// Get new or already used reference char for this block
			char thisRef;
			if(refMap.containsKey(jsonString)) {
				thisRef = refMap.get(jsonString);
			} else {
				thisRef = nextRef++;
				refMap.put(jsonString, thisRef);

				refMapBuilder.append("    \"" + thisRef + "\": " + jsonString + ",\n");
			}

			map[pos.getX()][pos.getY()][pos.getZ()] = thisRef;
		}
		refMapBuilder.deleteCharAt(refMapBuilder.length() - 2);
		refMapBuilder.append("  },\n");

		StringBuilder output = new StringBuilder("{\n");

		output.append("  \"type\": \"" + this.id.toString() + "\",\n");
		output.append("  \"version\": 3,\n");
		output.append(refMapBuilder);

		output.append("  \"shape\": [\n");

		for(int x = map.length - 1; x >= 0; x--) {
			output.append("    [\n");
			for(int y = map[x].length - 1; y >= 0; y--) {
				StringBuilder builder = new StringBuilder();
				for(int z = 0; z < map[x][y].length; z++) {
					char chr = ' ';
					if(map[x][y][z] != '\u0000') {
						chr = map[x][y][z];
					}
					builder.append(chr);
				}

				output.append("      \"" + builder + "\",\n");
			}
			output.deleteCharAt(output.length() - 2);
			output.append("    ],\n");
		}
		output.deleteCharAt(output.length() - 2);

		output.append("  ]\n}\n");

		return output.toString();
	}

	public boolean equalsWithRotation(MultiblockBlockModel tempModel) {
		if(tempModel == this) {
			return true;
		}
		if(tempModel == null) {
			return false;
		}

		if(this.blocks.size() != tempModel.blocks.size()) {
			return false;
		}

		boolean no0 = false;
		boolean no90 = false;
		boolean no180 = false;
		boolean no270 = false;
		for(int x = 0; x < this.width; x++) {
			for(int y = 0; y < this.height; y++) {
				for(int z = 0; z < this.depth; z++) {
					// Does x,y,z match any of the tempModels rotations?
					char shouldBe = this.blocksAsArray[x][y][z];

					char at0 = tempModel.blocksAsArray[x][y][z];
					char at90 = tempModel.blocksAsArray90[x][y][z];
					char at180 = tempModel.blocksAsArray180[x][y][z];
					char at270 = tempModel.blocksAsArray270[x][y][z];

					if(at0 != shouldBe) {
						no0 = true;
					}
					if(at90 != shouldBe) {
						no90 = true;
					}
					if(at180 != shouldBe) {
						no180 = true;
					}
					if(at270 != shouldBe) {
						no270 = true;
					}

					if(no0 && no90 && no180 && no270) {
						break;
					}
				}
			}
		}

		return no0 || no90 || no180 || no270;
	}
}