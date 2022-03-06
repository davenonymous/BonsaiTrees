package com.davenonymous.bonsaitrees3.setup;

import com.davenonymous.bonsaitrees3.command.ModCommands;
import com.davenonymous.bonsaitrees3.registry.SoilCompatibility;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ForgeEventHandlers {

	@SubscribeEvent
	public void onRegisterCommands(RegisterCommandsEvent event) {
		ModCommands.register(event.getDispatcher());
	}

	@SubscribeEvent
	public void onServerStarting(ServerStartingEvent event) {
		SoilCompatibility.INSTANCE.update(event.getServer().getRecipeManager().getRecipes());
	}
}