package com.davenonymous.bonsaitrees3.libnonymous.serialization;


import com.davenonymous.bonsaitrees3.libnonymous.helper.BlockStateSerializationHelper;
import com.google.gson.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class MultiBlockModelSerializer implements JsonDeserializer<MultiblockBlockModel> {
	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public MultiblockBlockModel deserialize(JsonElement root, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		if(!root.isJsonObject()) {
			return null;
		}

		JsonObject rootObj = root.getAsJsonObject();

		int version = rootObj.has("version") ? rootObj.get("version").getAsInt() : 1;
		switch(version) {
			case 1:
			case 2: {
				LOGGER.warn("Invalid version in shape file: '{}', skipping shape! Shape files below v3 are not supported!", rootObj.get("version"));
				return null;
			}
			case 3: {
				return deserializeV3(rootObj, typeOfT, context);
			}
		}

		LOGGER.warn("Invalid version in shape file: '{}', skipping shape! Maybe the shape file is from a newer mod version?", rootObj.get("version"));
		return null;
	}

	private Map<String, BlockState> getReferenceMapV3(JsonObject jsonRefMap) {
		Map<String, BlockState> refMap = new HashMap<>();
		for(Map.Entry<String, JsonElement> jsonRefEntry : jsonRefMap.entrySet()) {
			JsonObject jsonBlockInfo = jsonRefEntry.getValue().getAsJsonObject();
			BlockState state = BlockStateSerializationHelper.deserializeBlockState(jsonBlockInfo);
			refMap.put(jsonRefEntry.getKey(), state);
		}

		return refMap;
	}

	private boolean hasUnknownBlocksInMap(JsonObject jsonRefMap) {
		for(Map.Entry<String, JsonElement> jsonRefEntry : jsonRefMap.entrySet()) {
			JsonObject jsonBlockInfo = jsonRefEntry.getValue().getAsJsonObject();
			if(!BlockStateSerializationHelper.isValidBlockState(jsonBlockInfo)) {
				return true;
			}
		}

		return false;
	}

	public MultiblockBlockModel deserializeV3(JsonObject root, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		// First get the name of the tree type
		if(!root.has("type")) {
			LOGGER.warn("Missing type name in multiblockmodel json");
			return null;
		}

		ResourceLocation treeType = new ResourceLocation(root.get("type").getAsString());

		if(hasUnknownBlocksInMap(root.getAsJsonObject("ref"))) {
			//LOGGER.warn("Unknown blocks in multiblockmodel json");
			return null;
		}

		// Get the reference map
		Map<String, BlockState> refMap = getReferenceMapV3(root.getAsJsonObject("ref"));

		// And use it to build the actual block map
		Map<BlockPos, BlockState> blocks = new HashMap<>();
		JsonArray jsonBlocks = root.getAsJsonArray("shape");


		int x = jsonBlocks.size() - 1;
		for(JsonElement zSliceElement : jsonBlocks) {
			int y = zSliceElement.getAsJsonArray().size() - 1;
			for(JsonElement ySliceElement : zSliceElement.getAsJsonArray()) {
				for(int z = 0; z < ySliceElement.getAsString().length(); z++) {
					String ref = ySliceElement.getAsString().charAt(z) + "";
					if(ref.equals(" ")) {
						continue;
					}

					if(!refMap.containsKey(ref)) {
						LOGGER.warn("Shape-Entry is using an unknown block reference '%s'! Skipping shape!", ref);
						return null;
					}

					blocks.put(new BlockPos(x, y, z), refMap.get(ref));
				}

				y--;
			}

			x--;
		}

		MultiblockBlockModel result = new MultiblockBlockModel(treeType);
		result.setBlocks(blocks);
		return result;
	}
}
