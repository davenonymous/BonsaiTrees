package com.davenonymous.bonsaitrees2.render;

import com.davenonymous.bonsaitrees2.util.Logz;
import com.davenonymous.libnonymous.render.MultiblockBlockModel;
import com.davenonymous.libnonymous.utils.BlockStateSerializationHelper;
import com.google.gson.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class TreeModelSerializer implements JsonDeserializer<MultiblockBlockModel> {

    @Override
    public MultiblockBlockModel deserialize(JsonElement root, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if(!root.isJsonObject()) {
            return null;
        }

        JsonObject rootObj = root.getAsJsonObject();

        int version = rootObj.has("version") ? rootObj.get("version").getAsInt() : 1;
        switch (version) {
            case 1: {
                Logz.warn("Invalid version in shape file: '%s', skipping shape! Shape files below v3 are not supported!", rootObj.get("version"));
                return null;
            }
            case 2: {
                Logz.warn("Invalid version in shape file: '%s', skipping shape! Shape files below v3 are not supported!", rootObj.get("version"));
                return null;
            }
            case 3: {
                return deserializeV3(rootObj, typeOfT, context);
            }
        }

        Logz.warn("Invalid version in shape file: '%s', skipping shape! Maybe the shape file is from a newer mod version?", rootObj.get("version"));
        return null;
    }

    private Map<String, BlockState> getReferenceMapV3(JsonObject jsonRefMap) {
        Map<String, BlockState> refMap = new HashMap<>();
        for(Map.Entry<String,JsonElement> jsonRefEntry : jsonRefMap.entrySet()) {
            JsonObject jsonBlockInfo = jsonRefEntry.getValue().getAsJsonObject();
            BlockState state = BlockStateSerializationHelper.deserializeBlockState(jsonBlockInfo);
            refMap.put(jsonRefEntry.getKey(), state);
        }

        return refMap;
    }

    public MultiblockBlockModel deserializeV3(JsonObject root, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        // First get the name of the tree type
        if(!root.has("type")) {
            Logz.warn("Missing type name in shape config");
            return null;
        }

        ResourceLocation treeType = new ResourceLocation(root.get("type").getAsString());

        // Get the reference map
        Map<String, BlockState> refMap = getReferenceMapV3(root.getAsJsonObject("ref"));

        // And use it to build the actual block map
        Map<BlockPos, BlockState> blocks = new HashMap<>();
        JsonArray jsonBlocks = root.getAsJsonArray("shape");


        int x = jsonBlocks.size()-1;
        for(JsonElement zSliceElement : jsonBlocks) {
            int y = zSliceElement.getAsJsonArray().size()-1;
            for(JsonElement ySliceElement : zSliceElement.getAsJsonArray()) {
                for(int z = 0; z < ySliceElement.getAsString().length(); z++) {
                    String ref = ySliceElement.getAsString().charAt(z) + "";
                    if(ref.equals(" ")) {
                        continue;
                    }

                    if(!refMap.containsKey(ref)) {
                        Logz.warn("Shape-Entry is using an unknown block reference '%s'! Skipping shape!", ref);
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
