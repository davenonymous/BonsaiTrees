package com.davenonymous.bonsaitrees.datagen;

import com.davenonymous.bonsaitrees.BonsaiTrees;
import com.davenonymous.bonsaitrees.setup.ModBlocks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class DGBlockStates extends BlockStateProvider {
	private final ExistingFileHelper exFileHelper;

	public DGBlockStates(PackOutput output, ExistingFileHelper exFileHelper) {
		super(output, BonsaiTrees.MODID, exFileHelper);
		this.exFileHelper = exFileHelper;
	}

	@Override
	protected void registerStatesAndModels() {
		var bonsaiPotModel = new ModelFile.ExistingModelFile(BonsaiTrees.resource("block/bonsaipot_bricks"), this.exFileHelper);
		simpleBlock(ModBlocks.BONSAI_POT.get(), bonsaiPotModel);

		var bonsaiPotSmallModel = new ModelFile.ExistingModelFile(BonsaiTrees.resource("block/bonsaipot_small"), this.exFileHelper);
		simpleBlock(ModBlocks.BONSAI_POT_SMALL.get(), bonsaiPotSmallModel);
	}
}
