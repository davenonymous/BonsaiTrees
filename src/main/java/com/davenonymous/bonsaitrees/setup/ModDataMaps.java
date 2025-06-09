package com.davenonymous.bonsaitrees.setup;

import com.davenonymous.bonsaitrees.BonsaiTrees;
import com.davenonymous.bonsaitrees.setup.data.*;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.datamaps.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = BonsaiTrees.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModDataMaps {
	public static final DataMapType<Item, BonsaiInfo> BONSAI = DataMapType
		.builder(
			BonsaiTrees.resource("bonsai"),
			Registries.ITEM,
			BonsaiInfo.CODEC
		)
		.synced(BonsaiInfo.CODEC, false)
		.build();

	public static final AdvancedDataMapType<Block, SoilInfo, BlockSoilRemover> BLOCK_SOIL = AdvancedDataMapType
		.builder(
			BonsaiTrees.resource("soil"),
			Registries.BLOCK,
			SoilInfo.CODEC
		)
		.remover(BlockSoilRemover.CODEC)
		.merger(new BlockSoilMerger())
		.synced(SoilInfo.CODEC, false)
		.build();

	public static final AdvancedDataMapType<Fluid, SoilInfo, FluidSoilRemover> FLUID_SOIL = AdvancedDataMapType
		.builder(
			BonsaiTrees.resource("soil"),
			Registries.FLUID,
			SoilInfo.CODEC
		)
		.remover(FluidSoilRemover.CODEC)
		.synced(SoilInfo.CODEC, false)
		.build();

	public static final AdvancedDataMapType<Item, SoilInfoWithTexture, ItemSoilRemover> ITEM_SOIL = AdvancedDataMapType
		.builder(
			BonsaiTrees.resource("soil"),
			Registries.ITEM,
			SoilInfoWithTexture.CODEC
		)
		.remover(ItemSoilRemover.CODEC)
		.synced(SoilInfoWithTexture.CODEC, false)
		.build();


	@SubscribeEvent
	private static void registerDataMapTypes(RegisterDataMapTypesEvent event) {
		event.register(BONSAI);
		event.register(BLOCK_SOIL);
		event.register(FLUID_SOIL);
		event.register(ITEM_SOIL);
	}

	public static class BlockSoilMerger implements DataMapValueMerger<Block, SoilInfo> {

		@Override
		public SoilInfo merge(Registry<Block> registry, Either<TagKey<Block>, ResourceKey<Block>> first, SoilInfo firstValue, Either<TagKey<Block>, ResourceKey<Block>> second, SoilInfo secondValue) {
			List<ResourceLocation> mergedSoilTypes = new ArrayList<>(firstValue.soilType());
			mergedSoilTypes.addAll(secondValue.soilType());
			int extraRolls = Math.max(firstValue.extraRolls().orElse(0), secondValue.extraRolls().orElse(0));
			return new SoilInfo(mergedSoilTypes, extraRolls > 0 ? Optional.of(extraRolls) : Optional.empty());
		}
	}

	public record BlockSoilRemover(ResourceLocation soilId) implements DataMapValueRemover<Block, SoilInfo> {
		public static final Codec<BlockSoilRemover> CODEC = ResourceLocation.CODEC.xmap(BlockSoilRemover::new, BlockSoilRemover::soilId);

		@Override
		public Optional<SoilInfo> remove(SoilInfo value, Registry<Block> registry, Either<TagKey<Block>, ResourceKey<Block>> source, Block object) {
			final List<ResourceLocation> newList = value.soilType().stream()
				.filter(id -> !id.equals(soilId))
				.toList();
			return Optional.of(new SoilInfo(newList, value.extraRolls()));
		}
	}

	public record ItemSoilRemover(ResourceLocation soilId) implements DataMapValueRemover<Item, SoilInfoWithTexture> {
		public static final Codec<ItemSoilRemover> CODEC = ResourceLocation.CODEC.xmap(ItemSoilRemover::new, ItemSoilRemover::soilId);

		@Override
		public Optional<SoilInfoWithTexture> remove(SoilInfoWithTexture value, Registry<Item> registry, Either<TagKey<Item>, ResourceKey<Item>> source, Item object) {
			final List<ResourceLocation> newList = value.soilType().stream()
				.filter(id -> !id.equals(soilId))
				.toList();
			return Optional.of(new SoilInfoWithTexture(newList, value.extraRolls(), value.texture()));
		}
	}

	public record FluidSoilRemover(ResourceLocation soilId) implements DataMapValueRemover<Fluid, SoilInfo> {
		public static final Codec<FluidSoilRemover> CODEC = ResourceLocation.CODEC.xmap(FluidSoilRemover::new, FluidSoilRemover::soilId);

		@Override
		public Optional<SoilInfo> remove(SoilInfo value, Registry<Fluid> registry, Either<TagKey<Fluid>, ResourceKey<Fluid>> source, Fluid object) {
			final List<ResourceLocation> newList = value.soilType().stream()
				.filter(id -> !id.equals(soilId))
				.toList();
			return Optional.of(new SoilInfo(newList, value.extraRolls()));
		}
	}



}
