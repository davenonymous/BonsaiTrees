package com.davenonymous.bonsaitrees.setup.cache;

import com.davenonymous.bonsaitrees.lib.util.LootHelper;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LootCache {
	// This is a client-side cache of the drops for each bonsai, so that we don't have to re-fetch them every time the JEI recipe is drawn
	// Do not use this from server side code
	public static final Map<ResourceLocation, List<LootHelper.LootTableDrop>> DROPS_BY_BONSAI = new HashMap<>();
}
