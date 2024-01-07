package com.davenonymous.bonsaitrees3.registry.soil;

import com.davenonymous.bonsaitrees3.BonsaiTrees3;
import com.davenonymous.libnonymous.helper.BlockStateSerializationHelper;
import com.davenonymous.libnonymous.helper.FluidStateSerializationHelper;
import com.davenonymous.libnonymous.serialization.JsonHelpers;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

import javax.annotation.Nullable;


public class SoilSerializer implements RecipeSerializer<SoilInfo> {

	@Override
	public SoilInfo fromJson(ResourceLocation recipeId, JsonObject json) {
		final Ingredient soil = JsonHelpers.getIngredientFromArrayOrSingle(json.get("soil"));
		if(soil.isEmpty()) {
			BonsaiTrees3.LOGGER.info("Skipping recipe '{}', contains unknown soil ingredient.", recipeId);
			return null;
		}

		float tickModifier = 1.0f;
		if(json.has("tickModifier")) {
			tickModifier = json.get("tickModifier").getAsFloat();
		}

		boolean isFluid = json.getAsJsonObject("display").has("fluid");
		SoilInfo result;
		if(isFluid) {
			FluidState state = FluidStateSerializationHelper.deserializeFluidState(json.getAsJsonObject("display"));
			result = new SoilInfo(recipeId, soil, state, tickModifier);
		} else {
			BlockState state = BlockStateSerializationHelper.deserializeBlockState(json.getAsJsonObject("display"));
			result = new SoilInfo(recipeId, soil, state, tickModifier);
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
	public SoilInfo fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		final boolean isFluid = buffer.readBoolean();
		final Ingredient ingredient = Ingredient.fromNetwork(buffer);
		final float tickModifier = buffer.readFloat();

		SoilInfo result;
		if(isFluid) {
			final FluidState fluidState = FluidStateSerializationHelper.deserializeFluidState(buffer);
			result = new SoilInfo(recipeId, ingredient, fluidState, tickModifier);
		} else {
			final BlockState blockState = BlockStateSerializationHelper.deserializeBlockState(buffer);
			result = new SoilInfo(recipeId, ingredient, blockState, tickModifier);
		}

		final int tagCount = buffer.readInt();
		for(int i = 0; i < tagCount; i++) {
			result.addTag(buffer.readUtf());
		}

		return result;
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, SoilInfo soil) {
		buffer.writeBoolean(soil.isFluid);
		soil.ingredient.toNetwork(buffer);
		buffer.writeFloat(soil.tickModifier);
		if(soil.isFluid) {
			FluidStateSerializationHelper.serializeFluidState(buffer, soil.fluidState);
		} else {
			BlockStateSerializationHelper.serializeBlockState(buffer, soil.blockState);
		}

		buffer.writeInt(soil.tags.size());
		for(String tag : soil.tags) {
			buffer.writeUtf(tag);
		}

	}
}