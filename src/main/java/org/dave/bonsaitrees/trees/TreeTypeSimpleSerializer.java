package org.dave.bonsaitrees.trees;

import com.google.gson.*;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.common.Loader;
import org.dave.bonsaitrees.api.TreeTypeSimple;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

import static org.dave.bonsaitrees.api.BonsaiDropChances.*;

public class TreeTypeSimpleSerializer implements JsonDeserializer<TreeTypeSimple> {
    @Override
    public TreeTypeSimple deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if(!json.isJsonObject()) {
            throw new JsonParseException("Tree type definition is no Json object!");
        }

        JsonObject rootObj = json.getAsJsonObject();

        if(!rootObj.has("name")) {
            throw new JsonParseException("Missing 'name' in tree type definition");
        }
        String typeName = rootObj.get("name").getAsString();

        if(rootObj.has("mod")) {
            String requiredMod = rootObj.get("mod").getAsString();
            if(requiredMod.length() > 0 && !Loader.isModLoaded(requiredMod)) {
                throw new JsonParseException("Mod '"+requiredMod+"' for type '"+typeName+"' is not loaded. Skipping integration!");
            }
        }

        if(!rootObj.has("sapling") || !rootObj.get("sapling").isJsonObject()) {
            throw new JsonParseException("Missing 'sapling' object section in tree type definition");
        }
        JsonObject saplingData = rootObj.get("sapling").getAsJsonObject();
        if(!saplingData.has("name")) {
            throw new JsonParseException("Sapling section is missing 'name' property.");
        }
        String saplingName = saplingData.get("name").getAsString();
        int saplingMeta = saplingData.has("data") ? saplingData.get("data").getAsInt() : 0;

        float growTimeMultiplier = rootObj.has("growTimeMultiplier") ? rootObj.get("growTimeMultiplier").getAsFloat() : 1.0f;

        TreeTypeSimple result = new TreeTypeSimple(typeName, saplingName, saplingMeta);
        result.setGrowTime((int)(result.getGrowTime() * growTimeMultiplier));

        if(rootObj.has("drops") && rootObj.get("drops").isJsonArray()) {
            for(JsonElement element : rootObj.get("drops").getAsJsonArray()) {
                if(!element.isJsonObject()) {
                    throw new JsonParseException("Entry in 'drops' array is no Json object!");
                }

                JsonObject dropData = element.getAsJsonObject();
                if(!dropData.has("type")) {
                    throw new JsonParseException("Missing 'type' property in drop entry");
                }
                String type = dropData.get("type").getAsString();
                String dropName = dropData.get("name").getAsString();
                int dropMeta = dropData.has("data") ? dropData.get("data").getAsInt() : 0;

                if(type.equalsIgnoreCase("WOOD")) {
                    result.addDrop(dropName, logAmount, dropMeta, logChance);
                } else if(type.equalsIgnoreCase("SAPLING")) {
                    result.addDrop(dropName, saplingAmount, dropMeta, saplingChance);
                } else if(type.equalsIgnoreCase("LEAVES")) {
                    result.addDrop(dropName, leafAmount, dropMeta, leafChance);
                } else if(type.equalsIgnoreCase("FRUIT")) {
                    result.addDrop(dropName, fruitAmount, dropMeta, fruitChance);
                } else if(type.equalsIgnoreCase("STICK")) {
                    result.addDrop(dropName, stickAmount, dropMeta, stickChance);
                } else if(type.equalsIgnoreCase("CUSTOM")) {
                    if(!dropData.has("count")) {
                        throw new JsonParseException("Drops with type 'CUSTOM' require a 'count' property!");
                    }
                    if(!dropData.has("chance")) {
                        throw new JsonParseException("Drops with type 'CUSTOM' require a 'chance' property!");
                    }

                    int dropAmount = Math.min(Math.max(dropData.get("count").getAsInt(), 0), 64);
                    float dropChance = Math.min(Math.max(dropData.get("chance").getAsFloat(), 0.0f), 1.0f);

                    result.addDrop(dropName, dropAmount, dropMeta, dropChance);
                }
            }
        }

        if(rootObj.has("worldgen")) {
            String worldGenClassName = rootObj.get("worldgen").getAsString();
            try {
                Class worldGenClass = Class.forName(worldGenClassName);
                Class[] types = {Boolean.TYPE};
                Constructor constructor = worldGenClass.getConstructor(types);

                Object[] parameters = {true};
                WorldGenerator worldGen = (WorldGenerator) constructor.newInstance(parameters);
                result.setWorldGen(worldGen);
            } catch (ClassNotFoundException e) {
                throw new JsonParseException("WorldGenClass '" + worldGenClassName + "' does not exist!");
            } catch (NoSuchMethodException e) {
                throw new JsonParseException("WorldGenClass '" + worldGenClassName + "' has no constructor with one boolean parameter!");
            } catch (IllegalAccessException e) {
                throw new JsonParseException("Could not instantiate WorldGenClass '" + worldGenClassName + "': IllegalAccessException: " + e);
            } catch (InstantiationException e) {
                throw new JsonParseException("Could not instantiate WorldGenClass '" + worldGenClassName + "': InstantiationException: " + e);
            } catch (InvocationTargetException e) {
                throw new JsonParseException("Could not instantiate WorldGenClass '" + worldGenClassName + "': InvocationTargetException: " + e);
            }
        }

        return result;
    }
}
