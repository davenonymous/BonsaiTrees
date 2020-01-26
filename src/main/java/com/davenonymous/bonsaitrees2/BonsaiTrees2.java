package com.davenonymous.bonsaitrees2;

//import com.davenonymous.bonsaitrees2.compat.top.TOPPlugin;
import com.davenonymous.bonsaitrees2.config.Config;
import com.davenonymous.bonsaitrees2.registry.SoilCompatibility;
import com.davenonymous.bonsaitrees2.setup.ModSetup;
import com.davenonymous.bonsaitrees2.setup.ProxyClient;
import com.davenonymous.bonsaitrees2.setup.ProxyServer;
import com.davenonymous.libnonymous.setup.IProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(BonsaiTrees2.MODID)
public class BonsaiTrees2 {
    public static final String MODID = "bonsaitrees2";

    public static IProxy proxy = DistExecutor.runForDist(() -> () -> new ProxyClient(), () -> () -> new ProxyServer());
    public static ModSetup setup = new ModSetup();

    public BonsaiTrees2() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);

        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        Config.loadConfig(Config.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve("bonsaitrees2-client.toml"));
        Config.loadConfig(Config.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve("bonsaitrees2-common.toml"));

    }

    private void setup(final FMLCommonSetupEvent event) {
        setup.init();
        proxy.init();
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        if(ModList.get().isLoaded("theoneprobe")) {
            //InterModComms.sendTo("theoneprobe", "getTheOneProbe", TOPPlugin::new);
        }
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        SoilCompatibility.INSTANCE.update(event.getServer().getRecipeManager().getRecipes());
    }
}
