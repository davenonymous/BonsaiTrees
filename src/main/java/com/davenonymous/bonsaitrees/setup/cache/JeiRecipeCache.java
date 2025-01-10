package com.davenonymous.bonsaitrees.setup.cache;

import com.davenonymous.bonsaitrees.compatibility.jei.BonsaiRecipe;
import com.davenonymous.bonsaitrees.setup.data.SoilInfo;
import com.davenonymous.bonsaitrees.setup.data.SoilType;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.datamaps.DataMapsUpdatedEvent;

import java.util.*;

public class JeiRecipeCache {

	public static final List<BonsaiRecipe> ALL_RECIPES = new ArrayList<>();
	public static final Map<Item, Set<BonsaiRecipe>> RECIPES_BY_INPUT = new HashMap<>();
	public static final Map<Item, Set<BonsaiRecipe>> RECIPES_BY_OUTPUT = new HashMap<>();
	public static final Map<SoilType, Set<BonsaiRecipe>> RECIPES_BY_SOILTYPE = new HashMap<>();

	public static void update(RegistryAccess registryAccess) {
		ALL_RECIPES.clear();
		RECIPES_BY_INPUT.clear();
		RECIPES_BY_OUTPUT.clear();
		RECIPES_BY_SOILTYPE.clear();

		BonsaiCache.BONSAI_BY_ITEM.forEach((item, bonsai) -> {
			BonsaiRecipe recipe = new BonsaiRecipe(item, bonsai);
			ALL_RECIPES.add(recipe);

			if(!RECIPES_BY_INPUT.containsKey(item)) {
				RECIPES_BY_INPUT.put(item, new HashSet<>());
			}
			RECIPES_BY_INPUT.get(item).add(recipe);

			bonsai.validSoilTypes(registryAccess).forEach(soilType -> {
				if(!RECIPES_BY_SOILTYPE.containsKey(soilType)) {
					RECIPES_BY_SOILTYPE.put(soilType, new HashSet<>());
				}
				RECIPES_BY_SOILTYPE.get(soilType).add(recipe);

				Map<Item, SoilInfo> soilItems = SoilCache.SOIL_BY_TYPE.get(soilType.id());
				if(soilItems == null) {
					return;
				}
				soilItems.forEach((soilItem, soilInfo) -> {
					if(!RECIPES_BY_INPUT.containsKey(soilItem)) {
						RECIPES_BY_INPUT.put(soilItem, new HashSet<>());
					}
					RECIPES_BY_INPUT.get(soilItem).add(recipe);
				});
			});

			ResourceLocation saplingId = item.builtInRegistryHolder().getKey().location();
			if(LootCache.DROPS_BY_BONSAI.containsKey(saplingId)) {
				LootCache.DROPS_BY_BONSAI.get(saplingId).forEach(drop -> {
					Item dropItem = drop.stack().getItem();
					if(!RECIPES_BY_OUTPUT.containsKey(dropItem)) {
						RECIPES_BY_OUTPUT.put(dropItem, new HashSet<>());
					}
					RECIPES_BY_OUTPUT.get(dropItem).add(recipe);
				});
			}
		});

		ALL_RECIPES.sort(Comparator.comparing(bonsai -> {
			ResourceLocation itemId = bonsai.sapling().builtInRegistryHolder().getKey().location();
			if(itemId.getNamespace().equals("minecraft")) {
				return "000" + itemId;
			}
			return "999" + itemId;
		}));
	}

	public static void dataMapsUpdated(DataMapsUpdatedEvent event) {
		update(event.getRegistries());
	}
}
