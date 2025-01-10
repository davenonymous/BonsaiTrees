package com.davenonymous.bonsaitrees.lib.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class Sorting {
	public static final Comparator<Item> ITEM_COMPARATOR = Comparator.comparing(item -> {
		ResourceLocation itemId = item.builtInRegistryHolder().getKey().location();
		if(itemId.getNamespace().equals("minecraft")) {
			return "000" + itemId;
		}
		return "999" + itemId;
	});

	public static final Comparator<ItemStack> ITEMSTACK_COMPARATOR = Comparator.comparing(itemStack -> {
		ResourceLocation itemId = itemStack.getItem().builtInRegistryHolder().getKey().location();
		if(itemId.getNamespace().equals("minecraft")) {
			return "000" + itemId;
		}
		return "999" + itemId;
	});

	public static List<ItemStack> stacksToSortedList(Collection<ItemStack> collection) {
		return collection.stream().sorted(ITEMSTACK_COMPARATOR).toList();
	}

	public static List<Item> toSortedList(Collection<Item> collection) {
		return collection.stream().sorted(ITEM_COMPARATOR).toList();
	}

	public static Queue<Item> toSortedQueue(Collection<Item> collection) {
		return collection.stream().sorted(ITEM_COMPARATOR).collect(LinkedList::new, LinkedList::add, LinkedList::addAll);
	}
}
