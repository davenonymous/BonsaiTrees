package com.davenonymous.bonsaitrees.setup.cache;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.registries.datamaps.DataMapsUpdatedEvent;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ItemAbilityCache {
	public static final Map<ItemAbility, List<ItemStack>> ITEMS_FOR_ABILITY = new HashMap<>();
	public static final List<ItemAbility> ABILITIES = List.of(
		ItemAbilities.AXE_DIG,
		ItemAbilities.PICKAXE_DIG,
		ItemAbilities.SHOVEL_DIG,
		ItemAbilities.HOE_DIG,
		ItemAbilities.SWORD_DIG,
		ItemAbilities.SHEARS_DIG,
		ItemAbilities.AXE_STRIP,
		ItemAbilities.AXE_SCRAPE,
		ItemAbilities.AXE_WAX_OFF,
		ItemAbilities.SHOVEL_FLATTEN,
		ItemAbilities.SHOVEL_DOUSE,
		ItemAbilities.SWORD_SWEEP,
		ItemAbilities.SHEARS_HARVEST,
		ItemAbilities.SHEARS_REMOVE_ARMOR,
		ItemAbilities.SHEARS_CARVE,
		ItemAbilities.SHEARS_DISARM,
		ItemAbilities.SHEARS_TRIM,
		ItemAbilities.HOE_TILL,
		ItemAbilities.SHIELD_BLOCK,
		ItemAbilities.FISHING_ROD_CAST,
		ItemAbilities.TRIDENT_THROW,
		ItemAbilities.BRUSH_BRUSH,
		ItemAbilities.FIRESTARTER_LIGHT
	);

	public static Ingredient getIngredientForAbility(ItemAbility ability) {
		return Ingredient.of(ITEMS_FOR_ABILITY.get(ability).stream());
	}

	public static void dataMapsUpdated(DataMapsUpdatedEvent event) {
		event.ifRegistry(Registries.ITEM, itemRegistry -> {
			itemRegistry.stream().forEach(item -> {
				ItemStack stack = new ItemStack(item);
				ABILITIES.forEach(ability -> {
					if(stack.canPerformAction(ability)) {
						if(!ITEMS_FOR_ABILITY.containsKey(ability)) {
							ITEMS_FOR_ABILITY.put(ability, new LinkedList<>());
						}
						if(ITEMS_FOR_ABILITY.get(ability).stream().anyMatch(existing -> ItemStack.isSameItem(existing, stack))) {
							return;
						}

						ITEMS_FOR_ABILITY.get(ability).add(stack);
					}
				});
			});
		});
	}

}
