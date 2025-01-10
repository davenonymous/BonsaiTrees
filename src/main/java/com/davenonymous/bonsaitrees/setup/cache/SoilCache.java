package com.davenonymous.bonsaitrees.setup.cache;

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

public class SoilCache {
	public static final Map<Block, SoilInfo> SOIL_BY_BLOCK = new HashMap<>();
	public static final Map<Fluid, SoilInfo> SOIL_BY_FLUID = new HashMap<>();
	public static final Map<Item, SoilInfoWithTexture> SOIL_BY_ITEM = new HashMap<>();
	public static final Map<ItemStack, SoilInfo> SOILS = new HashMap<>();

	public static final Map<ResourceLocation, Map<Item, SoilInfo>> SOIL_BY_TYPE = new HashMap<>();
	public static final Map<SoilType, Set<Item>> BONSAIS_BY_SOIL = new HashMap<>();

	public static void dataMapsUpdated(DataMapsUpdatedEvent event) {
		Registry<Block> blockRegistry = event.getRegistries().registryOrThrow(Registries.BLOCK);
		Registry<Fluid> fluidRegistry = event.getRegistries().registryOrThrow(Registries.FLUID);
		Registry<Item> itemRegistry = event.getRegistries().registryOrThrow(Registries.ITEM);


		SOIL_BY_TYPE.clear();
		SOILS.clear();

		SOIL_BY_BLOCK.clear();
		Map<ResourceKey<Block>, SoilInfo> blockDataMap = blockRegistry.getDataMap(ModDataMaps.BLOCK_SOIL);
		for(Map.Entry<ResourceKey<Block>, SoilInfo> entry : blockDataMap.entrySet()) {
			SOIL_BY_BLOCK.put(blockRegistry.get(entry.getKey()), entry.getValue());

			ResourceLocation type = entry.getValue().soilType();
			if(!SOIL_BY_TYPE.containsKey(type)) {
				SOIL_BY_TYPE.put(type, new HashMap<>());
			}

			SOIL_BY_TYPE.get(type).put(blockRegistry.get(entry.getKey()).asItem(), entry.getValue());
			SOILS.put(new ItemStack(blockRegistry.get(entry.getKey())), entry.getValue());
		}


		SOIL_BY_FLUID.clear();
		Map<ResourceKey<Fluid>, SoilInfo> fluidDataMap = fluidRegistry.getDataMap(ModDataMaps.FLUID_SOIL);
		for(Map.Entry<ResourceKey<Fluid>, SoilInfo> entry : fluidDataMap.entrySet()) {
			SOIL_BY_FLUID.put(fluidRegistry.get(entry.getKey()), entry.getValue());
			ResourceLocation type = entry.getValue().soilType();
			if(!SOIL_BY_TYPE.containsKey(type)) {
				SOIL_BY_TYPE.put(type, new HashMap<>());
			}

			SOIL_BY_TYPE.get(type).put(fluidRegistry.get(entry.getKey()).getBucket(), entry.getValue());
			SOILS.put(new ItemStack(fluidRegistry.get(entry.getKey()).getBucket()), entry.getValue());
		}


		SOIL_BY_ITEM.clear();
		Map<ResourceKey<Item>, SoilInfoWithTexture> itemDataMap = itemRegistry.getDataMap(ModDataMaps.ITEM_SOIL);
		for(Map.Entry<ResourceKey<Item>, SoilInfoWithTexture> entry : itemDataMap.entrySet()) {
			SOIL_BY_ITEM.put(itemRegistry.get(entry.getKey()), entry.getValue());
			ResourceLocation type = entry.getValue().soilType();
			if(!SOIL_BY_TYPE.containsKey(type)) {
				SOIL_BY_TYPE.put(type, new HashMap<>());
			}

			var soilInfo = SoilInfo.fromSoilInfoWithTexture(entry.getValue());
			SOIL_BY_TYPE.get(type).put(itemRegistry.get(entry.getKey()), soilInfo);
			SOILS.put(new ItemStack(itemRegistry.get(entry.getKey())), soilInfo);
		}

		BONSAIS_BY_SOIL.clear();
		for(var entry : BonsaiCache.BONSAI_BY_ITEM.entrySet()) {
			Item bonsaiItem = entry.getKey();
			BonsaiInfo bonsaiInfo = entry.getValue();
			List<SoilType> validSoilTypes = bonsaiInfo.validSoilTypes(event.getRegistries());
			for(SoilType soilType : validSoilTypes) {
				if(!BONSAIS_BY_SOIL.containsKey(soilType)) {
					BONSAIS_BY_SOIL.put(soilType, new HashSet<>());
				}
				BONSAIS_BY_SOIL.get(soilType).add(bonsaiItem);
			}
		}
	}

	public static boolean isSoil(ItemStack soilStack) {
		return getSoilInfo(soilStack).isPresent();
	}

	public static Optional<SoilInfo> getSoilInfo(ItemStack soilStack) {
		if(!soilStack.isEmpty() && soilStack.getItem() instanceof BlockItem item) {
			return Optional.ofNullable(SoilCache.SOIL_BY_BLOCK.get(item.getBlock()));
		}

		if(!soilStack.isEmpty() && soilStack.getItem() instanceof BucketItem bucket) {
			return Optional.ofNullable(SoilCache.SOIL_BY_FLUID.get(bucket.content));
		}

		if(!soilStack.isEmpty() && SoilCache.SOIL_BY_ITEM.containsKey(soilStack.getItem())) {
			SoilInfoWithTexture soilInfoWithTexture = SoilCache.SOIL_BY_ITEM.get(soilStack.getItem());
			if(soilInfoWithTexture == null) {
				return Optional.empty();
			}
			return Optional.of(SoilInfo.fromSoilInfoWithTexture(soilInfoWithTexture));
		}

		return Optional.empty();
	}
}
