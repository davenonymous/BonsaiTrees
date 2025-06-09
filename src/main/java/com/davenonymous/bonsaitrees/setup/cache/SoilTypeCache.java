package com.davenonymous.bonsaitrees.setup.cache;

import com.davenonymous.bonsaitrees.BonsaiTrees;
import com.davenonymous.bonsaitrees.setup.ModRegistries;
import com.davenonymous.bonsaitrees.setup.data.SoilType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.datamaps.DataMapsUpdatedEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SoilTypeCache {
	public static final Map<ResourceLocation, SoilType> SOIL_BY_RESOURCE = new HashMap<>();
	public static final Map<SoilType, ResourceLocation> RESOURCE_BY_SOIL = new HashMap<>();

	public static void dataMapsUpdated(DataMapsUpdatedEvent event) {
		if(!event.getRegistryKey().equals(Registries.ITEM)) {
			return;
		}

		Optional<Registry<SoilType>> soilTypeRegistry = event.getRegistries().registry(ModRegistries.SOILTYPE_REGISTRY_KEY);
		if(soilTypeRegistry.isEmpty()) {
			BonsaiTrees.LOGGER.error("SoilType registry is missing!");
			return;
		}

		SOIL_BY_RESOURCE.clear();
		RESOURCE_BY_SOIL.clear();
		for(var entry : soilTypeRegistry.get().entrySet()) {
			ResourceKey<SoilType> key = entry.getKey();
			SoilType type = entry.getValue();

			SOIL_BY_RESOURCE.put(key.location(), type);
			RESOURCE_BY_SOIL.put(type, key.location());

			BonsaiTrees.LOGGER.info("Registered Soil Type: {}", key.location());
		}

	}
}
