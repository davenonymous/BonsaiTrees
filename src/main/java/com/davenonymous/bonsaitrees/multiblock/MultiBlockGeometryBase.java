package com.davenonymous.bonsaitrees.multiblock;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class MultiBlockGeometryBase {
	public static final MultiBlockGeometryBase EMPTY = new MultiBlockGeometryBase(4, Collections.emptyMap(), Collections.emptyList(), 1, 0);
	public static final MapCodec<MultiBlockGeometryBase> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.INT.fieldOf("version").forGetter(MultiBlockGeometryBase::version),
		Codec.unboundedMap(Codec.STRING, BlockState.CODEC).fieldOf("ref").forGetter(MultiBlockGeometryBase::ref),
		Codec.list(Codec.list(Codec.STRING)).fieldOf("shape").forGetter(MultiBlockGeometryBase::shape),
		Codec.INT.optionalFieldOf("scaleToBlocks", 1).forGetter(MultiBlockGeometryBase::scaleToBlocks),
		Codec.INT.optionalFieldOf("lightEmission", 0).forGetter(MultiBlockGeometryBase::lightEmission)
	).apply(instance, MultiBlockGeometryBase::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, MultiBlockGeometryBase> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.INT, MultiBlockGeometryBase::version,
		ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.fromCodecWithRegistries(BlockState.CODEC)), MultiBlockGeometryBase::ref,
		ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()).apply(ByteBufCodecs.list()), MultiBlockGeometryBase::shape,
		ByteBufCodecs.INT, MultiBlockGeometryBase::scaleToBlocks,
		ByteBufCodecs.INT, MultiBlockGeometryBase::lightEmission,
		MultiBlockGeometryBase::new
	);

	private final int version;
	private final Map<String, BlockState> ref;
	private final List<List<String>> shape;
	private final int scaleToBlocks;
	private final Map<BlockPos, Voxel> voxels;
	private final int lightEmission;
	public final BlockPos trunkPos;

	public MultiBlockGeometryBase(int version, Map<String, BlockState> ref, List<List<String>> shape, int scaleToBlocks, int lightEmission) {
		this.version = version;
		this.ref = ref;
		this.shape = shape;
		this.scaleToBlocks = scaleToBlocks;
		this.voxels = getBlocks();
		this.trunkPos = getTrunkPos();
		this.lightEmission = lightEmission;
	}

	public int getBlockCount() {
		return voxels.keySet().size();
	}

	public int getStateCount() {
		return ref.keySet().size();
	}

	private static Map<BlockPos, Voxel> castVoxelMap(Map<BlockPos, BlockState> blocks) {
		Map<BlockPos, Voxel> voxels = new HashMap<>();
		for(var entry : blocks.entrySet()) {
			BlockPos pos = entry.getKey();
			BlockState state = entry.getValue();
			voxels.put(pos, new Voxel(pos, state));
		}
		return voxels;
	}

	public MultiBlockGeometryBase(int scaleToBlocks, int version, Map<BlockPos, Voxel> blocks, int lightEmission) {
		this.scaleToBlocks = scaleToBlocks;
		this.version = version;
		this.voxels = blocks;

		int width = 0;
		int height = 0;
		int depth = 0;
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

		Map<BlockState, Character> refMap = new HashMap<>();
		char refChar = 'a';

		this.ref = new HashMap<>();
		var blocksAsArray = new char[width + 1][height + 1][depth + 1];
		for(int x = 0; x < width + 1; x++) {
			for(int y = 0; y < height + 1; y++) {
				for(int z = 0; z < depth + 1; z++) {
					blocksAsArray[x][y][z] = ' ';
				}
			}
		}

		for(var entry : blocks.entrySet().stream().sorted(Map.Entry.comparingByKey()).toList()) {
			BlockPos pos = entry.getKey();
			Voxel state = entry.getValue();

			if(!refMap.containsKey(state.state())) {
				refMap.put(state.state(), refChar++);
				ref.put(String.valueOf(refChar), state.state());
				if(refChar == 'z' + 1) {
					refChar = 'A';
				}
			}

			char stateChar = refMap.get(state.state());
			blocksAsArray[pos.getX()][pos.getY()][pos.getZ()] = stateChar;
		}

		this.shape = Arrays.stream(blocksAsArray).map(x -> Arrays.stream(x).map(String::valueOf).toList()).toList();
		this.trunkPos = getTrunkPos();
		this.lightEmission = lightEmission;
	}

	public static MultiBlockGeometryBase forDataGen(Map<BlockPos, BlockState> blocks, int lightEmission) {
		return new MultiBlockGeometryBase(1, 4, castVoxelMap(blocks), lightEmission);
	}

	private BlockPos getTrunkPos() {
		Vec3i size = getSize();
		BlockPos center = new BlockPos(size.getX() / 2, 0, size.getZ() / 2);

		List<BlockPos> roots = new ArrayList<>();
		if(voxels.containsKey(center)) {
			roots.add(center);
		}
		for(BlockPos testPos : BlockPos.spiralAround(center, 3, Direction.EAST, Direction.NORTH)) {
			if(voxels.containsKey(testPos)) {
				var state = voxels.get(testPos).state;
				if(state.isAir() || state.is(Blocks.WATER) || state.is(BlockTags.LEAVES)) {
					continue;
				}
				roots.add(new BlockPos(testPos.getX(), 0, testPos.getZ()));
			}
		}

		if(roots.isEmpty()) {
			return center;
		}

		if(roots.size() == 1) {
			return roots.getFirst();
		}

		if(roots.size() >= 4) {
			return center;
		}

		// Otherwise use the center of the roots
		int x = 0;
		int z = 0;
		for(BlockPos root : roots) {
			x += root.getX();
			z += root.getZ();
		}
		center = new BlockPos(x / roots.size(), 0, z / roots.size());
		return center;
	}

	private Map<BlockPos, Voxel> getBlocks() {
		Map<BlockPos, Voxel> blocks = new HashMap<>();
		for(int x = 0; x < shape.size(); x++) {
			List<String> slice = shape.get(x);
			for(int y = slice.size() - 1; y >= 0; y--) {
				String row = slice.get(y);
				for(int z = 0; z < row.length(); z++) {
					String key = row.substring(z, z + 1);
					if(key.equals(" ")) {
						continue;
					}
					BlockState state = ref.get(key);
					if(state != null) {
						BlockPos pos = new BlockPos(shape.size() - 1 - x, slice.size() - 1 - y, z);
						blocks.put(pos, new Voxel(pos, state));
					}
				}
			}
		}

		return blocks;
	}

	public Vec3i getSize() {
		int maxX = 0;
		int maxY = 0;
		int maxZ = 0;
		for(Voxel voxel : voxels.values()) {
			BlockPos pos = voxel.pos();
			maxX = Math.max(maxX, pos.getX());
			maxY = Math.max(maxY, pos.getY());
			maxZ = Math.max(maxZ, pos.getZ());
		}
		return new Vec3i(maxX, maxY, maxZ);
	}

	public int getMaxDimension() {
		var size = getSize();
		return Math.max(size.getX(), Math.max(size.getY(), size.getZ()));
	}

	public int getMinDimension() {
		var size = getSize();
		return Math.min(size.getX(), Math.min(size.getY(), size.getZ()));
	}

	public int version() {
		return version;
	}

	public Map<String, BlockState> ref() {
		return ref;
	}

	public List<List<String>> shape() {
		return shape;
	}

	public int scaleToBlocks() {
		return scaleToBlocks;
	}

	public int lightEmission() {
		return lightEmission;
	}

	public Map<BlockPos, Voxel> voxels() {
		return voxels;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}
		if(obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		var that = (MultiBlockGeometryBase) obj;
		return this.version == that.version &&
			Objects.equals(this.ref, that.ref) &&
			Objects.equals(this.shape, that.shape) &&
			this.scaleToBlocks == that.scaleToBlocks;
	}

	@Override
	public int hashCode() {
		return Objects.hash(version, ref, shape, scaleToBlocks);
	}

	@Override
	public String toString() {
		return "MultiBlockModelGeometry[" +
			"version=" + version + ", " +
			"ref=" + ref + ", " +
			"shape=" + shape + ", " +
			"scaleToBlocks=" + scaleToBlocks + ']';
	}

	public String serializePretty() {
		if(getSize().getX() + 1 == 0 || getSize().getY() + 1 == 0 || getSize().getZ() + 1 == 0) {
			return "";
		}

		char[][][] map = new char[getSize().getX() + 1][getSize().getY() + 1][getSize().getZ() + 1];
		StringBuilder refMapBuilder = new StringBuilder();
		refMapBuilder.append("  \"ref\": {\n");
		char nextRef = 'a';
		Map<String, Character> refMap = new HashMap<>();
		for(Map.Entry<BlockPos, Voxel> entry : this.voxels.entrySet()) {
			BlockPos pos = entry.getKey();
			BlockState state = entry.getValue().state();


			String jsonString = BlockState.CODEC.encodeStart(JsonOps.INSTANCE, state).result().get().toString();

			// Get new or already used reference char for this block
			char thisRef;
			if(refMap.containsKey(jsonString)) {
				thisRef = refMap.get(jsonString);
			} else {
				thisRef = nextRef++;
				if(nextRef == 'z' + 1) {
					nextRef = 'A';
				}
				refMap.put(jsonString, thisRef);

				refMapBuilder.append("    \"" + thisRef + "\": " + jsonString + ",\n");
			}

			map[pos.getX()][pos.getY()][pos.getZ()] = thisRef;
		}
		refMapBuilder.deleteCharAt(refMapBuilder.length() - 2);
		refMapBuilder.append("  },\n");

		StringBuilder output = new StringBuilder("{\n");

		output.append("  \"loader\": \"bonsaitrees4:multiblockmodel\",\n");
		output.append("  \"version\": 4,\n");
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


	public record Voxel(BlockPos pos, BlockState state) {
		public BakedModel model() {
			return Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(state);
		}
	}
}
