package org.dave.bonsaitrees.trees;

import com.google.gson.stream.JsonReader;
import net.minecraft.item.ItemStack;
import org.dave.bonsaitrees.base.BaseTreeType;
import org.dave.bonsaitrees.misc.ConfigurationHandler;
import org.dave.bonsaitrees.utility.Logz;
import org.dave.bonsaitrees.utility.SerializationHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class TreeShapeRegistry {
    private static Map<String, TreeShape> treeShapesByFilename;
    private static Map<BaseTreeType, List<TreeShape>> treeShapesByType;
    private static final Random rand = new Random();

    public static void init() {
        reload();
    }

    public static TreeShape getTreeShapeByFilename(String name) {
        return treeShapesByFilename.get(name);
    }

    public static TreeShape getRandomShapeForStack(ItemStack stack) {
        if(stack.isEmpty()) {
            return null;
        }

        BaseTreeType type = TreeTypeRegistry.getTypeByStack(stack);
        if(type == null) {
            return null;
        }

        return getRandomShapeByType(type);
    }

    private static TreeShape getRandomShapeByType(BaseTreeType type) {
        if(!treeShapesByType.containsKey(type)) {
            return null;
        }

        List<TreeShape> shapes = treeShapesByType.get(type);
        return shapes.get(rand.nextInt(shapes.size()));
    }

    public static void reload() {
        treeShapesByFilename = new HashMap<>();
        treeShapesByType = new HashMap<>();

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

            String shortenedFilename = file.getName().substring(0, file.getName().length()-4);
            shape.setFileName(shortenedFilename);

            treeShapesByFilename.put(shortenedFilename, shape);
            if(!treeShapesByType.containsKey(shape.getTreeType())) {
                treeShapesByType.put(shape.getTreeType(), new ArrayList<>());
            }
            treeShapesByType.get(shape.getTreeType()).add(shape);
        }

        if(treeShapesByFilename.size() == 0) {
            Logz.warn("No tree shapes registered. This is bad!");
        } else {
            Logz.info("Loaded %d tree shapes.", treeShapesByFilename.size());
        }
    }
}
