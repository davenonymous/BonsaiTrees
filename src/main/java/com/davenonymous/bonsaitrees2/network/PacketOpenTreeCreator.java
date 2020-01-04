package com.davenonymous.bonsaitrees2.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketOpenTreeCreator {
    public PacketOpenTreeCreator() {
    }

    public PacketOpenTreeCreator(PacketBuffer buf) {
    }

    public void toBytes(PacketBuffer buf) {
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        //ctx.get().enqueueWork(TreeCreatorScreen::open);
        ctx.get().setPacketHandled(true);
    }
}
