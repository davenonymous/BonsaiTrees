package com.davenonymous.bonsaitrees.lib.gui;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;

import java.util.function.IntFunction;

public enum RedstoneMode implements StringRepresentable {
	IGNORE_POWER(0, "ignore", Component.translatable("bonsaitrees4.redstone_mode.ignore_power")),
	REQUIRE_POWER(1, "require", Component.translatable("bonsaitrees4.redstone_mode.require_power")),
	STOP_ON_POWER(2, "stop", Component.translatable("bonsaitrees4.redstone_mode.stop_on_power"));

	private final int id;
	private final String key;
	private final Component description;

	public static final IntFunction<RedstoneMode> BY_ID =
		ByIdMap.continuous(
			RedstoneMode::getId,
			RedstoneMode.values(),
			ByIdMap.OutOfBoundsStrategy.ZERO
		);

	public static final StringRepresentable.EnumCodec<RedstoneMode> CODEC = StringRepresentable.fromEnum(RedstoneMode::values);

	public static final StreamCodec<ByteBuf, RedstoneMode> STREAM_CODEC =
		ByteBufCodecs.idMapper(RedstoneMode.BY_ID, RedstoneMode::getId);

	RedstoneMode(int id, String key, Component description) {
		this.description = description;
		this.id = id;
		this.key = key;
	}

	public boolean resolve(Level level, BlockPos pos) {
		return switch(this) {
			case IGNORE_POWER -> true;
			case REQUIRE_POWER -> level.hasNeighborSignal(pos);
			case STOP_ON_POWER -> !level.hasNeighborSignal(pos);
			default -> false;
		};
	}

	public static RedstoneMode byId(int id) {
		return BY_ID.apply(id);
	}

	public int getId() {
		return this.id;
	}

	public Component getDescription() {
		return this.description;
	}

	@Override
	public String getSerializedName() {
		return this.key;
	}
}
