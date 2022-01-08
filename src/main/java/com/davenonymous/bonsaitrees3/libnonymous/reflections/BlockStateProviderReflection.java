package com.davenonymous.bonsaitrees3.libnonymous.reflections;

import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;

public class BlockStateProviderReflection {
	private static Field weightedList;

	static {
		weightedList = ObfuscationReflectionHelper.findField(WeightedStateProvider.class, "f_68809_");
		weightedList.setAccessible(true);
	}

	@SuppressWarnings("unchecked")
	public static <T> SimpleWeightedRandomList<T> getWeightedListFromWeightedStateProvider(Class<T> type, WeightedStateProvider providerInstance) {
		try {
			return (SimpleWeightedRandomList<T>) weightedList.get(providerInstance);
		} catch (IllegalAccessException e) {
		}

		return null;
	}
}