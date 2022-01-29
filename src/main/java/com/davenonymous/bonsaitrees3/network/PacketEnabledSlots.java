package com.davenonymous.bonsaitrees3.network;

import com.davenonymous.libnonymous.base.BasePacket;
import com.davenonymous.libnonymous.gui.framework.WidgetSlot;
import com.davenonymous.libnonymous.serialization.Sync;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class PacketEnabledSlots extends BasePacket {
	@Sync
	boolean[] enabledSlots;

	public PacketEnabledSlots(List<Slot> slots) {
		super();
		this.enabledSlots = new boolean[slots.size()];

		int index = 0;
		for(Slot slot : slots) {
			if(slot instanceof WidgetSlot ws) {
				this.enabledSlots[index] = ws.isEnabled();
			}
			index++;
		}
	}

	public PacketEnabledSlots(boolean[] enabledSlots) {
		super();
		this.enabledSlots = enabledSlots;
	}

	public PacketEnabledSlots(FriendlyByteBuf buf) {
		super(buf);
	}

	@Override
	public void doWork(Supplier<NetworkEvent.Context> ctx) {
		var serverPlayer = ctx.get().getSender();
		int index = 0;
		for(Slot slot : serverPlayer.containerMenu.slots) {
			if(slot instanceof WidgetSlot) {
				if(index >= this.enabledSlots.length) {
					break;
				}

				((WidgetSlot) slot).setEnabled(this.enabledSlots[index]);
			}

			index++;
		}
	}
}