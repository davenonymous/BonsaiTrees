package com.davenonymous.bonsaitrees.datacomponents;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record ToolDataComponent(ItemStack tool) {
	public static final Codec<ToolDataComponent> CODEC = RecordCodecBuilder.create(instance ->
		instance.group(
			ItemStack.CODEC.fieldOf("tool").forGetter(ToolDataComponent::tool)
		).apply(instance, ToolDataComponent::new)
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, ToolDataComponent> STREAM_CODEC = StreamCodec.composite(
		ItemStack.STREAM_CODEC, ToolDataComponent::tool,
		ToolDataComponent::new
	);
}
