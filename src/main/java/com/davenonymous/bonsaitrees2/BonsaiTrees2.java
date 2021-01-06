package com.davenonymous.bonsaitrees2;

import com.davenonymous.bonsaitrees2.block.ModObjects;
import com.davenonymous.bonsaitrees2.compat.top.TOPPlugin;
import com.davenonymous.bonsaitrees2.config.Config;
import com.davenonymous.bonsaitrees2.registry.SoilCompatibility;
import com.davenonymous.bonsaitrees2.setup.ModSetup;
import com.davenonymous.bonsaitrees2.setup.ProxyClient;
import com.davenonymous.bonsaitrees2.setup.ProxyServer;
import com.davenonymous.bonsaitrees2.setup.Registration;
import com.davenonymous.bonsaitrees2.util.Logz;
import com.davenonymous.libnonymous.setup.IProxy;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(BonsaiTrees2.MODID)
public class BonsaiTrees2 {
    public static final String MODID = "bonsaitrees2";

    public static IProxy proxy = DistExecutor.unsafeRunForDist(() -> ProxyClient::new, () -> ProxyServer::new);
    public static ModSetup setup = new ModSetup();

    public BonsaiTrees2() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);

        Registration.init();

        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        setup.init();
        proxy.init();
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        if(ModList.get().isLoaded("theoneprobe")) {
            InterModComms.sendTo("theoneprobe", "getTheOneProbe", TOPPlugin::new);
        }
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        SoilCompatibility.INSTANCE.update(event.getServer().getRecipeManager().getRecipes());
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void startServer(FMLServerAboutToStartEvent event) {
        IReloadableResourceManager manager = event.getServer().getResourceManager();
        manager.addReloadListener((IResourceManagerReloadListener) resourceManager -> {
            RecipeManager recipeManager = event.getServer().getRecipeManager();
            if(!ModObjects.soilRecipeHelper.hasRecipes(recipeManager)) {
                Logz.warn("Warning. No soils loaded! This mod will not work properly!");
            }
            if(!ModObjects.saplingRecipeHelper.hasRecipes(recipeManager)) {
                Logz.warn("Warning. No bonsai saplings loaded! This mod will not work properly!");
            }

            Logz.info("Loaded {} bonsai types and {} soil types",
                    ModObjects.saplingRecipeHelper.getRecipeCount(recipeManager),
                    ModObjects.soilRecipeHelper.getRecipeCount(recipeManager)
            );
        });
    }
}
