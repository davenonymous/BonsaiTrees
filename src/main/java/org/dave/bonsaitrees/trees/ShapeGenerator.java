package org.dave.bonsaitrees.trees;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
import java.util.Random;

public class ShapeGenerator {
    public static final int NUM_SHAPES = 3;


    private static TreeShape generateShape(World world, BlockPos pos, BaseTreeType type) {
        if(type.getSource() == null) {
            return null;
        }

        Random rand = new Random();
        TreeShape result = new TreeShape(type.typeName);

        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");

        File file = new File(ConfigurationHandler.treeTypesDir, type.getSource());
        try {
            engine.eval(new FileReader(file));
            Invocable invocable = (Invocable) engine;

            if(engine.get("generateTree") == null) {
                Logz.warn("Script '%s' is missing the tree generation function", file.getName());
                return null;
            }

            boolean isEnabled = (boolean) invocable.invokeFunction("isEnabled");

            if(isEnabled) {
                invocable.invokeFunction("generateTree", type, world, pos, rand);
                result.setBlocksByFloodFill(world, pos);
                if(engine.get("modifyTreeShape") != null) {
                    invocable.invokeFunction("modifyTreeShape", type, result.getBlocks());
                }
            }
        } catch (ScriptException e) {
            Logz.warn("Could not compile+eval script=%s: %s", file.getName(), e);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            Logz.warn("Script %s is missing generateTree method: %s", file.getName(), e);
        }

        return result;
    }

    public static void generateMissingShapes(World world, BlockPos pos) {
        for(BaseTreeType type : TreeTypeRegistry.getAllTypes()) {
            int shapes = TreeShapeRegistry.getShapeCountForType(type);
            if (shapes > 0) {
                continue;
            }

            Logz.info("Generating shapes for tree: %s", type.typeName);

            clearArea(world, pos, 32);
            for(int i = 0; i < NUM_SHAPES; i++) {
                TreeShape treeShape = generateShape(world, pos, type);
                clearArea(world, pos, 32);

                if(treeShape == null) {
                    continue;
                }

                if (treeShape.getBlocks().size() == 0) {
                    Logz.warn("Can not determine shape");
                    continue;
                }

                String filename = treeShape.saveToFile();
                Logz.info("Created shape file: %s", filename);
            }
        }
    }


    private static void clearArea(World world, BlockPos pos, int areaSize) {
        for(int deltaX = 0; deltaX < areaSize; deltaX++) {
            for(int deltaY = 0; deltaY < areaSize; deltaY++) {
                for(int deltaZ = 0; deltaZ < areaSize; deltaZ++) {
                    int x = pos.getX() - areaSize / 2;
                    int y = pos.getY();
                    int z = pos.getZ() - areaSize / 2;

                    x += deltaX;
                    y += deltaY;
                    z += deltaZ;

                    world.setBlockToAir(new BlockPos(x, y, z));
                }
            }
        }
    }
}
