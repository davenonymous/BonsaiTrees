package com.davenonymous.bonsaitrees3.setup;

import com.davenonymous.bonsaitrees3.network.Networking;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class ModSetup {
	public static void init(FMLCommonSetupEvent event) {
		Networking.registerMessages();
	}
}