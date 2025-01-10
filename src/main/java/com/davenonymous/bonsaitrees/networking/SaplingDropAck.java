package com.davenonymous.bonsaitrees.networking;

import com.davenonymous.bonsaitrees.BonsaiTrees;
import com.davenonymous.bonsaitrees.lib.util.LootHelper;
import com.davenonymous.bonsaitrees.setup.cache.BonsaiCache;
import com.davenonymous.bonsaitrees.setup.cache.JeiRecipeCache;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;
import java.util.Optional;

public record SaplingDropAck() implements CustomPacketPayload {
	public static final Type<SaplingDropAck> TYPE = new Type<>(BonsaiTrees.resource("sapling_drops_ack"));

	public static final StreamCodec<RegistryFriendlyByteBuf, SaplingDropAck> STREAM_CODEC = StreamCodec.unit(new SaplingDropAck());

	public static void handleOnServer(SaplingDropAck message, IPayloadContext context) {
		BonsaiTrees.LOGGER.info("Received sapling ack message from client");
		ServerPlayer player = (ServerPlayer) context.player();

		LootParams lootParams = new LootParams.Builder(player.serverLevel()).create(LootContextParamSets.EMPTY);
		LootContext lootContext = new LootContext.Builder(lootParams).create(Optional.empty());
		ServerLevel level = player.serverLevel();
		for(var bonsai : BonsaiCache.BONSAI_BY_RESOURCE.entrySet()) {
			ResourceLocation itemId = bonsai.getKey();
			ResourceKey<LootTable> lootTable = bonsai.getValue().lootTable();

			List<LootHelper.LootTableDrop> drops = LootHelper.getLootTableDrops(lootTable, level, lootContext);
			PacketDistributor.sendToPlayer(player, new SaplingDropMessage(itemId, drops));
		}

		PacketDistributor.sendToPlayer(player, new SaplingDropAck());
	}

	public static void handleOnClient(SaplingDropAck message, IPayloadContext context) {
		BonsaiTrees.LOGGER.info("Received sapling ack message from server");
		JeiRecipeCache.update(Minecraft.getInstance().level.registryAccess());
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
