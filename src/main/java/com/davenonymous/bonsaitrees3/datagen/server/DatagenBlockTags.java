package com.davenonymous.bonsaitrees3.datagen.server;

import com.davenonymous.bonsaitrees3.BonsaiTrees3;
import com.davenonymous.bonsaitrees3.setup.Registration;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class DatagenBlockTags extends BlockTagsProvider {
	public DatagenBlockTags(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, BonsaiTrees3.MODID, existingFileHelper);
	}

	@Override
	protected void addTags() {
		m_206424_(BlockTags.MINEABLE_WITH_PICKAXE).add(Registration.BONSAI_POT.get());
		m_206424_(BlockTags.NEEDS_STONE_TOOL).add(Registration.BONSAI_POT.get());
	}

	@Override
	public String getName() {
		return "Bonsai Trees BlockTags";
	}
}