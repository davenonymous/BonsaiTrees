package com.davenonymous.bonsaitrees.setup;

import com.davenonymous.bonsaitrees.BonsaiTrees;
import com.davenonymous.bonsaitrees.blocks.BonsaiPotBlock;
import com.davenonymous.bonsaitrees.blocks.BonsaiPotBlockEntity;
import com.davenonymous.bonsaitrees.blocks.BonsaiPotSmallBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(BonsaiTrees.MODID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, BonsaiTrees.MODID);
	public static final DeferredRegister<MapCodec<? extends Block>> BLOCK_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_TYPE, BonsaiTrees.MODID);

	public static final DeferredBlock<Block> BONSAI_POT = BLOCKS.register(
		"bonsaipot",
		() -> new BonsaiPotBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE))
	);

	public static final DeferredBlock<Block> BONSAI_POT_SMALL = BLOCKS.register(
		"bonsaipot_small",
		() -> new BonsaiPotSmallBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE))
	);

	public static final Supplier<BlockEntityType<BonsaiPotBlockEntity>> BONSAI_POT_ENTITY = BLOCK_ENTITIES.register(
		"bonsaipot",
		() -> BlockEntityType.Builder.of(BonsaiPotBlockEntity::new, BONSAI_POT.get(), BONSAI_POT_SMALL.get())
			.build(null)
	);

	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<BonsaiPotBlock>> BONSAI_POT_TYPE = BLOCK_TYPES.register(
		"bonsaipot",
		() -> BlockBehaviour.simpleCodec(BonsaiPotBlock::new)
	);

	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<BonsaiPotSmallBlock>> BONSAI_POT_SMALL_TYPE = BLOCK_TYPES.register(
		"bonsaipot_small",
		() -> BlockBehaviour.simpleCodec(BonsaiPotSmallBlock::new)
	);

}
