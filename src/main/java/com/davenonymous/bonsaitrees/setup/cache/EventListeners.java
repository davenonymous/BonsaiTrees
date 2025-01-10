package com.davenonymous.bonsaitrees.setup.cache;


import com.davenonymous.bonsaitrees.BonsaiTrees;
import com.davenonymous.bonsaitrees.networking.SaplingDropRequest;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.datamaps.DataMapsUpdatedEvent;

@EventBusSubscriber(modid = BonsaiTrees.MODID, bus = EventBusSubscriber.Bus.GAME)
public class EventListeners {
	@SubscribeEvent
	public static void dataMapsUpdated(DataMapsUpdatedEvent event) {
		SoilTypeCache.dataMapsUpdated(event);
		BonsaiCache.dataMapsUpdated(event);
		SoilCache.dataMapsUpdated(event);
		ItemAbilityCache.dataMapsUpdated(event);

		JeiRecipeCache.dataMapsUpdated(event);
	}

	@SubscribeEvent
	private static void onDataPackSync(OnDatapackSyncEvent event) {
		ServerPlayer player = event.getPlayer();
		if(player == null) {
			return;
		}

		PacketDistributor.sendToPlayer(player, new SaplingDropRequest());
	}
}
