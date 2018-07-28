package org.dave.bonsaitrees.trees;

import com.google.gson.stream.JsonReader;
import net.minecraft.item.ItemStack;
import org.dave.bonsaitrees.BonsaiTrees;
import org.dave.bonsaitrees.api.IBonsaiTreeType;
import org.dave.bonsaitrees.misc.ConfigurationHandler;
import org.dave.bonsaitrees.utility.Logz;
import org.dave.bonsaitrees.utility.ResourceLoader;
import org.dave.bonsaitrees.utility.SerializationHelper;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class TreeShapeRegistry {
    private static Map<String, TreeShape> treeShapesByFilename;
    private static Map<IBonsaiTreeType, List<TreeShape>> treeShapesByType;
    private static final Random rand = new Random();

    public static void init() {
        reload();
    }

    public static Collection<TreeShape> getTreeShapes() {
        return treeShapesByFilename.values();
    }

    public static TreeShape getTreeShapeByFilename(String name) {
        return treeShapesByFilename.get(name);
    }

    public static TreeShape getNextTreeShape(IBonsaiTreeType type, TreeShape shape) {
        List<TreeShape> shapeList = treeShapesByType.get(type);
        if(shapeList == null || shapeList.size() == 0) {
            return shape;
        }

        if(shape == null) {
            return shapeList.get(0);
        }

        int index = shapeList.indexOf(shape);
        if(index == -1) {
            return shape;
        }

        if(index == shapeList.size()-1) {
            return shapeList.get(0);
        }

        return shapeList.get(index+1);
    }

    public static TreeShape getRandomShapeForStack(ItemStack stack) {
        if(stack.isEmpty()) {
            return null;
        }

        IBonsaiTreeType type = BonsaiTrees.instance.typeRegistry.getTypeByStack(stack);
        if(type == null) {
            return null;
        }

        return getRandomShapeByType(type);
    }

    public static int getShapeCountForType(IBonsaiTreeType type) {
        if(!treeShapesByType.containsKey(type) || treeShapesByType.get(type) == null) {
            return 0;
        }

        return treeShapesByType.get(type).size();
    }

    public static TreeShape getRandomShapeByType(IBonsaiTreeType type) {
        if(!treeShapesByType.containsKey(type)) {
            return null;
        }

        List<TreeShape> shapes = treeShapesByType.get(type);
        return shapes.get(rand.nextInt(shapes.size()));
    }

    public static void reload() {
        treeShapesByFilename = new HashMap<>();
        treeShapesByType = new HashMap<>();

        ResourceLoader loader = new ResourceLoader(ConfigurationHandler.treeShapesDir, "assets/bonsaitrees/config/shapes.d/");
        for(Map.Entry<String, InputStream> entry : loader.getResources().entrySet()) {
            String filename = entry.getKey();
            InputStream is = entry.getValue();

            if (!filename.endsWith(".json")) {
                continue;
            }

            Logz.debug(" > Loading tree shape from file: '%s'", filename);
            TreeShape shape = SerializationHelper.GSON.fromJson(new JsonReader(new InputStreamReader(is)), TreeShape.class);
            if(shape == null) {
                Logz.warn("Could not load shape from file: '%s'", filename);
                continue;
            }

            if(!ConfigurationHandler.IntegrationSettings.loadShapesOfUnloadedTrees && shape.getTreeType() == null) {
                Logz.debug("Tree not registered. Skipping shape from file: %s", filename);
                continue;
            }

            String shortenedFilename = filename.substring(0, filename.length()-4);
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
