package com.davenonymous.bonsaitrees.setup.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

public record SoilInfo(List<ResourceLocation> soilType, Optional<Integer> extraRolls) {
	public static final Codec<SoilInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		ResourceLocation.CODEC.listOf().fieldOf("soilTypes").forGetter(SoilInfo::soilType),
		Codec.INT.optionalFieldOf("extraRolls").forGetter(SoilInfo::extraRolls)
	).apply(instance, SoilInfo::new));

	public static SoilInfo of(List<ResourceLocation> soilTypes) {
		return new SoilInfo(soilTypes, Optional.empty());
	}

	public static SoilInfo of(ResourceLocation soilType) {
		return new SoilInfo(List.of(soilType), Optional.empty());
	}


	public static SoilInfo of(ResourceLocation soilType, int extraRolls) {
		return new SoilInfo(List.of(soilType), Optional.of(extraRolls));
	}

	public static SoilInfo fromSoilInfoWithTexture(SoilInfoWithTexture soilInfoWithTexture) {
		return new SoilInfo(soilInfoWithTexture.soilType(), soilInfoWithTexture.extraRolls());
	}
}
