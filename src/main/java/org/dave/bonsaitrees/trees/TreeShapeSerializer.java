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

public class TreeShapeSerializer implements JsonSerializer<TreeShape>, JsonDeserializer<TreeShape> {
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

        // First get the name of the tree type
        if(!root.getAsJsonObject().has("type")) {
            Logz.warn("Missing type name in shape config");
            return null;
        }

        String treeType = root.getAsJsonObject().get("type").getAsString();

        // Get the reference map
        Map<String, IBlockState> refMap = new HashMap<>();
        JsonObject jsonRefMap = root.getAsJsonObject().getAsJsonObject("ref");
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

        // And use it to build the actual block map
        Map<BlockPos, IBlockState> blocks = new HashMap<>();
        JsonArray jsonBlocks = root.getAsJsonObject().getAsJsonArray("shape");
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

    @Override
    public JsonElement serialize(TreeShape src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject root = new JsonObject();

        root.addProperty("type", src.getTreeType().typeName);

        Map<String, String> refMap = new HashMap<>();
        JsonObject references = new JsonObject();

        char nextRef = 'a';
        JsonArray shapeJson = new JsonArray();
        for(Map.Entry<BlockPos, IBlockState> blockInfo : src.getBlocks().entrySet()) {
            BlockPos pos = blockInfo.getKey();
            IBlockState state = blockInfo.getValue();

            JsonObject posJson = new JsonObject();
            posJson.addProperty("x", pos.getX());
            posJson.addProperty("y", pos.getY());
            posJson.addProperty("z", pos.getZ());

            JsonObject blockJson = new JsonObject();
            blockJson.add("pos", posJson);
            String blockName = state.getBlock().getRegistryName().toString();
            int meta = state.getBlock().getMetaFromState(state);
            String fullName = blockName + ":" + meta;

            String refName;
            if(refMap.containsKey(fullName)) {
                refName = refMap.get(fullName);
            } else {
                refName = "" + nextRef++;
                refMap.put(fullName, refName);

                JsonObject blockRefJson = new JsonObject();
                blockRefJson.addProperty("name", blockName);
                blockRefJson.addProperty("meta", meta);

                references.add(refName, blockRefJson);
            }

            blockJson.addProperty("ref", refName);

            shapeJson.add(blockJson);
        }
        root.add("ref", references);
        root.add("shape", shapeJson);

        return root;
    }
}
