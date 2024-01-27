package com.davenonymous.bonsaitrees3.datagen.client;

import com.davenonymous.bonsaitrees3.BonsaiTrees3;
import com.davenonymous.bonsaitrees3.setup.Registration;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class DatagenItemModels extends ItemModelProvider {
	public DatagenItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, BonsaiTrees3.MODID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		withExistingParent(ForgeRegistries.ITEMS.getKey(Registration.BONSAI_POT_ITEM.get()).getPath(), modLoc("block/bonsaipot"));
	}
}
