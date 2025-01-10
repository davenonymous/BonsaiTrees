package com.davenonymous.bonsaitrees.networking;

import com.davenonymous.bonsaitrees.BonsaiTrees;
import com.davenonymous.bonsaitrees.blocks.BonsaiPotBlockEntity;
import com.davenonymous.bonsaitrees.lib.gui.RedstoneMode;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SetRedstoneMode(BlockPos pos, RedstoneMode mode) implements CustomPacketPayload {
	public static final Type<SetRedstoneMode> TYPE = new Type<>(BonsaiTrees.resource("set_redstone_mode"));

	public static final StreamCodec<FriendlyByteBuf, SetRedstoneMode> STREAM_CODEC = StreamCodec.composite(
		BlockPos.STREAM_CODEC, SetRedstoneMode::pos,
		RedstoneMode.STREAM_CODEC, SetRedstoneMode::mode,
		SetRedstoneMode::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(SetRedstoneMode message, IPayloadContext context) {
		var level = context.player().level();
		BonsaiPotBlockEntity potBlock = (BonsaiPotBlockEntity) level.getBlockEntity(message.pos);
		if(potBlock != null) {
			potBlock.setRedstoneMode(message.mode());
		}
	}
}
