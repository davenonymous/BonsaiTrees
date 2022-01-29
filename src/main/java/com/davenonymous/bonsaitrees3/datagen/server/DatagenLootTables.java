package com.davenonymous.bonsaitrees3.datagen.server;

import com.davenonymous.libnonymous.base.BaseLootTableProvider;
import com.davenonymous.bonsaitrees3.setup.Registration;
import net.minecraft.data.DataGenerator;

public class DatagenLootTables extends BaseLootTableProvider {

	public DatagenLootTables(DataGenerator dataGeneratorIn) {
		super(dataGeneratorIn, "Bonsai Trees");
	}

	@Override
	protected void addTables() {
		this.addTable(Registration.BONSAI_POT.get(), dropSelf(Registration.BONSAI_POT.get()));
	}
}