package org.dave.bonsaitrees.soils;

import com.google.gson.*;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.dave.bonsaitrees.utility.Logz;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BonsaiSoilSerializer implements JsonDeserializer<List<BonsaiSoil>> {
    private BonsaiSoil deserializeSoil(JsonElement json) throws JsonParseException {
        if(!json.isJsonObject()) {
            throw new JsonParseException("Soil definition is no Json object!");
        }

        JsonObject rootObj = json.getAsJsonObject();

        if(!rootObj.has("name")) {
            throw new JsonParseException("Missing 'name' in soil definition");
        }
        String soilName = rootObj.get("name").getAsString();

        if(rootObj.has("mod")) {
            String requiredMod = rootObj.get("mod").getAsString();
            if(requiredMod.length() > 0 && !Loader.isModLoaded(requiredMod)) {
                throw new JsonParseException("Mod '"+requiredMod+"' for soil '"+soilName+"' is not loaded. Skipping integration!");
            }
        }

        if(!rootObj.has("soil") || !rootObj.get("soil").isJsonObject()) {
            throw new JsonParseException("Missing 'soil' object section in soil definition");
        }
        JsonObject soilData = rootObj.get("soil").getAsJsonObject();
        if(!soilData.has("name")) {
            throw new JsonParseException("Soil stack section is missing 'name' property.");
        }
        String soilStackName = soilData.get("name").getAsString();
        int soilMeta = soilData.has("data") ? soilData.get("data").getAsInt() : 0;

        boolean ignoreMeta = false;
        if(soilData.has("ignoreMeta") && soilData.get("ignoreMeta").isJsonPrimitive()) {
            ignoreMeta = soilData.get("ignoreMeta").getAsBoolean();
        }

        float growTimeMultiplier = 1.0f;
        float dropChanceMultiplier = 1.0f;
        if(rootObj.has("modifiers") && rootObj.get("modifiers").isJsonObject()) {
            JsonObject modifierData = rootObj.get("modifiers").getAsJsonObject();
            if(modifierData.has("growTime") && modifierData.get("growTime").isJsonPrimitive()) {
                growTimeMultiplier = modifierData.get("growTime").getAsFloat();
            }
            if(modifierData.has("dropChance") && modifierData.get("dropChance").isJsonPrimitive()) {
                dropChanceMultiplier = modifierData.get("dropChance").getAsFloat();
            }
        }



        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(soilStackName));
        ItemStack soilStack = new ItemStack(block, 1, soilMeta);

        BonsaiSoil result = new BonsaiSoil(soilName, soilStack);
        result.setIgnoreMeta(ignoreMeta);
        result.setModifierDropChance(dropChanceMultiplier);
        result.setModifierSpeed(growTimeMultiplier);

        if(rootObj.has("providedTags") && rootObj.get("providedTags").isJsonArray()) {
            for(JsonElement element : rootObj.get("providedTags").getAsJsonArray()) {
                if(!element.isJsonPrimitive()) {
                    continue;
                }

                String tag = element.getAsString();
                result.addProvidedTag(tag);
            }
        } else {
            result.addProvidedTag("dirt");
            result.addProvidedTag("grass");
        }

        return result;
    }

    @Override
    public List<BonsaiSoil> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        List<BonsaiSoil> resultList = new ArrayList<>();

        if(json.isJsonArray()) {
            for(JsonElement soil : json.getAsJsonArray()) {
                resultList.add(this.deserializeSoil(soil));
            }
        } else if(json.isJsonObject()) {
            resultList.add(this.deserializeSoil(json));
        }

        return resultList;
    }
}
