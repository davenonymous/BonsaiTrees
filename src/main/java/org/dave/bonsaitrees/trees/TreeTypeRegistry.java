package org.dave.bonsaitrees.trees;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.dave.bonsaitrees.misc.ConfigurationHandler;
import org.dave.bonsaitrees.utility.Logz;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class TreeTypeRegistry {
    public static Map<String, TreeType> treeTypes;

    public static void init() {
        reload();
    }

    public static TreeType getTypeForStack(ItemStack stack) {
        if(stack == ItemStack.EMPTY) {
            return null;
        }

        for(TreeType type : treeTypes.values()) {
            boolean sameItem = type.sapling.getItem() == stack.getItem();
            boolean sameMeta = type.sapling.getMetadata() == stack.getMetadata();
            boolean sameNbt = ItemStack.areItemStackTagsEqual(type.sapling, stack);
            if(sameItem && sameMeta && sameNbt) {
                return type;
            }
        }

        return null;

    }

    public static void registerTreeType(String name, TreeType treeType) {
        if(treeTypes.containsKey(name)) {
            Logz.warn("Overwriting tree type: %s", name);
        } else {
            Logz.info("Registering tree type: %s", name);
        }

        treeTypes.put(name, treeType);
    }

    public static void reload() {
        treeTypes = new HashMap<>();

        if(!ConfigurationHandler.treeTypesDir.exists()) {
            Logz.warn("Tree Types folder does not exist!");
            return;
        }

        for(File file : ConfigurationHandler.treeTypesDir.listFiles()) {
            if(!file.getName().endsWith(".js")) {
                continue;
            }

            Logz.info(" > Loading tree types from file: '%s'", file.getName());
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");

            try {
                engine.eval(new FileReader(file));
                Invocable invocable = (Invocable) engine;

                invocable.invokeFunction("main");
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
