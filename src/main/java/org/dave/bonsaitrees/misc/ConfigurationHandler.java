package org.dave.bonsaitrees.misc;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.dave.bonsaitrees.BonsaiTrees;
import org.dave.bonsaitrees.api.BonsaiDropChances;
import org.dave.bonsaitrees.utility.Logz;

import java.io.File;

public class ConfigurationHandler {
    public static Configuration configuration;

    public static File configDir;
    public static File treeTypesDir;
    public static File treeShapesDir;
    public static File soilsDir;

    public static final String CATEGORY_DROPS = "drops";
    public static final String CATEGORY_CLIENT = "client";
    public static final String CATEGORY_INTEGRATION = "integration";
    public static final String CATEGORY_GENERAL = "general";

    public static void init(File configFile) {
        if (configuration != null) {
            return;
        }

        configDir = new File(configFile.getParentFile(), BonsaiTrees.MODID);
        if (!configDir.exists()) {
            configDir.mkdirs();
        }

        treeTypesDir = new File(configDir, "types.d");
        if (!treeTypesDir.exists()) {
            treeTypesDir.mkdirs();
        }

        treeShapesDir = new File(configDir, "shapes.d");
        if (!treeShapesDir.exists()) {
            treeShapesDir.mkdirs();
        }

        soilsDir = new File(configDir, "soils.d");
        if (!soilsDir.exists()) {
            soilsDir.mkdirs();
        }

        // TODO: Add a command to extract the default shapes and json types
        /*
        int count = JarExtract.copy("assets/bonsaitrees/config/types.d", treeTypesDir);
        Logz.info("Extracted %d tree type configs", count);

        int count2 = JarExtract.copy("assets/bonsaitrees/config/shapes.d", treeShapesDir);
        Logz.info("Extracted %d tree shape configs", count2);
        */

        configuration = new Configuration(new File(configDir, "settings.cfg"), null);
        loadConfiguration();
    }

    private static void loadConfiguration() {
        Logz.info("Loading configuration");

        BonsaiDropChances.stickChance = configuration.getFloat(
                "stickChance", CATEGORY_DROPS, 0.2f, 0.0f, 1.0f, "Default chance for a stick to drop"
        );

        BonsaiDropChances.logChance = configuration.getFloat(
                "logChance", CATEGORY_DROPS, 0.75f, 0.0f, 1.0f, "Default chance for a wood log to drop"
        );

        BonsaiDropChances.leafChance = configuration.getFloat(
                "leafChance", CATEGORY_DROPS, 0.1f, 0.0f, 1.0f, "Default chance for a leaf to drop"
        );

        BonsaiDropChances.saplingChance = configuration.getFloat(
                "saplingChance", CATEGORY_DROPS, 0.05f, 0.0f, 1.0f, "Default chance for a sapling to drop"
        );

        BonsaiDropChances.fruitChance = configuration.getFloat(
                "fruitChance", CATEGORY_DROPS, 0.2f, 0.0f, 1.0f, "Default chance for a fruit to drop"
        );


        // TODO: Test what happens when drop amounts are set to 0 and then make it work ;)
        BonsaiDropChances.stickAmount = configuration.getInt(
                "stickAmount", CATEGORY_DROPS, 3, 0, 64, "How many sticks to drop by default"
        );

        BonsaiDropChances.logAmount = configuration.getInt(
                "logAmount", CATEGORY_DROPS, 1, 0, 64, "How many wood logs to drop by default"
        );

        BonsaiDropChances.leafAmount = configuration.getInt(
                "leafAmount", CATEGORY_DROPS, 1, 0, 64, "How many leaves to drop by default"
        );

        BonsaiDropChances.saplingAmount = configuration.getInt(
                "saplingAmount", CATEGORY_DROPS, 1, 0, 64, "How many saplings to drop by default"
        );

        BonsaiDropChances.fruitAmount = configuration.getInt(
                "fruitAmount", CATEGORY_DROPS, 2, 0, 64, "How many fruits to drop by default"
        );


        ClientSettings.maxTreeScale = configuration.getFloat(
                "maxTreeScale", CATEGORY_CLIENT, 0.9f, 0.5f, 1.0f, "Maximum width/depth of a block to grow to"
        );

        ClientSettings.showChanceInJEI = configuration.getBoolean(
                "showChanceInJEI", CATEGORY_CLIENT, true, "Whether to show the drop chances in JEI"
        );

        IntegrationSettings.disabledIntegrations = configuration.getStringList(
                "disabledIntegrations", CATEGORY_INTEGRATION, new String[] {}, "Integrations to disable (by classname, e.g. org.dave.bonsaitrees.integration.mods.PamsHarvestcraft)"
        );

        IntegrationSettings.disabledTreeTypes = configuration.getStringList(
                "disabledTreeTypes", CATEGORY_INTEGRATION, new String[] {}, "Tree types to disable (e.g. forestry:hillCherry)"
        );

        IntegrationSettings.disabledSoils = configuration.getStringList(
                "disabledSoils", CATEGORY_INTEGRATION, new String[] {}, "Bonsai Soils to disable (e.g. minecraft:grass)"
        );

        IntegrationSettings.loadShapesOfUnloadedTrees = configuration.getBoolean(
                "loadShapesOfUnloadedTrees", CATEGORY_INTEGRATION, false, "Can be enabled for development purposes mostly."
        );

        GeneralSettings.disableHoppingBonsaiPot = configuration.getBoolean(
                "disableHoppingBonsaiPot", CATEGORY_GENERAL, false, "Whether to disable the Hopping Bonsai Pot and make it behave like a normal Bonsai Pot"
        );

        GeneralSettings.baseGrowTicks = configuration.getInt(
                "baseGrowTicks", CATEGORY_GENERAL, 600, 1, Integer.MAX_VALUE, "How many ticks trees need to fully grow. Some tree types modify this value"
        );

        GeneralSettings.noDyeCost = configuration.getBoolean(
                "noDyeCost", CATEGORY_GENERAL, true, "If set to false, then dye is being used up when painting bonsai pots"
        );

        if(configuration.hasChanged()) {
            configuration.save();
        }
    }

    @SubscribeEvent
    public static void onConfigurationChanged(ConfigChangedEvent event) {
        if(!event.getModID().equalsIgnoreCase(BonsaiTrees.MODID)) {
            return;
        }

        loadConfiguration();
    }

    public static class ClientSettings {
        public static float maxTreeScale = 0.9f;
        public static boolean showChanceInJEI = true;
    }

    public static class IntegrationSettings {
        public static String[] disabledIntegrations = new String[] {};
        public static String[] disabledTreeTypes = new String[] {};
        public static String[] disabledSoils = new String[] {};
        public static boolean loadShapesOfUnloadedTrees = false;
    }

    public static class GeneralSettings {
        public static boolean disableHoppingBonsaiPot = false;
        public static int baseGrowTicks = 600;
        public static boolean noDyeCost = true;
    }
}
