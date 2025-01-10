package com.davenonymous.bonsaitrees.setup.cache;

import com.davenonymous.bonsaitrees.setup.ModDataMaps;
import com.davenonymous.bonsaitrees.setup.data.BonsaiInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.datamaps.DataMapsUpdatedEvent;

import java.util.HashMap;
import java.util.Map;

public class BonsaiCache {
	public static final Map<Item, BonsaiInfo> BONSAI_BY_ITEM = new HashMap<>();
	public static final Map<ResourceLocation, BonsaiInfo> BONSAI_BY_RESOURCE = new HashMap<>();

	public static void dataMapsUpdated(DataMapsUpdatedEvent event) {
		event.ifRegistry(Registries.ITEM, itemRegistry -> {
			BONSAI_BY_ITEM.clear();
			BONSAI_BY_RESOURCE.clear();

			Map<ResourceKey<Item>, BonsaiInfo> data = itemRegistry.getDataMap(ModDataMaps.BONSAI);
			for(Map.Entry<ResourceKey<Item>, BonsaiInfo> entry : data.entrySet()) {
				var itemKey = entry.getKey();
				var bonsai = entry.getValue();
				var item = itemRegistry.get(itemKey);
				BONSAI_BY_ITEM.put(item, bonsai);
				BONSAI_BY_RESOURCE.put(itemKey.location(), bonsai);
			}

		});
	}
}
