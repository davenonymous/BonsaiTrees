package org.dave.bonsaitrees.misc;

import net.minecraftforge.common.config.Configuration;
import org.dave.bonsaitrees.BonsaiTrees;
import org.dave.bonsaitrees.utility.JarExtract;
import org.dave.bonsaitrees.utility.Logz;

import java.io.File;

public class ConfigurationHandler {
    public static Configuration configuration;

    public static File configDir;
    public static File treeTypesDir;
    public static File treeShapesDir;

    public static void init(File configFile) {
        if (configuration != null) {
            return;
        }

        configDir = new File(configFile.getParentFile(), BonsaiTrees.MODID);
        if (!configDir.exists()) {
            configDir.mkdirs();
        }

        treeTypesDir = new File(configDir, "types.d");
        //if (!treeTypesDir.exists()) {
            //treeTypesDir.mkdirs();

            int count = JarExtract.copy("assets/bonsaitrees/config/types.d", treeTypesDir);
            Logz.info("Extracted %d tree type configs", count);
        //}

        treeShapesDir = new File(configDir, "shapes.d");
        //if (!treeShapesDir.exists()) {
            //treeShapesDir.mkdirs();

            int count2 = JarExtract.copy("assets/bonsaitrees/config/shapes.d", treeShapesDir);
            Logz.info("Extracted %d tree shape configs", count2);
        //}

        configuration = new Configuration(new File(configDir, "settings.cfg"), null);
    }
}
