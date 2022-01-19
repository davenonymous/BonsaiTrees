package com.davenonymous.bonsaitrees3.libnonymous.reflections;

import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;

public class SaplingBlockReflection {
	private static Field treeGrower;

	static {
		treeGrower = ObfuscationReflectionHelper.findField(SaplingBlock.class, "f_55975_");
		treeGrower.setAccessible(true);
	}

	@SuppressWarnings("unchecked")
	public static AbstractTreeGrower getTreeGrowerFromSaplingBlock(SaplingBlock saplingBlock) {
		try {
			return (AbstractTreeGrower) treeGrower.get(saplingBlock);
		} catch (IllegalAccessException e) {
		}

		return null;
	}
}