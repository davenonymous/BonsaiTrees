package com.davenonymous.bonsaitrees3.network;

import com.davenonymous.bonsaitrees3.blocks.BonsaiPotBlockEntity;
import com.davenonymous.libnonymous.base.BasePacket;
import com.davenonymous.libnonymous.helper.RedstoneMode;
import com.davenonymous.libnonymous.serialization.Sync;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SetRedstoneMode extends BasePacket {
	@Sync
	BlockPos pos;

	@Sync
	RedstoneMode mode;

	public SetRedstoneMode(BlockPos pos, RedstoneMode mode) {
		super();
		this.pos = pos;
		this.mode = mode;
	}

	public SetRedstoneMode(FriendlyByteBuf buf) {
		super(buf);
	}

	@Override
	public void doWork(Supplier<NetworkEvent.Context> ctx) {
		var level = ctx.get().getSender().getLevel();
		BonsaiPotBlockEntity potBlock = (BonsaiPotBlockEntity) level.getBlockEntity(this.pos);
		if(potBlock != null) {
			potBlock.redstoneMode = this.mode;
			potBlock.setChanged();
			potBlock.notifyClients();
		}
	}
}