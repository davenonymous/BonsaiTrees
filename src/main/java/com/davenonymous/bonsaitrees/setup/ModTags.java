package com.davenonymous.bonsaitrees.setup;

import com.davenonymous.bonsaitrees.BonsaiTrees;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {

	public static final TagKey<Block> DIRTS = createBlockTag("dirts", true);


	private static TagKey<Block> createBlockTag(String name) {
		return createBlockTag(name, false);
	}

	private static TagKey<Block> createBlockTag(String name, boolean common) {
		return TagKey.create(
			Registries.BLOCK,
			common ? ResourceLocation.fromNamespaceAndPath("c", name) : BonsaiTrees.resource(name)
		);
	}

	private static TagKey<Item> createItemTag(String name) {
		return createItemTag(name, false);
	}

	private static TagKey<Item> createItemTag(String name, boolean common) {
		return TagKey.create(
			Registries.ITEM,
			common ? ResourceLocation.fromNamespaceAndPath("c", name) : BonsaiTrees.resource(name)
		);
	}
}