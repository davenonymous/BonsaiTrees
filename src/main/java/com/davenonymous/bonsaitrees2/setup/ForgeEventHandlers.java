package com.davenonymous.bonsaitrees2.setup;

import com.davenonymous.bonsaitrees2.command.ModCommands;
import com.davenonymous.bonsaitrees2.compat.jei.BonsaiTrees2JEIPlugin;
import com.davenonymous.bonsaitrees2.registry.RecipeTypes;
import com.davenonymous.bonsaitrees2.registry.SoilCompatibility;
import com.davenonymous.bonsaitrees2.registry.sapling.SaplingInfo;
import com.davenonymous.bonsaitrees2.render.TreeModels;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

import java.util.stream.Collectors;

public class ForgeEventHandlers {
    @SubscribeEvent
    public void serverLoad(FMLServerStartingEvent event) {
        ModCommands.register(event.getCommandDispatcher());
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void startServer(FMLServerAboutToStartEvent event) {
        IReloadableResourceManager manager = event.getServer().getResourceManager();
        manager.addReloadListener((IResourceManagerReloadListener) resourceManager -> {
            SoilCompatibility.INSTANCE.update(event.getServer().getRecipeManager().getRecipes());
        });
    }

    @SubscribeEvent
    public void recipesUpdated(RecipesUpdatedEvent event) {
        if(ModList.get().isLoaded("jei")) {
            BonsaiTrees2JEIPlugin.saplings = event.getRecipeManager().getRecipes().stream().filter(r -> r.getType() == RecipeTypes.saplingRecipeType).map(r -> (SaplingInfo) r).collect(Collectors.toList());
        }

        SoilCompatibility.INSTANCE.update(event.getRecipeManager().getRecipes());
        TreeModels.init();
    }
}
