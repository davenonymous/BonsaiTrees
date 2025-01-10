package com.davenonymous.bonsaitrees.setup;

import com.davenonymous.bonsaitrees.BonsaiTrees;
import com.davenonymous.bonsaitrees.setup.data.SoilType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

@EventBusSubscriber(modid = BonsaiTrees.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModRegistries {
	public static final ResourceKey<Registry<SoilType>> SOILTYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(
		BonsaiTrees.resource("soiltype"));


	@SubscribeEvent
	static void newRegistry(DataPackRegistryEvent.NewRegistry event) {
		event.dataPackRegistry(SOILTYPE_REGISTRY_KEY, SoilType.codec(), SoilType.codec());
	}
}