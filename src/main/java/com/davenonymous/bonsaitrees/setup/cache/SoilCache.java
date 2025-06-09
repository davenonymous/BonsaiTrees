package com.davenonymous.bonsaitrees.setup.cache;

import com.davenonymous.bonsaitrees.BonsaiTrees;
import com.davenonymous.bonsaitrees.setup.ModDataMaps;
import com.davenonymous.bonsaitrees.setup.data.BonsaiInfo;
import com.davenonymous.bonsaitrees.setup.data.SoilInfo;
import com.davenonymous.bonsaitrees.setup.data.SoilInfoWithTexture;
import com.davenonymous.bonsaitrees.setup.data.SoilType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.registries.datamaps.DataMapsUpdatedEvent;

import java.util.*;
import java.util.stream.Collectors;

public class SoilCache {
	public static final Map<Block, Set<SoilInfo>> SOIL_BY_BLOCK = new HashMap<>();
	public static final Map<Fluid, Set<SoilInfo>> SOIL_BY_FLUID = new HashMap<>();
	public static final Map<Item, Set<SoilInfoWithTexture>> SOIL_BY_ITEM = new HashMap<>();
	public static final Map<ItemStack, Set<SoilInfo>> SOILS = new HashMap<>();

	public static final Map<ResourceLocation, Map<Item, Set<SoilInfo>>> SOIL_BY_TYPE = new HashMap<>();
	public static final Map<ResourceLocation, Set<Item>> BONSAIS_BY_SOIL = new HashMap<>();

	public static void dataMapsUpdated(DataMapsUpdatedEvent event) {
		// TODO: Can this be a reload listener instead?
		Registry<Block> blockRegistry = event.getRegistries().registryOrThrow(Registries.BLOCK);
		Registry<Fluid> fluidRegistry = event.getRegistries().registryOrThrow(Registries.FLUID);
		Registry<Item> itemRegistry = event.getRegistries().registryOrThrow(Registries.ITEM);

		// TODO: SOIL_BY_TYPE is buggy, blocks can have multiple soil types, but this map only keeps the last one registered.
		SOIL_BY_TYPE.clear();
		SOILS.clear();

		SOIL_BY_BLOCK.clear();
		Map<ResourceKey<Block>, SoilInfo> blockDataMap = blockRegistry.getDataMap(ModDataMaps.BLOCK_SOIL);
		for(Map.Entry<ResourceKey<Block>, SoilInfo> entry : blockDataMap.entrySet()) {
			ResourceKey<Block> key = entry.getKey();
			if(!blockRegistry.containsKey(key)) {
				BonsaiTrees.LOGGER.warn("Block {} is not registered in the block registry, skipping soil registration.", key.location());
				continue;
			}

			Block soilBlock = blockRegistry.get(key);
			Item soilItem = soilBlock.asItem();
			SoilInfo soilInfo = entry.getValue();

			SOIL_BY_BLOCK.computeIfAbsent(soilBlock, k -> new HashSet<>()).add(soilInfo);

			List<ResourceLocation> types = soilInfo.soilType();
			for(var type : types) {
				var byTypeMap = SOIL_BY_TYPE.computeIfAbsent(type, k -> new HashMap<>());
				byTypeMap.computeIfAbsent(soilItem, k -> new HashSet<>()).add(soilInfo);
			}

			SOILS.computeIfAbsent(new ItemStack(soilBlock), k -> new HashSet<>()).add(soilInfo);
		}


		SOIL_BY_FLUID.clear();
		Map<ResourceKey<Fluid>, SoilInfo> fluidDataMap = fluidRegistry.getDataMap(ModDataMaps.FLUID_SOIL);
		for(Map.Entry<ResourceKey<Fluid>, SoilInfo> entry : fluidDataMap.entrySet()) {
			ResourceKey<Fluid> key = entry.getKey();
			if(!fluidRegistry.containsKey(key)) {
				BonsaiTrees.LOGGER.warn("Fluid {} is not registered in the fluid registry, skipping soil registration.", key.location());
				continue;
			}

			SoilInfo soilInfo = entry.getValue();
			SOIL_BY_FLUID.computeIfAbsent(fluidRegistry.get(key), k -> new HashSet<>()).add(soilInfo);

			List<ResourceLocation> types = soilInfo.soilType();
			for(var type : types) {
				var byTypeMap = SOIL_BY_TYPE.computeIfAbsent(type, k -> new HashMap<>());
				byTypeMap.computeIfAbsent(fluidRegistry.get(key).getBucket(), k -> new HashSet<>()).add(soilInfo);
			}

			SOILS.computeIfAbsent(new ItemStack(fluidRegistry.get(key).getBucket()), k -> new HashSet<>()).add(soilInfo);
		}


		SOIL_BY_ITEM.clear();
		Map<ResourceKey<Item>, SoilInfoWithTexture> itemDataMap = itemRegistry.getDataMap(ModDataMaps.ITEM_SOIL);
		for(Map.Entry<ResourceKey<Item>, SoilInfoWithTexture> entry : itemDataMap.entrySet()) {
			ResourceKey<Item> key = entry.getKey();
			if(!itemRegistry.containsKey(key)) {
				BonsaiTrees.LOGGER.warn("Item {} is not registered in the item registry, skipping soil registration.", key.location());
				continue;
			}

			Item item = itemRegistry.get(key);

			SoilInfoWithTexture soilInfoWithTexture = entry.getValue();
			SOIL_BY_ITEM.computeIfAbsent(item, k -> new HashSet<>()).add(soilInfoWithTexture);

			SoilInfo soilInfo = SoilInfo.fromSoilInfoWithTexture(soilInfoWithTexture);

			List<ResourceLocation> types = soilInfo.soilType();
			for(var type : types) {
				var byTypeMap = SOIL_BY_TYPE.computeIfAbsent(type, k -> new HashMap<>());
				byTypeMap.computeIfAbsent(item, k -> new HashSet<>()).add(soilInfo);
			}

			SOILS.computeIfAbsent(new ItemStack(item), k -> new HashSet<>()).add(soilInfo);
		}

		BONSAIS_BY_SOIL.clear();
		for(var entry : BonsaiCache.BONSAI_BY_ITEM.entrySet()) {
			Item bonsaiItem = entry.getKey();
			BonsaiInfo bonsaiInfo = entry.getValue();
			List<SoilType> validSoilTypes = bonsaiInfo.validSoilTypes(event.getRegistries());
			for(SoilType soilType : validSoilTypes) {
				if(!BONSAIS_BY_SOIL.containsKey(soilType.id())) {
					BONSAIS_BY_SOIL.put(soilType.id(), new HashSet<>());
				}
				BONSAIS_BY_SOIL.get(soilType.id()).add(bonsaiItem);
			}
		}

		BonsaiTrees.LOGGER.debug("Soil-Type Summary:");
		for(var soilType : BONSAIS_BY_SOIL.keySet()) {
			var bonsais = BONSAIS_BY_SOIL.get(soilType);
			BonsaiTrees.LOGGER.debug("  {} with {} bonsais", soilType, bonsais.size());
		}
	}

	public static boolean isSoil(ItemStack soilStack) {
		return getSoilInfo(soilStack).isPresent();
	}

	public static Optional<Set<SoilInfo>> getSoilInfo(ItemStack soilStack) {
		if(!soilStack.isEmpty() && soilStack.getItem() instanceof BlockItem item) {
			return Optional.ofNullable(SoilCache.SOIL_BY_BLOCK.get(item.getBlock()));
		}

		if(!soilStack.isEmpty() && soilStack.getItem() instanceof BucketItem bucket) {
			return Optional.ofNullable(SoilCache.SOIL_BY_FLUID.get(bucket.content));
		}

		if(!soilStack.isEmpty() && SoilCache.SOIL_BY_ITEM.containsKey(soilStack.getItem())) {
			Set<SoilInfoWithTexture> texturedSoilInfos = SoilCache.SOIL_BY_ITEM.get(soilStack.getItem());
			if(texturedSoilInfos == null || texturedSoilInfos.isEmpty()) {
				return Optional.empty();
			}
			Set<SoilInfo> soilInfos = texturedSoilInfos.stream().map(SoilInfo::fromSoilInfoWithTexture).collect(Collectors.toSet());
			return Optional.of(soilInfos);
		}

		return Optional.empty();
	}
}
