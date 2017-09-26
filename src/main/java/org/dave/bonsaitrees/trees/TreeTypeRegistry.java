package org.dave.bonsaitrees.trees;

import net.minecraft.item.ItemStack;
import org.dave.bonsaitrees.base.BaseTreeType;
import org.dave.bonsaitrees.misc.ConfigurationHandler;
import org.dave.bonsaitrees.utility.Logz;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TreeTypeRegistry {
    private static Map<String, BaseTreeType> treeTypes;

    public static void init() {
        reload();
    }

    public static void checkMissingShapes() {
        for(BaseTreeType type : getAllTypes()) {
            int shapes = TreeShapeRegistry.getShapeCountForType(type);
            if(shapes == 0) {
                Logz.warn("Tree type '%s' has no shapes configured", type.typeName);
            }
        }
    }

    public static Collection<BaseTreeType> getAllTypes() {
        return treeTypes.values();
    }

    public static BaseTreeType getTypeByName(String name) {
        return treeTypes.get(name);
    }

    public static BaseTreeType getTypeByStack(ItemStack stack) {
        if(stack == ItemStack.EMPTY) {
            return null;
        }

        // Find the first treetype that works with the given stack
        return treeTypes.values().stream().filter(treeType -> treeType.worksWith(stack)).findFirst().orElse(null);
    }

    public static void registerTreeType(BaseTreeType treeType) {
        if(treeTypes.containsKey(treeType.typeName)) {
            Logz.warn("Overwriting tree type: %s", treeType.typeName);
        } else {
            Logz.info("Registering tree type: %s", treeType.typeName);
        }

        treeTypes.put(treeType.typeName, treeType);
    }

    public static void reload() {
        reload(null);
    }

    public static void reload(String integrationFileName) {
        treeTypes = new HashMap<>();

        if(!ConfigurationHandler.treeTypesDir.exists()) {
            Logz.warn("Tree Types folder does not exist!");
            return;
        }

        for(File file : ConfigurationHandler.treeTypesDir.listFiles()) {
            if(!file.getName().endsWith(".js") || file.getName().equals("defaults.js")) {
                continue;
            }

            if(integrationFileName != null && !file.getName().equals(integrationFileName)) {
                continue;
            }

            Logz.info(" > Loading tree types from file: '%s'", file.getName());
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");

            try {
                engine.eval(new FileReader(file));
                Invocable invocable = (Invocable) engine;

                boolean isEnabled = (boolean) invocable.invokeFunction("isEnabled");

                if(isEnabled) {
                    invocable.invokeFunction("main", file.getName());
                }
            } catch (ScriptException e) {
                Logz.warn("Could not compile+eval script=%s: %s", file.getName(), e);
                continue;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                Logz.warn("Script %s is missing main method: %s", file.getName(), e);
            }
        }

        if(treeTypes.size() == 0) {
            Logz.warn("No tree types registered. This is bad, the mod will not be very useful!");
        } else {
            Logz.info("Loaded %d tree types.", treeTypes.size());
        }


    }

}
