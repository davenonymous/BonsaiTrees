package org.dave.bonsaitrees;

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
import org.dave.bonsaitrees.network.PackageHandler;
import org.dave.bonsaitrees.proxy.CommonProxy;
import org.dave.bonsaitrees.render.PotColorizer;
import org.dave.bonsaitrees.soils.BonsaiSoilRegistry;
import org.dave.bonsaitrees.soils.SoilCompatibility;
import org.dave.bonsaitrees.trees.TreeEvents;
import org.dave.bonsaitrees.trees.TreeShapeRegistry;
import org.dave.bonsaitrees.trees.TreeTypeRegistry;
import org.dave.bonsaitrees.utility.Logz;


@Mod(
        modid = BonsaiTrees.MODID,
        version = BonsaiTrees.VERSION,
        guiFactory = BonsaiTrees.GUI_FACTORY,
        acceptedMinecraftVersions = "[1.12,1.13)"
)
public class BonsaiTrees {
    public static final String MODID = "bonsaitrees";
    public static final String VERSION = "1.1.4";
    public static final String GUI_FACTORY = "org.dave.bonsaitrees.misc.ConfigGuiFactory";

    @Mod.Instance(BonsaiTrees.MODID)
    public static BonsaiTrees instance;

    @SidedProxy(clientSide = "org.dave.bonsaitrees.proxy.ClientProxy", serverSide = "org.dave.bonsaitrees.proxy.ServerProxy")
    public static CommonProxy proxy;

    public BonsaiSoilRegistry soilRegistry;
    public TreeTypeRegistry typeRegistry;
    public SoilCompatibility soilCompatibility;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ConfigurationHandler.init(event.getSuggestedConfigurationFile());

        IntegrationRegistry.loadBonsaiIntegrations(event.getAsmData());

        MinecraftForge.EVENT_BUS.register(ConfigurationHandler.class);
        MinecraftForge.EVENT_BUS.register(RenderTickCounter.class);
        MinecraftForge.EVENT_BUS.register(TreeEvents.class);
        MinecraftForge.EVENT_BUS.register(PotColorizer.class);

        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        PackageHandler.init();

        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        soilRegistry = new BonsaiSoilRegistry();
        typeRegistry = new TreeTypeRegistry();
        soilCompatibility = new SoilCompatibility();

        IntegrationRegistry.registerTreeIntegrations();
        Logz.info("Registered %d tree types", typeRegistry.getAllTypes().size());

        IntegrationRegistry.registerSoilIntegrations();
        Logz.info("Registered %d soil types", soilRegistry.getAllSoils().size());

        soilCompatibility.updateCompatibility(soilRegistry, typeRegistry);

        TreeShapeRegistry.init();
        typeRegistry.checkMissingShapes();

        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandBonsaiTrees());
    }
}
