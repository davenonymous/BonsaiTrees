package com.davenonymous.bonsaitrees3.libnonymous.helper;

import com.davenonymous.bonsaitrees3.libnonymous.json.MCJsonUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class FluidStateSerializationHelper {
	private static final Logger LOGGER = LogManager.getLogger();

	public static FluidState deserializeFluidState(JsonObject json) {
		final Fluid fluid = MCJsonUtils.getFluid(json, "fluid");
		FluidState state = fluid.defaultFluidState();

		// If the properties member exists, attempt to assign properties to the block state.
		if(json.has("properties")) {

			final JsonElement propertiesElement = json.get("properties");

			if(propertiesElement.isJsonObject()) {

				final JsonObject props = propertiesElement.getAsJsonObject();

				// Iterate each member of the properties object. Expecting a simple key to
				// primitive string structure.
				for(final Map.Entry<String, JsonElement> property : props.entrySet()) {

					// Check the block for the property. Keys = property names.
					final Property blockProperty = fluid.defaultFluidState().getProperties().stream().filter(prop -> prop.getName().equals(property.getKey())).findFirst().get();
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
									LOGGER.error("Failed to update state for fluid {}. The mod that adds this fluid has issues.", fluid.getRegistryName());
								}
							} else {

								throw new JsonSyntaxException("The property " + property.getKey() + " with value " + valueString + " could not be parsed!");
							}
						} else {

							throw new JsonSyntaxException("Expected property value for " + property.getKey() + " to be primitive string. Got " + property.getValue());
						}
					} else {

						throw new JsonSyntaxException("The property " + property.getKey() + " is not valid for block " + fluid.getRegistryName());
					}
				}
			} else {

				throw new JsonSyntaxException("Expected properties to be an object. Got " + propertiesElement);
			}
		}

		return state;
	}

	public static void serializeFluidState(FriendlyByteBuf buffer, FluidState state) {
		buffer.writeResourceLocation(state.getType().getRegistryName());

		final Collection<Property<?>> properties = state.getProperties();
		buffer.writeInt(properties.size());

		for(final Property property : properties) {
			buffer.writeUtf(property.getName());
			buffer.writeUtf(state.getValue(property).toString());
		}
	}

	public static FluidState deserializeFluidState(FriendlyByteBuf buffer) {
		final ResourceLocation id = buffer.readResourceLocation();
		final Fluid fluid = ForgeRegistries.FLUIDS.getValue(id);

		if(fluid != null) {
			final int size = buffer.readInt();

			FluidState state = fluid.defaultFluidState();
			for(int i = 0; i < size; i++) {
				final String propName = buffer.readUtf();
				final String value = buffer.readUtf();

				// Check the block for the property. Keys = property names.
				final Property blockProperty = fluid.defaultFluidState().getProperties().stream().filter(property -> property.getName().equals(propName)).findFirst().get();
				if(blockProperty != null) {
					// Attempt to parse the value with the the property.
					final Optional<Comparable> propValue = blockProperty.getValue(value);

					if(propValue.isPresent()) {
						// Update the state with the new property.
						try {
							state = state.setValue(blockProperty, propValue.get());
						} catch (final Exception e) {
							LOGGER.error("Failed to read state for block {}. The mod that adds this block has issues.", fluid.getRegistryName());
						}
					}
				}
			}

			return state;
		}

		return Fluids.EMPTY.defaultFluidState();
	}
}