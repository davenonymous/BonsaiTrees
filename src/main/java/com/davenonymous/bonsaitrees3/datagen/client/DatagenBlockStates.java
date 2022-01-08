package com.davenonymous.bonsaitrees3.datagen.client;

import com.davenonymous.bonsaitrees3.BonsaiTrees3;
import com.davenonymous.bonsaitrees3.client.BonsaiPotModelLoader;
import com.davenonymous.bonsaitrees3.setup.Registration;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.CustomLoaderBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public class DatagenBlockStates extends BlockStateProvider {

	public DatagenBlockStates(DataGenerator gen, ExistingFileHelper exFileHelper) {
		super(gen, BonsaiTrees3.MODID, exFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		registerBonsaiPot();
	}

	private void registerBonsaiPot() {
		BlockModelBuilder frame = models().getBuilder("block/bonsaipot").parent(models().getExistingFile(mcLoc("cube"))).customLoader((blockModelBuilder, helper) -> new CustomLoaderBuilder<BlockModelBuilder>(BonsaiPotModelLoader.BONSAIPOT_LOADER, blockModelBuilder, helper) {
		}).end();

		simpleBlock(Registration.BONSAI_POT.get(), frame);
	}
}
