package com.davenonymous.bonsaitrees.setup;

import com.davenonymous.bonsaitrees.BonsaiTrees;
import com.davenonymous.bonsaitrees.blocks.BonsaiPotBlockItem;
import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(BonsaiTrees.MODID);

	// Block Items
	public static final DeferredItem<BlockItem> BONSAI_POT_ITEM = ITEMS
		.register("bonsaipot", () -> new BonsaiPotBlockItem(ModBlocks.BONSAI_POT.get(), new BlockItem.Properties()));

	public static final DeferredItem<BlockItem> BONSAI_POT_SMALL_ITEM = ITEMS
		.register("bonsaipot_small", () -> new BonsaiPotBlockItem(ModBlocks.BONSAI_POT_SMALL.get(), new BlockItem.Properties()));
}
