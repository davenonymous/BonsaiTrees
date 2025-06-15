package com.davenonymous.bonsaitrees.lib.util;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public class StackHelper {

	public static List<ItemStack> mergeStacks(List<ItemStack> stacks) {
		stacks.removeIf(ItemStack::isEmpty);

		for(int i = 0; i < stacks.size(); i++) {
			ItemStack stackA = stacks.get(i);

			for(int j = i + 1; j < stacks.size(); j++) {
				ItemStack stackB = stacks.get(j);

				if(!ItemStack.isSameItemSameComponents(stackA, stackB)) {
					continue;
				}

				int mergedCount = stackA.getCount() + stackB.getCount();
				int actualMergedCount = Math.min(mergedCount, stackA.getMaxStackSize());
				stackA.setCount(actualMergedCount);
				stackB.setCount(mergedCount - actualMergedCount);
			}
		}
		stacks.removeIf(ItemStack::isEmpty);

		return stacks;
	}
}
