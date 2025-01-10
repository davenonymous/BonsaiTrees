package com.davenonymous.bonsaitrees.datacomponents;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record SoilDataComponent(ItemStack soil) {
	public static final Codec<SoilDataComponent> CODEC = RecordCodecBuilder.create(instance ->
		instance.group(
			ItemStack.CODEC.fieldOf("soil").forGetter(SoilDataComponent::soil)
		).apply(instance, SoilDataComponent::new)
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, SoilDataComponent> STREAM_CODEC = StreamCodec.composite(
		ItemStack.STREAM_CODEC, SoilDataComponent::soil,
		SoilDataComponent::new
	);
}