package com.davenonymous.bonsaitrees.setup.data;

import com.davenonymous.bonsaitrees.BonsaiTrees;
import com.davenonymous.bonsaitrees.setup.cache.SoilCache;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

public record SoilType(ResourceLocation id, ItemStack defaultItem, String translationKey) {

	public SoilType(String name, ItemStack defaultItem, String translationKey) {
		this(BonsaiTrees.resource(name), defaultItem, translationKey);
	}

	private static final MapCodec<SoilType> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
		return instance.group(
			ResourceLocation.CODEC.fieldOf("id").forGetter(SoilType::id),
			ItemStack.CODEC.fieldOf("defaultItem").forGetter(SoilType::defaultItem),
			Codec.STRING.fieldOf("translationKey").forGetter(SoilType::translationKey)
		).apply(instance, SoilType::new);
	});

	public static Codec<SoilType> codec() {
		return CODEC.codec();
	}

	public boolean hasSoils() {
		if(!SoilCache.SOIL_BY_TYPE.containsKey(this.id())) {
			return false;
		}

		Map<Item, SoilInfo> mySoils = SoilCache.SOIL_BY_TYPE.get(this.id());
		return !mySoils.isEmpty();
	}

	public boolean hasBonsais() {
		return SoilCache.BONSAIS_BY_SOIL.containsKey(this) && !SoilCache.BONSAIS_BY_SOIL.get(this).isEmpty();
	}
}
