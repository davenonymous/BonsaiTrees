package com.davenonymous.bonsaitrees.setup;

import com.davenonymous.bonsaitrees.BonsaiTrees;
import com.davenonymous.bonsaitrees.datacomponents.*;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents {
	public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, BonsaiTrees.MODID);

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<SaplingDataComponent>> SAPLING_COMPONENT = DATA_COMPONENTS.registerComponentType(
		"sapling",
		builder -> builder
			.persistent(SaplingDataComponent.CODEC)
			.networkSynchronized(SaplingDataComponent.STREAM_CODEC)
	);

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<SoilDataComponent>> SOIL_COMPONENT = DATA_COMPONENTS.registerComponentType(
		"soil",
		builder -> builder
			.persistent(SoilDataComponent.CODEC)
			.networkSynchronized(SoilDataComponent.STREAM_CODEC)
	);

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<CamouflageDataComponent>> CAMOUFLAGE_COMPONENT = DATA_COMPONENTS.registerComponentType(
		"camouflage",
		builder -> builder
			.persistent(CamouflageDataComponent.CODEC)
			.networkSynchronized(CamouflageDataComponent.STREAM_CODEC)
	);

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<ToolDataComponent>> TOOL_COMPONENT = DATA_COMPONENTS.registerComponentType(
		"tool",
		builder -> builder
			.persistent(ToolDataComponent.CODEC)
			.networkSynchronized(ToolDataComponent.STREAM_CODEC)
	);

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<RedstoneModeDataComponent>> REDSTONEMODE_COMPONENT = DATA_COMPONENTS.registerComponentType(
		"redstone_mode",
		builder -> builder
			.persistent(RedstoneModeDataComponent.CODEC)
			.networkSynchronized(RedstoneModeDataComponent.STREAM_CODEC)
	);
}
