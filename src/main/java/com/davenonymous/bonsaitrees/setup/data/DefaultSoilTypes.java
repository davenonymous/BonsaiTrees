package com.davenonymous.bonsaitrees.setup.data;

import com.davenonymous.bonsaitrees.BonsaiTrees;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class DefaultSoilTypes {
	public static final ResourceKey<Registry<SoilType>> SOILTYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(
		BonsaiTrees.resource("soiltype"));

	private static ResourceKey<SoilType> builtIn(String name) {
		return ResourceKey.create(SOILTYPE_REGISTRY_KEY, BonsaiTrees.resource(name));
	}

	public static final ResourceKey<SoilType> DIRT = builtIn("dirt");
	public static final ResourceKey<SoilType> SAND = builtIn("sand");
	public static final ResourceKey<SoilType> WATER = builtIn("water");
	public static final ResourceKey<SoilType> LAVA = builtIn("lava");
	public static final ResourceKey<SoilType> STONE = builtIn("stone");
	public static final ResourceKey<SoilType> END_STONE = builtIn("end_stone");
	public static final ResourceKey<SoilType> NETHER_STONE = builtIn("nether_stone");
	public static final ResourceKey<SoilType> MYCELIUM = builtIn("mycelium");
	public static final ResourceKey<SoilType> NYLIUM = builtIn("nylium");
}
