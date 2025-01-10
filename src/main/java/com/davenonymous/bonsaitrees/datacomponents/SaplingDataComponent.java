package com.davenonymous.bonsaitrees.datacomponents;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public record SaplingDataComponent(ResourceLocation sapling, Optional<Float> progress) {
	public static final Codec<SaplingDataComponent> CODEC = RecordCodecBuilder.create(instance ->
		instance.group(
			ResourceLocation.CODEC.fieldOf("sapling").forGetter(SaplingDataComponent::sapling),
			Codec.FLOAT.optionalFieldOf("progress").forGetter(SaplingDataComponent::progress)
		).apply(instance, SaplingDataComponent::new)
	);

	public static final StreamCodec<ByteBuf, SaplingDataComponent> STREAM_CODEC = StreamCodec.composite(
		ResourceLocation.STREAM_CODEC, SaplingDataComponent::sapling,
		ByteBufCodecs.optional(ByteBufCodecs.FLOAT), SaplingDataComponent::progress,
		SaplingDataComponent::new
	);
}