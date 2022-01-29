package com.davenonymous.bonsaitrees3.network;

import com.davenonymous.bonsaitrees3.BonsaiTrees3;
import com.davenonymous.libnonymous.helper.RedstoneMode;
import com.davenonymous.libnonymous.serialization.MultiblockBlockModel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.Connection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.List;
import java.util.Optional;

public class Networking {
	public static SimpleChannel INSTANCE;
	private static final String CHANNEL_NAME = "channel";
	private static int ID = 0;

	public static int nextID() {
		return ID++;
	}

	public static void registerMessages() {
		INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(BonsaiTrees3.MODID, CHANNEL_NAME), () -> "1.0", s -> true, s -> true);

		INSTANCE.registerMessage(nextID(), CutBonsaiPacket.class, CutBonsaiPacket::toBytes, CutBonsaiPacket::new, CutBonsaiPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		INSTANCE.registerMessage(nextID(), PacketEnabledSlots.class, PacketEnabledSlots::toBytes, PacketEnabledSlots::new, PacketEnabledSlots::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		INSTANCE.registerMessage(nextID(), PacketModelToJson.class, PacketModelToJson::toBytes, PacketModelToJson::new, PacketModelToJson::doWork, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		INSTANCE.registerMessage(nextID(), SetRedstoneMode.class, SetRedstoneMode::toBytes, SetRedstoneMode::new, SetRedstoneMode::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
	}

	public static void sendCutTreeToServer(BlockPos pos) {
		INSTANCE.sendToServer(new CutBonsaiPacket(pos));
	}

	public static void sendRedstoneModeToServer(BlockPos pos, RedstoneMode mode) {
		INSTANCE.sendToServer(new SetRedstoneMode(pos, mode));
	}

	public static void sendEnabledSlotsMessage(List<Slot> inventorySlots) {
		INSTANCE.sendToServer(new PacketEnabledSlots(inventorySlots));
	}

	public static void sendModelToClipboard(Connection target, MultiblockBlockModel model) {
		INSTANCE.sendTo(new PacketModelToJson(model), target, NetworkDirection.PLAY_TO_CLIENT);
	}

}