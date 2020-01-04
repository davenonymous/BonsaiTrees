package com.davenonymous.bonsaitrees2.registry.soil;

import com.davenonymous.bonsaitrees2.BonsaiTrees2;
import com.davenonymous.libnonymous.utils.BlockStateSerializationHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.block.BlockState;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;

public class SoilSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<SoilInfo> {
    public SoilSerializer() {
        this.setRegistryName(new ResourceLocation(BonsaiTrees2.MODID, "soil"));
    }

    @Override
    public SoilInfo read(ResourceLocation recipeId, JsonObject json) {
        final Ingredient soil = Ingredient.deserialize(json.getAsJsonObject("soil"));
        final BlockState renderState = BlockStateSerializationHelper.deserializeBlockState(json.getAsJsonObject("display"));

        float tickModifier = 1.0f;
        if(json.has("tickModifier")) {
            tickModifier = json.get("tickModifier").getAsFloat();
        }

        SoilInfo result = new SoilInfo(recipeId, soil, renderState, tickModifier);
        if(json.has("compatibleSoilTags")) {
            JsonArray tagsJson = json.getAsJsonArray("compatibleSoilTags");
            for(JsonElement element : tagsJson) {
                if(!element.isJsonPrimitive()) {
                    continue;
                }

                String tag = element.getAsString();
                if(tag == null) {
                    continue;
                }

                result.addTag(tag);
            }
        }

        return result;
    }

    @Nullable
    @Override
    public SoilInfo read(ResourceLocation recipeId, PacketBuffer buffer) {
        final Ingredient ingredient = Ingredient.read(buffer);
        final BlockState renderState = BlockStateSerializationHelper.deserializeBlockState(buffer);
        final float tickModifier = buffer.readFloat();

        SoilInfo result = new SoilInfo(recipeId, ingredient, renderState, tickModifier);

        final int tagCount = buffer.readInt();
        for(int i = 0; i < tagCount; i++) {
            result.addTag(buffer.readString());
        }

        return result;
    }

    @Override
    public void write(PacketBuffer buffer, SoilInfo soil) {
        soil.ingredient.write(buffer);
        BlockStateSerializationHelper.serializeBlockState(buffer, soil.renderState);
        buffer.writeFloat(soil.tickModifier);
        buffer.writeInt(soil.tags.size());
        for(String tag : soil.tags) {
            buffer.writeString(tag);
        }

    }


}
