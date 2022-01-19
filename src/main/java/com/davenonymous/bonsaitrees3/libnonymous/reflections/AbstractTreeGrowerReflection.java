package com.davenonymous.bonsaitrees3.libnonymous.reflections;

import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

public class AbstractTreeGrowerReflection {
	private static Method getConfiguredFeature;

	static {
		getConfiguredFeature = ObfuscationReflectionHelper.findMethod(AbstractTreeGrower.class, "m_6486_", Random.class, boolean.class);
		getConfiguredFeature.setAccessible(true);
	}

	@SuppressWarnings("unchecked")
	public static ConfiguredFeature<?, ?> getConfiguredFeature(AbstractTreeGrower grower, Random random, boolean pLargeHive) {
		try {
			ConfiguredFeature<?, ?> feature = (ConfiguredFeature<?, ?>) getConfiguredFeature.invoke(grower, random, pLargeHive);
			return feature;
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}

		return null;
	}
}