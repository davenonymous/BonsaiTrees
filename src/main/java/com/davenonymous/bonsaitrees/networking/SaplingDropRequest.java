package com.davenonymous.bonsaitrees.networking;

import com.davenonymous.bonsaitrees.BonsaiTrees;
import com.davenonymous.bonsaitrees.setup.cache.LootCache;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SaplingDropRequest() implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<SaplingDropRequest> TYPE = new CustomPacketPayload.Type<>(BonsaiTrees.resource("sapling_drops_request"));

	public static final StreamCodec<RegistryFriendlyByteBuf, SaplingDropRequest> STREAM_CODEC = StreamCodec.unit(new SaplingDropRequest());

	public static void handle(SaplingDropRequest message, IPayloadContext context) {
		BonsaiTrees.LOGGER.info("Received sapling drop request from server");
		LootCache.DROPS_BY_BONSAI.clear();
		PacketDistributor.sendToServer(new SaplingDropAck());
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
