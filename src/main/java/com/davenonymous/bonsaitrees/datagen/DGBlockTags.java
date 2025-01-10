package com.davenonymous.bonsaitrees.datagen;

import com.davenonymous.bonsaitrees.BonsaiTrees;
import com.davenonymous.bonsaitrees.setup.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class DGBlockTags extends BlockTagsProvider {
	public DGBlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, BonsaiTrees.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(net.minecraft.tags.BlockTags.MINEABLE_WITH_AXE).add(
			ModBlocks.BONSAI_POT.get(),
			ModBlocks.BONSAI_POT_SMALL.get()
		);
	}
}
