package com.davenonymous.bonsaitrees3.registry;

import com.davenonymous.bonsaitrees3.compat.jei.BonsaiTreesJEIPlugin;
import com.davenonymous.bonsaitrees3.setup.Registration;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RecipeEvents {
	public static final Logger LOGGER = LogManager.getLogger();

	@SubscribeEvent
	public static void onRecipesUpdated(RecipesUpdatedEvent event) {
		event.getRecipeManager().getAllRecipesFor(Registration.RECIPE_TYPE_SOIL).forEach(s -> {
			LOGGER.info("Loaded soil recipe: {}", s.getId());
		});
		event.getRecipeManager().getAllRecipesFor(Registration.RECIPE_TYPE_SAPLING).forEach(s -> {
			LOGGER.info("Loaded sapling recipe: {}", s.getId());
		});

		SoilCompatibility.INSTANCE.update(event.getRecipeManager().getRecipes());
		if(ModList.get().isLoaded("jei")) {
			BonsaiTreesJEIPlugin.saplings = Registration.RECIPE_HELPER_SAPLING.getRecipesList(event.getRecipeManager());
		}

	}

}
