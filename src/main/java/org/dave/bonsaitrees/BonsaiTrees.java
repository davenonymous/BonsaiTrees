package org.dave.bonsaitrees;

import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.dave.bonsaitrees.command.CommandBonsaiTrees;
import org.dave.bonsaitrees.integration.IntegrationRegistry;
import org.dave.bonsaitrees.misc.ConfigurationHandler;
import org.dave.bonsaitrees.misc.RenderTickCounter;
import org.dave.bonsaitrees.proxy.CommonProxy;
import org.dave.bonsaitrees.trees.TreeEvents;
import org.dave.bonsaitrees.trees.TreeShapeRegistry;
import org.dave.bonsaitrees.trees.TreeTypeRegistry;


@Mod(modid = BonsaiTrees.MODID, version = BonsaiTrees.VERSION, acceptedMinecraftVersions = "[1.12,1.13)")
public class BonsaiTrees {
    public static final String MODID = "bonsaitrees";
    public static final String VERSION = "1.0.4";

    @Mod.Instance(BonsaiTrees.MODID)
    public static BonsaiTrees instance;

    @SidedProxy(clientSide = "org.dave.bonsaitrees.proxy.ClientProxy", serverSide = "org.dave.bonsaitrees.proxy.ServerProxy")
    public static CommonProxy proxy;

    public TreeTypeRegistry typeRegistry;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ConfigurationHandler.init(event.getSuggestedConfigurationFile());

        IntegrationRegistry.loadBonsaiIntegrations(event.getAsmData());

        MinecraftForge.EVENT_BUS.register(ConfigurationHandler.class);
        MinecraftForge.EVENT_BUS.register(RenderTickCounter.class);
        MinecraftForge.EVENT_BUS.register(TreeEvents.class);

        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        typeRegistry = new TreeTypeRegistry();
        IntegrationRegistry.registerBonsaiIntegrations();

        TreeShapeRegistry.init();
        typeRegistry.checkMissingShapes();

        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandBonsaiTrees());
    }
}
