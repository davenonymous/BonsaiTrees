package com.davenonymous.bonsaitrees3.libnonymous.base;

import com.davenonymous.bonsaitrees3.libnonymous.serialization.packetbuffer.PacketBufferFieldSerializationData;
import com.davenonymous.bonsaitrees3.libnonymous.serialization.packetbuffer.PacketBufferUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public abstract class BasePacket {
	public List<PacketBufferFieldSerializationData> IOActions;

	public BasePacket() {
		IOActions = PacketBufferUtils.initSerializableSyncFields(this.getClass());
	}

	public BasePacket(FriendlyByteBuf buf) {
		IOActions = PacketBufferUtils.initSerializableSyncFields(this.getClass());

		PacketBufferUtils.readFieldsFromByteBuf(IOActions, this, buf, data -> true);
	}

	public void toBytes(FriendlyByteBuf buf) {
		PacketBufferUtils.writeFieldsToByteBuf(IOActions, this, buf, data -> true);
	}

	public abstract void doWork(Supplier<NetworkEvent.Context> ctx);

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> doWork(ctx));
		ctx.get().setPacketHandled(true);
	}
}
