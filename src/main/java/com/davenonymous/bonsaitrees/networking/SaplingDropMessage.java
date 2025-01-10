package com.davenonymous.bonsaitrees.networking;

import com.davenonymous.bonsaitrees.BonsaiTrees;
import com.davenonymous.bonsaitrees.lib.util.LootHelper;
import com.davenonymous.bonsaitrees.setup.cache.LootCache;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record SaplingDropMessage(ResourceLocation sapling, List<LootHelper.LootTableDrop> drops) implements CustomPacketPayload {

	public static final Type<SaplingDropMessage> TYPE = new Type<>(BonsaiTrees.resource("sapling_drops"));

	public static final StreamCodec<RegistryFriendlyByteBuf, SaplingDropMessage> STREAM_CODEC = StreamCodec.composite(
		ResourceLocation.STREAM_CODEC, SaplingDropMessage::sapling,
		LootHelper.LootTableDrop.STREAM_CODEC.apply(ByteBufCodecs.list()), SaplingDropMessage::drops,
		SaplingDropMessage::new
	);

	public static void handle(SaplingDropMessage message, IPayloadContext context) {
		BonsaiTrees.LOGGER.info("Received sapling drop message from server for sapling {}", message.sapling());
		LootCache.DROPS_BY_BONSAI.put(message.sapling(), message.drops());
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
