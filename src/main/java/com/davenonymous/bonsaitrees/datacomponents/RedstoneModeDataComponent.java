package com.davenonymous.bonsaitrees.datacomponents;

import com.davenonymous.bonsaitrees.lib.gui.RedstoneMode;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record RedstoneModeDataComponent(RedstoneMode mode) {
	public static final Codec<RedstoneModeDataComponent> CODEC = RecordCodecBuilder.create(instance ->
		instance.group(
			RedstoneMode.CODEC.fieldOf("mode").forGetter(RedstoneModeDataComponent::mode)
		).apply(instance, RedstoneModeDataComponent::new)
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, RedstoneModeDataComponent> STREAM_CODEC = StreamCodec.composite(
		RedstoneMode.STREAM_CODEC, RedstoneModeDataComponent::mode,
		RedstoneModeDataComponent::new
	);
}
