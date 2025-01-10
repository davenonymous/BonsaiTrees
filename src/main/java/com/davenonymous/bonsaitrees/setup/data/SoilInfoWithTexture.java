package com.davenonymous.bonsaitrees.setup.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public record SoilInfoWithTexture(ResourceLocation soilType, Optional<Integer> extraRolls, ResourceLocation texture) {
	public static final Codec<SoilInfoWithTexture> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		ResourceLocation.CODEC.fieldOf("soilType").forGetter(SoilInfoWithTexture::soilType),
		Codec.INT.optionalFieldOf("extraRolls").forGetter(SoilInfoWithTexture::extraRolls),
		ResourceLocation.CODEC.fieldOf("texture").forGetter(SoilInfoWithTexture::texture)
	).apply(instance, SoilInfoWithTexture::new));

	public static SoilInfoWithTexture of(ResourceLocation soilType, int extraRolls, ResourceLocation texture) {
		return new SoilInfoWithTexture(soilType, Optional.of(extraRolls), texture);
	}

	public static SoilInfoWithTexture of(ResourceLocation soilType, ResourceLocation texture) {
		return new SoilInfoWithTexture(soilType, Optional.empty(), texture);
	}

}
