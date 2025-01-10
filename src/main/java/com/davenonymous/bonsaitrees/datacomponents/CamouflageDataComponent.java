package com.davenonymous.bonsaitrees.datacomponents;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record CamouflageDataComponent(ResourceLocation camouflage) {
	public static final Codec<CamouflageDataComponent> CODEC = RecordCodecBuilder.create(instance ->
		instance.group(
			ResourceLocation.CODEC.fieldOf("camouflage").forGetter(CamouflageDataComponent::camouflage)
		).apply(instance, CamouflageDataComponent::new)
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, CamouflageDataComponent> STREAM_CODEC = StreamCodec.composite(
		ResourceLocation.STREAM_CODEC, CamouflageDataComponent::camouflage,
		CamouflageDataComponent::new
	);
}