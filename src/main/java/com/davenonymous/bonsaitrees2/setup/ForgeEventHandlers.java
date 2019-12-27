package com.davenonymous.bonsaitrees2.setup;

import com.davenonymous.bonsaitrees2.command.ModCommands;
import com.davenonymous.bonsaitrees2.compat.jei.BonsaiTrees2JEIPlugin;
import com.davenonymous.bonsaitrees2.registry.RecipeTypes;
import com.davenonymous.bonsaitrees2.registry.SoilCompatibility;
import com.davenonymous.bonsaitrees2.registry.sapling.SaplingInfo;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

import java.util.stream.Collectors;

public class ForgeEventHandlers {
    @SubscribeEvent
    public void serverLoad(FMLServerStartingEvent event) {
        ModCommands.register(event.getCommandDispatcher());
    }



    @SubscribeEvent
    public void recipesUpdated(RecipesUpdatedEvent event) {
        BonsaiTrees2JEIPlugin.saplings = event.getRecipeManager().getRecipes().stream().filter(r -> r.getType() == RecipeTypes.saplingRecipeType).map(r -> (SaplingInfo)r).collect(Collectors.toList());

        SoilCompatibility.INSTANCE.update(event.getRecipeManager().getRecipes());
    }
}
