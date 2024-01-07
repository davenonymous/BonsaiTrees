package com.davenonymous.bonsaitrees3.registry;


import com.davenonymous.bonsaitrees3.BonsaiTrees3;
import com.davenonymous.bonsaitrees3.registry.sapling.SaplingInfo;
import com.davenonymous.bonsaitrees3.registry.soil.SoilInfo;
import com.davenonymous.bonsaitrees3.setup.Registration;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

import java.util.*;

public class SoilCompatibility {
	public static final SoilCompatibility INSTANCE = new SoilCompatibility();

	private List<Item> additionalUpgradeItems;
	private Map<SoilInfo, Set<SaplingInfo>> treeCompatibility;
	private Map<SaplingInfo, Set<SoilInfo>> soilCompatibility;
	public boolean isReady = false;

	private void addCompatEntry(SoilInfo soil, SaplingInfo tree) {
		if(!soilCompatibility.containsKey(tree)) {
			soilCompatibility.put(tree, new HashSet<>());
		}

		soilCompatibility.get(tree).add(soil);

		if(!treeCompatibility.containsKey(soil)) {
			treeCompatibility.put(soil, new HashSet<>());
		}

		treeCompatibility.get(soil).add(tree);
	}


	public Set<SoilInfo> getValidSoilsForSapling(SaplingInfo sapling) {
		return soilCompatibility.getOrDefault(sapling, new HashSet<>());
	}

	public boolean canTreeGrowOnSoil(SaplingInfo sapling, SoilInfo soil) {
		if(!soilCompatibility.containsKey(sapling) || soilCompatibility.get(sapling) == null) {
			return false;
		}

		return soilCompatibility.get(sapling).contains(soil);
	}

	public boolean isValidSoil(ItemStack soilStack) {
		for(SoilInfo soil : treeCompatibility.keySet()) {
			if(soil.ingredient.test(soilStack)) {
				return true;
			}
		}

		return false;
	}

	public boolean isUpgradeItem(ItemStack upgradeStack) {
		if(additionalUpgradeItems == null) {
			return true;
		}
		return additionalUpgradeItems.contains(upgradeStack.getItem());
	}

	public void update(Collection<Recipe<?>> recipes) {
		if(recipes == null || recipes.size() <= 0) {
			return;
		}

		List<SaplingInfo> saplings = recipes.stream().filter(r -> r.getType() == Registration.RECIPE_TYPE_SAPLING.get()).map(r -> (SaplingInfo) r).toList();
		List<SoilInfo> soils = recipes.stream().filter(r -> r.getType() == Registration.RECIPE_TYPE_SOIL.get()).map(r -> (SoilInfo) r).toList();

		treeCompatibility = new HashMap<>();
		soilCompatibility = new HashMap<>();
		additionalUpgradeItems = new ArrayList<>();

		Map<String, Set<SoilInfo>> reverseSoilTagMap = new HashMap<>();
		for(SoilInfo soil : soils) {
			for(String tag : soil.tags) {
				if(!reverseSoilTagMap.containsKey(tag)) {
					reverseSoilTagMap.put(tag, new HashSet<>());
				}

				reverseSoilTagMap.get(tag).add(soil);
			}
		}

		for(SaplingInfo sapling : saplings) {
			for(String tag : sapling.tags) {
				if(!reverseSoilTagMap.containsKey(tag)) {
					continue;
				}

				for(SoilInfo soil : reverseSoilTagMap.get(tag)) {
					this.addCompatEntry(soil, sapling);
				}
			}

			for(var drop : sapling.drops) {
				if(drop.requiredUpgrades.isEmpty()) {
					continue;
				}

				for(var item : drop.requiredUpgrades.getItems()) {
					if(!additionalUpgradeItems.contains(item.getItem())) {
						additionalUpgradeItems.add(item.getItem());
					}
				}
			}
		}

		BonsaiTrees3.LOGGER.info("Updated soil compatibility");
		BonsaiTrees3.LOGGER.info("Updated additional upgrade items. Found {}.", additionalUpgradeItems.size());
		isReady = true;
	}
}