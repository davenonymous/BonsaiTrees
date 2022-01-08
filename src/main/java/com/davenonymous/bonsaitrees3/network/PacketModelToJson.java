package com.davenonymous.bonsaitrees3.network;

import com.davenonymous.bonsaitrees3.libnonymous.base.BasePacket;
import com.davenonymous.bonsaitrees3.libnonymous.serialization.MultiblockBlockModel;
import com.davenonymous.bonsaitrees3.libnonymous.serialization.Sync;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketModelToJson extends BasePacket {
	@Sync
	MultiblockBlockModel model;

	public PacketModelToJson(MultiblockBlockModel model) {
		super();
		this.model = model;
	}

	public PacketModelToJson(FriendlyByteBuf buf) {
		super(buf);
	}

	@Override
	public void doWork(Supplier<NetworkEvent.Context> ctx) {
		Minecraft.getInstance().keyboardHandler.setClipboard(model.serializePretty());
	}
}