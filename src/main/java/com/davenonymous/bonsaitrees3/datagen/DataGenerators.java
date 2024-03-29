package com.davenonymous.bonsaitrees3.datagen;

import com.davenonymous.bonsaitrees3.BonsaiTrees3;
import com.davenonymous.bonsaitrees3.datagen.client.DatagenBlockStates;
import com.davenonymous.bonsaitrees3.datagen.client.DatagenItemModels;
import com.davenonymous.bonsaitrees3.datagen.client.DatagenTranslations;
import com.davenonymous.bonsaitrees3.datagen.server.*;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = BonsaiTrees3.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
	private static void generateServerData(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		var blockTagsProvider = new DatagenBlockTags(generator, existingFileHelper);
		generator.addProvider(blockTagsProvider);
		generator.addProvider(new DatagenItemTags(generator, blockTagsProvider, existingFileHelper));
		generator.addProvider(new DatagenRecipes(generator));
		generator.addProvider(new DatagenLootTables(generator));

		generator.addProvider(new DatagenSoils(generator));
		generator.addProvider(new DatagenSaplings(generator));
	}

	private static void generateClientData(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		generator.addProvider(new DatagenBlockStates(generator, existingFileHelper));
		generator.addProvider(new DatagenItemModels(generator, existingFileHelper));
		generator.addProvider(new DatagenTranslations(generator, "en_us"));
	}

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		var generator = event.getGenerator();
		if(event.includeServer()) {
			generateServerData(generator, event.getExistingFileHelper());
		}

		if(event.includeClient()) {
			generateClientData(generator, event.getExistingFileHelper());
		}
	}
}
