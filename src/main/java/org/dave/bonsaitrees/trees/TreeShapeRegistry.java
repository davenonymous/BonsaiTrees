package org.dave.bonsaitrees.trees;

import com.google.gson.stream.JsonReader;
import net.minecraft.item.ItemStack;
import org.dave.bonsaitrees.misc.ConfigurationHandler;
import org.dave.bonsaitrees.utility.Logz;
import org.dave.bonsaitrees.utility.SerializationHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class TreeShapeRegistry {
    public static Map<String, TreeShape> treeShapes;

    public static void init() {
        reload();
    }

    public static String getRandomShapeForStack(ItemStack stack) {
        if(stack.isEmpty()) {
            return null;
        }

        List<String> options = new ArrayList<>();
        for(Map.Entry<String, TreeShape> shapeEntry : treeShapes.entrySet()) {
            ItemStack sapling = shapeEntry.getValue().sapling;
            boolean sameItem = sapling.getItem() == stack.getItem();
            boolean sameMeta = sapling.getMetadata() == stack.getMetadata();
            boolean sameNbt = ItemStack.areItemStackTagsEqual(sapling, stack);
            if(sameItem && sameMeta && sameNbt) {
                options.add(shapeEntry.getKey());
            }
        }

        if(options.size() == 0) {
            return null;
        }

        return options.get(new Random().nextInt(options.size()));
    }

    public static void reload() {
        treeShapes = new HashMap<>();

        if(!ConfigurationHandler.treeShapesDir.exists()) {
            Logz.warn("Tree Shapes folder does not exist!");
            return;
        }

        for(File file : ConfigurationHandler.treeShapesDir.listFiles()) {
            if (!file.getName().endsWith(".json")) {
                continue;
            }

            TreeShape shape = null;
            Logz.info(" > Loading tree shape from file: '%s'", file.getName());
            try {
                shape = SerializationHelper.GSON.fromJson(new JsonReader(new FileReader(file)), TreeShape.class);
            } catch (FileNotFoundException e) {
            }

            if(shape == null) {
                Logz.warn("Could not load shape from file: '%s'", file.getName());
                continue;
            }

            treeShapes.put(file.getName().substring(0, file.getName().length()-4), shape);
        }

        if(treeShapes.size() == 0) {
            Logz.warn("No tree shapes registered. This is bad!");
        } else {
            Logz.info("Loaded %d tree shapes.", treeShapes.size());
        }
    }
}
