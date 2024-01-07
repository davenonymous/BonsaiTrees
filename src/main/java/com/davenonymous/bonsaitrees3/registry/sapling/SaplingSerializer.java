package com.davenonymous.bonsaitrees3.registry.sapling;

import com.davenonymous.libnonymous.json.MCJsonUtils;
import com.davenonymous.libnonymous.serialization.JsonHelpers;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;
//import net.minecraftforge.registries.ForgeRegistryEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;


public class SaplingSerializer implements RecipeSerializer<SaplingInfo> {
	private static final Logger LOGGER = LogManager.getLogger();

	private boolean isValidIngredient(JsonObject obj) {
		if(obj == null) {
			return false;
		}
		Item item = MCJsonUtils.getItem(obj, "item");
		if(ForgeRegistries.ITEMS.getKey(item).toString().equals("minecraft:air")) {
			return false;
		}

		return true;
	}

	@Override
	public SaplingInfo fromJson(ResourceLocation recipeId, JsonObject json) {
		final Ingredient sapling = JsonHelpers.getIngredientFromArrayOrSingle(json.get("sapling"));
		if(sapling.isEmpty()) {
			LOGGER.info("Skipping recipe '{}', contains unknown sapling.", recipeId);
			return null;
		}

		int baseTicks = 200;
		if(json.has("ticks")) {
			baseTicks = json.get("ticks").getAsInt();
		}

		SaplingInfo result = new SaplingInfo(recipeId, sapling, baseTicks);
		if(json.has("drops")) {
			JsonArray dropsJson = json.getAsJsonArray("drops");
			for(JsonElement element : dropsJson) {
				if(!element.isJsonObject()) {
					continue;
				}

				JsonObject dropObj = element.getAsJsonObject();
				if(!isValidIngredient(dropObj.getAsJsonObject("result"))) {
					LOGGER.info("Skipping recipe '{}', contains unknown drop.", recipeId);
					return null;
				}

				SaplingDrop drop = new SaplingDrop(element.getAsJsonObject());
				// TODO: Add error handling

				result.addDrop(drop);
			}
		}

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
	public SaplingInfo fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		final Ingredient ingredient = Ingredient.fromNetwork(buffer);
		final int baseTicks = buffer.readInt();

		SaplingInfo result = new SaplingInfo(recipeId, ingredient, baseTicks);

		final int dropCount = buffer.readInt();
		for(int i = 0; i < dropCount; i++) {
			result.addDrop(new SaplingDrop(buffer));
		}

		final int tagCount = buffer.readInt();
		for(int i = 0; i < tagCount; i++) {
			result.addTag(buffer.readUtf());
		}

		return result;
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, SaplingInfo sapling) {
		sapling.ingredient.toNetwork(buffer);
		buffer.writeInt(sapling.baseTicks);
		buffer.writeInt(sapling.drops.size());
		for(SaplingDrop drop : sapling.drops) {
			drop.write(buffer);
		}
		buffer.writeInt(sapling.tags.size());
		for(String tag : sapling.tags) {
			buffer.writeUtf(tag);
		}
	}
}