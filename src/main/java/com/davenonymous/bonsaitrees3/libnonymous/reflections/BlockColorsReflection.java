package com.davenonymous.bonsaitrees3.libnonymous.reflections;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.IRegistryDelegate;

import java.lang.reflect.Field;
import java.util.Map;

public class BlockColorsReflection {
	private static Field blockColors;

	static {
		blockColors = ObfuscationReflectionHelper.findField(BlockColors.class, "f_92571_");
		blockColors.setAccessible(true);
	}

	@SuppressWarnings("unchecked")
	public static Map<IRegistryDelegate<Block>, BlockColor> getBlockColorsMaps(BlockColors blockColorsInstance) {
		try {
			return (Map<IRegistryDelegate<Block>, BlockColor>) blockColors.get(blockColorsInstance);
		} catch (IllegalAccessException e) {
		}

		return null;
	}

}