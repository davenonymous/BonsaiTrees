package com.davenonymous.bonsaitrees.setup.data;

import com.davenonymous.bonsaitrees.BonsaiTrees;
import com.davenonymous.bonsaitrees.setup.ModRegistries;
import com.davenonymous.bonsaitrees.setup.cache.SoilCache;
import com.davenonymous.bonsaitrees.setup.config.GameplayConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.*;

public record BonsaiInfo(ResourceLocation model, Optional<List<ResourceLocation>> validSoils, Optional<Integer> requiredTicks, Optional<Integer> lightEmission) {
	public static final Codec<BonsaiInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		ResourceLocation.CODEC.fieldOf("model").forGetter(BonsaiInfo::model),
		ResourceLocation.CODEC.listOf().optionalFieldOf("valid_soil_types").forGetter(BonsaiInfo::validSoils),
		Codec.INT.optionalFieldOf("base_ticks").forGetter(BonsaiInfo::requiredTicks),
		Codec.INT.optionalFieldOf("light_emission").forGetter(BonsaiInfo::lightEmission)
	).apply(instance, BonsaiInfo::new));

	public static BonsaiInfo plain(ResourceLocation model) {
		return new BonsaiInfo(model, Optional.empty(), Optional.empty(), Optional.empty());
	}

	public static BonsaiInfo of(ResourceLocation... validSoils) {
		return new BonsaiInfo(null, Optional.of(Arrays.stream(validSoils).toList()), Optional.empty(), Optional.empty());
	}


	public int baseTicks() {
		return requiredTicks.orElse(GameplayConfig.baseGrowTicks);
	}

	public boolean canGrowOnSoil(ResourceLocation soilTypeId) {
		if(validSoils.isEmpty() && soilTypeId.equals(BonsaiTrees.resource("dirt"))) {
			return true;
		}

		if(validSoils.isEmpty()) {
			return false;
		}

		if(validSoils.get().isEmpty()) {
			return false;
		}

		return validSoils.get().contains(soilTypeId);
	}

	public List<SoilType> validSoilTypes(RegistryAccess registryAccess) {
		Registry<SoilType> soilRegistry = registryAccess.registryOrThrow(ModRegistries.SOILTYPE_REGISTRY_KEY);
		if(validSoils.isEmpty()) {
			SoilType soil = soilRegistry.getOrThrow(DefaultSoilTypes.DIRT);
			return List.of(soil);
		}

		return validSoils.get().stream().map(soilRegistry::get).toList();
	}

	public List<Item> validSoilItems(RegistryAccess registryAccess) {
		Set<Item> result = new HashSet<>();
		for(SoilType soil : validSoilTypes(registryAccess)) {
			Map<Item, SoilInfo> soilInfoMap = SoilCache.SOIL_BY_TYPE.get(soil.id());
			if(soilInfoMap == null || soilInfoMap.isEmpty()) {
				continue;
			}
			result.addAll(soilInfoMap.keySet());
		}
		return new ArrayList<>(result);
	}

	public ResourceKey<LootTable> lootTable() {
		ResourceLocation modelId = model();
		return ResourceKey.create(Registries.LOOT_TABLE, BonsaiTrees.resource("bonsai/" + modelId.getNamespace() + "/" + modelId.getPath()));
	}

	@Override
	public String toString() {
		return String.format("BonsaiInfo{model=%s, baseTicks=%s, soilTypes=%s}", model, requiredTicks, validSoils);
	}

	public BonsaiInfo withModel(ResourceLocation model) {
		return new BonsaiInfo(model, validSoils, requiredTicks, lightEmission);
	}

	public BonsaiInfo withValidSoils(List<ResourceLocation> validSoils) {
		if(validSoils.isEmpty()) {
			return new BonsaiInfo(model, Optional.empty(), requiredTicks, lightEmission);
		}
		return new BonsaiInfo(model, Optional.of(validSoils), requiredTicks, lightEmission);
	}

	public BonsaiInfo withRequiredTicks(int requiredTicks) {
		return new BonsaiInfo(model, validSoils, Optional.of(requiredTicks), lightEmission);
	}

	public BonsaiInfo withLightEmission(int lightEmission) {
		return new BonsaiInfo(model, validSoils, requiredTicks, lightEmission == 0 ? Optional.empty() : Optional.of(lightEmission));
	}
}
