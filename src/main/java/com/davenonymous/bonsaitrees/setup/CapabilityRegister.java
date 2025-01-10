package com.davenonymous.bonsaitrees.setup;


import com.davenonymous.bonsaitrees.BonsaiTrees;
import com.davenonymous.bonsaitrees.blocks.BonsaiPotBlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = BonsaiTrees.MODID, bus = EventBusSubscriber.Bus.MOD)
public class CapabilityRegister {

	@SubscribeEvent
	public static void onCapabilityRegister(RegisterCapabilitiesEvent event) {

		event.registerBlock(
			Capabilities.ItemHandler.BLOCK,
			BonsaiPotBlockEntity::getCapability,
			ModBlocks.BONSAI_POT.get(),
			ModBlocks.BONSAI_POT_SMALL.get()
		);
	}

}
