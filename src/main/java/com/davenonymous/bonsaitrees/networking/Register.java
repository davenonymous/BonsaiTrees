package com.davenonymous.bonsaitrees.networking;

import com.davenonymous.bonsaitrees.BonsaiTrees;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = BonsaiTrees.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Register {
	@SubscribeEvent
	public static void register(final RegisterPayloadHandlersEvent event) {
		final PayloadRegistrar registrar = event.registrar("1");

		registrar.playToClient(
			SaplingDropMessage.TYPE,
			SaplingDropMessage.STREAM_CODEC,
			SaplingDropMessage::handle
		);

		registrar.playToClient(
			SaplingDropRequest.TYPE,
			SaplingDropRequest.STREAM_CODEC,
			SaplingDropRequest::handle
		);

		registrar.playBidirectional(
			SaplingDropAck.TYPE,
			SaplingDropAck.STREAM_CODEC,
			new DirectionalPayloadHandler<>(
				SaplingDropAck::handleOnClient,
				SaplingDropAck::handleOnServer
			)
		);

		registrar.playToServer(
			SetRedstoneMode.TYPE,
			SetRedstoneMode.STREAM_CODEC,
			SetRedstoneMode::handle
		);
	}
}
