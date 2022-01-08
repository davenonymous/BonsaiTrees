package com.davenonymous.bonsaitrees3.libnonymous.helper;

import com.davenonymous.bonsaitrees3.libnonymous.json.MCJsonUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class BlockStateSerializationHelper {
	private static final Logger LOGGER = LogManager.getLogger();

	public static CompoundTag serializeBlockStateToNBT(BlockState state) {
		CompoundTag result = new CompoundTag();
		final Block block = state.getBlock();
		result.putString("block", block.getRegistryName().toString());
		if(state.getProperties().size() > 0) {
			CompoundTag propertiesTag = new CompoundTag();

			for(final Property property : state.getProperties()) {
				propertiesTag.putString(property.getName(), state.getValue(property).toString());
			}

			result.put("properties", propertiesTag);
		}

		return result;
	}

	public static BlockState deserializeBlockState(CompoundTag nbt) {
		if(!nbt.contains("block")) {
			LOGGER.warn("NBT compound {} is not a blockstate", nbt);
			return null;
		}

		ResourceLocation blockId = ResourceLocation.tryParse(nbt.getString("block"));
		final Block block = ForgeRegistries.BLOCKS.getValue(blockId);
		if(block == null) {
			LOGGER.warn("Unknown block {} in NBT package", nbt.getString("block"));
			return null;
		}

		BlockState state = block.defaultBlockState();
		if(nbt.contains("properties")) {
			CompoundTag propertiesTag = nbt.getCompound("properties");
			for(String propertyName : propertiesTag.getAllKeys()) {
				final Property blockProperty = block.defaultBlockState().getProperties().stream().filter(property -> property.getName().equals(propertyName)).findFirst().get();
				if(blockProperty == null) {
					LOGGER.warn("The property '{}' is not valid for block {}", propertyName, blockId);
					continue;
				}

				String valueString = propertiesTag.getString(propertyName);
				final Optional<Comparable> propValue = blockProperty.getValue(valueString);
				if(!propValue.isPresent()) {
					LOGGER.warn("The property '{}' with value '{}' could not be parsed!", propertyName, valueString);
					continue;
				}

				try {
					state = state.setValue(blockProperty, propValue.get());
				} catch (final Exception e) {
					LOGGER.warn("Failed to update state for block {}. The mod that adds this block has issues.", block.getRegistryName());
					continue;
				}
			}
		}

		return state;
	}

	public static void serializeBlockState(FriendlyByteBuf buffer, BlockState state) {
		buffer.writeResourceLocation(state.getBlock().getRegistryName());

		final Collection<Property<?>> properties = state.getProperties();
		buffer.writeInt(properties.size());

		for(final Property property : properties) {
			buffer.writeUtf(property.getName());
			buffer.writeUtf(state.getValue(property).toString());
		}
	}

	public static BlockState deserializeBlockState(FriendlyByteBuf buffer) {
		final ResourceLocation id = buffer.readResourceLocation();
		final Block block = ForgeRegistries.BLOCKS.getValue(id);

		if(block != null) {
			final int size = buffer.readInt();

			BlockState state = block.defaultBlockState();
			for(int i = 0; i < size; i++) {
				final String propName = buffer.readUtf();
				final String value = buffer.readUtf();

				// Check the block for the property. Keys = property names.
				final Property blockProperty = block.defaultBlockState().getProperties().stream().filter(property -> property.getName().equals(propName)).findFirst().get();
				if(blockProperty != null) {
					// Attempt to parse the value with the the property.
					final Optional<Comparable> propValue = blockProperty.getValue(value);

					if(propValue.isPresent()) {
						// Update the state with the new property.
						try {
							state = state.setValue(blockProperty, propValue.get());
						} catch (final Exception e) {
							LOGGER.error("Failed to read state for block {}. The mod that adds this block has issues.", block.getRegistryName());
						}
					}
				}
			}

			return state;
		}

		return Blocks.AIR.defaultBlockState();
	}

	public static JsonObject serializeBlockState(BlockState state) {
		JsonObject result = new JsonObject();

		final Block block = state.getBlock();
		result.addProperty("block", block.getRegistryName().toString());
		if(state.getProperties().size() > 0) {
			JsonObject propertiesObj = new JsonObject();

			for(final Property property : state.getProperties()) {
				propertiesObj.addProperty(property.getName(), state.getValue(property).toString());
			}

			result.add("properties", propertiesObj);
		}

		return result;
	}

	public static boolean isValidBlockState(JsonObject json) {
		String blockName = json.get("block").getAsString();
		if(blockName.equals("minecraft:air")) {
			return true;
		}

		final Block block = MCJsonUtils.getBlock(json, "block");
		if(block == null || block.getRegistryName().toString().equals("minecraft:air")) {
			return false;
		}

		return true;
	}


	public static BlockState deserializeBlockState(JsonObject json) {

		// Read the block from the forge registry.
		final Block block = MCJsonUtils.getBlock(json, "block");

		// Start off with the default state.
		BlockState state = block.defaultBlockState();

		// If the properties member exists, attempt to assign properties to the block state.
		if(json.has("properties")) {

			final JsonElement propertiesElement = json.get("properties");

			if(propertiesElement.isJsonObject()) {

				final JsonObject props = propertiesElement.getAsJsonObject();

				// Iterate each member of the properties object. Expecting a simple key to
				// primitive string structure.
				for(final Map.Entry<String, JsonElement> property : props.entrySet()) {

					// Check the block for the property. Keys = property names.
					final Property blockProperty = block.defaultBlockState().getProperties().stream().filter(prop -> prop.getName().equals(property.getKey())).findFirst().get();
					if(blockProperty != null) {

						if(property.getValue().isJsonPrimitive()) {

							// Attempt to parse the value with the the property.
							final String valueString = property.getValue().getAsString();
							final Optional<Comparable> propValue = blockProperty.getValue(valueString);

							if(propValue.isPresent()) {

								// Update the state with the new property.
								try {

									state = state.setValue(blockProperty, propValue.get());
								} catch (final Exception e) {
									LOGGER.error("Failed to update state for block {}. The mod that adds this block has issues.", block.getRegistryName());
								}
							} else {

								throw new JsonSyntaxException("The property " + property.getKey() + " with value " + valueString + " could not be parsed!");
							}
						} else {

							throw new JsonSyntaxException("Expected property value for " + property.getKey() + " to be primitive string. Got " + property.getValue());
						}
					} else {

						throw new JsonSyntaxException("The property " + property.getKey() + " is not valid for block " + block.getRegistryName());
					}
				}
			} else {

				throw new JsonSyntaxException("Expected properties to be an object. Got " + propertiesElement);
			}
		}

		return state;
	}


}