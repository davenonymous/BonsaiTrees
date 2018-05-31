package org.dave.bonsaitrees.trees;

import com.google.gson.*;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.dave.bonsaitrees.utility.Logz;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class TreeShapeSerializer implements JsonDeserializer<TreeShape> {
    /**
     * Gson invokes this call-back method during deserialization when it encounters a field of the
     * specified type.
     * <p>In the implementation of this call-back method, you should consider invoking
     * {@link JsonDeserializationContext#deserialize(JsonElement, Type)} method to create objects
     * for any non-trivial field of the returned object. However, you should never invoke it on the
     * the same type passing {@code json} since that will cause an infinite loop (Gson will call your
     * call-back method again).
     *
     * @param root    The Json data being deserialized
     * @param typeOfT The type of the Object to deserialize to
     * @param context
     * @return a deserialized object of the specified type typeOfT which is a subclass of {@code T}
     * @throws JsonParseException if json is not in the expected format of {@code typeofT}
     */
    @Override
    public TreeShape deserialize(JsonElement root, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if(!root.isJsonObject()) {
            return null;
        }

        JsonObject rootObj = root.getAsJsonObject();

        int version = rootObj.has("version") ? rootObj.get("version").getAsInt() : 1;
        switch (version) {
            case 1: {
                return deserializeV1(rootObj, typeOfT, context);
            }
            case 2: {
                return deserializeV2(rootObj, typeOfT, context);
            }

            default: {
                Logz.warn("Invalid version in shape file: '%s', skipping shape! Maybe the shape file is from a newer mod version?", rootObj.get("version"));
                return null;
            }
        }
    }

    private Map<String, IBlockState> getReferenceMapV1(JsonObject jsonRefMap) {
        Map<String, IBlockState> refMap = new HashMap<>();
        for(Map.Entry<String,JsonElement> jsonRefEntry : jsonRefMap.entrySet()) {
            JsonObject jsonBlockInfo = jsonRefEntry.getValue().getAsJsonObject();
            String blockName = jsonBlockInfo.get("name").getAsString();
            Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockName));
            if(block == null) {
                Logz.warn("Invalid block '%s' used in shape", blockName);
                return null;
            }
            int blockMeta = jsonBlockInfo.has("meta") ? jsonBlockInfo.get("meta").getAsInt() : 0;
            IBlockState state = block.getStateFromMeta(blockMeta);

            refMap.put(jsonRefEntry.getKey(), state);
        }

        return refMap;
    }

    public TreeShape deserializeV2(JsonObject root, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        // First get the name of the tree type
        if(!root.has("type")) {
            Logz.warn("Missing type name in shape config");
            return null;
        }

        String treeType = root.get("type").getAsString();

        // Get the reference map
        Map<String, IBlockState> refMap = getReferenceMapV1(root.getAsJsonObject("ref"));

        // And use it to build the actual block map
        Map<BlockPos, IBlockState> blocks = new HashMap<>();
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

        TreeShape result = new TreeShape(treeType);
        result.setBlocks(blocks);
        return result;
    }

    public TreeShape deserializeV1(JsonObject root, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        // First get the name of the tree type
        if(!root.has("type")) {
            Logz.warn("Missing type name in shape config");
            return null;
        }

        String treeType = root.get("type").getAsString();

        // Get the reference map
        Map<String, IBlockState> refMap = getReferenceMapV1(root.getAsJsonObject("ref"));

        // And use it to build the actual block map
        Map<BlockPos, IBlockState> blocks = new HashMap<>();
        JsonArray jsonBlocks = root.getAsJsonArray("shape");
        for(JsonElement voxel : jsonBlocks) {
            /*
            {
                "pos": {"x": 1, "y": 2, "z": 1 },
                "ref": "a"
            },*/

            if(!voxel.isJsonObject()) {
                Logz.warn("Shape-Array contains an element that is no object! Skipping shape!");
                return null;
            }

            if(!voxel.getAsJsonObject().has("pos") || !voxel.getAsJsonObject().has("ref")) {
                Logz.warn("Shape-Entry is missing one of the required parameters 'pos' or 'ref'! Skipping shape!");
                return null;
            }

            String refString = voxel.getAsJsonObject().get("ref").getAsString();
            if(!refMap.containsKey(refString)) {
                Logz.warn("Shape-Entry is using an unknown block reference! Skipping shape!");
                return null;
            }

            JsonObject posJson = voxel.getAsJsonObject().get("pos").getAsJsonObject();
            int x = posJson.get("x").getAsInt();
            int y = posJson.get("y").getAsInt();
            int z = posJson.get("z").getAsInt();

            blocks.put(new BlockPos(x, y, z), refMap.get(refString));
        }

        TreeShape result = new TreeShape(treeType);
        result.setBlocks(blocks);
        return result;
    }

    public static String serializePretty(TreeShape shape) {
        if(shape.getWidth() == 0 || shape.getHeight() == 0 || shape.getDepth() == 0) {
            Logz.warn("Can not serialize tree shape for type: '%s', invalid dimensions: w=%d, h=%d, d=%d", shape.getTreeTypeName(), shape.getWidth(), shape.getHeight(), shape.getDepth());
            return null;
        }

        int width = shape.getWidth()+1;
        int height = shape.getHeight()+1;
        int depth = shape.getDepth()+1;

        char[][][] map = new char[width][height][depth];

        StringBuilder refMapBuilder = new StringBuilder();
        refMapBuilder.append("  \"ref\": {\n");
        char nextRef = 'a';
        Map<String, Character> refMap = new HashMap<>();
        for(Map.Entry<BlockPos, IBlockState> entry : shape.getBlocks().entrySet()) {
            BlockPos pos = entry.getKey();
            IBlockState state = entry.getValue();

            // Get new or already used reference char for this block
            String blockName = state.getBlock().getRegistryName().toString();
            int meta = state.getBlock().getMetaFromState(state);
            String fullName = blockName + ":" + meta;

            char thisRef;
            if(refMap.containsKey(fullName)) {
                thisRef = refMap.get(fullName);
            } else {
                thisRef = nextRef++;
                refMap.put(fullName, thisRef);

                refMapBuilder.append("    \""+thisRef+"\": {\n");
                refMapBuilder.append("      \"name\": \""+ blockName +"\",\n");
                refMapBuilder.append("      \"meta\": "+ meta +"\n");
                refMapBuilder.append("    },\n");
            }

            map[pos.getX()][pos.getY()][pos.getZ()] = thisRef;
        }
        refMapBuilder.deleteCharAt(refMapBuilder.length()-2);
        refMapBuilder.append("  },\n");

        StringBuilder output = new StringBuilder("{\n");

        output.append("  \"type\": \"" + shape.getTreeTypeName() + "\",\n");
        output.append("  \"version\": 2,\n");
        output.append(refMapBuilder);

        output.append("  \"shape\": [\n");

        for(int x = map.length-1; x >= 0; x--) {
            output.append("    [\n");
            for(int y = map[x].length-1; y >= 0; y--) {
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
            output.deleteCharAt(output.length()-2);
            output.append("    ],\n");
        }
        output.deleteCharAt(output.length()-2);

        output.append("  ]\n}\n");

        return output.toString();
    }
}
