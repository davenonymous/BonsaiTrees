package org.dave.bonsaitrees.trees;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.dave.bonsaitrees.BonsaiTrees;
import org.dave.bonsaitrees.api.IBonsaiIntegration;
import org.dave.bonsaitrees.api.IBonsaiTreeType;
import org.dave.bonsaitrees.utility.Logz;

import java.util.Random;

public class ShapeGenerator {
    public static final int NUM_SHAPES = 3;

    private static TreeShape generateShape(World world, BlockPos pos, IBonsaiTreeType type, Random rand) {
        TreeShape result = new TreeShape(type.getName());
        IBonsaiIntegration integrator = BonsaiTrees.instance.typeRegistry.getIntegrationForType(type);
        integrator.generateTree(type, world, pos, rand);

        result.setBlocksByFloodFill(world, pos);

        integrator.modifyTreeShape(type, result.getBlocks());

        return result;
    }

    public static void generateMissingShapes(World world, BlockPos pos) {
        Random rand = new Random();

        for(IBonsaiTreeType type : BonsaiTrees.instance.typeRegistry.getAllTypes()) {
            int shapes = TreeShapeRegistry.getShapeCountForType(type);
            if (shapes > 0) {
                continue;
            }

            Logz.info("Generating shapes for tree: %s", type.getName());

            clearArea(world, pos, 32);
            for(int i = 0; i < NUM_SHAPES; i++) {
                TreeShape treeShape = generateShape(world, pos, type, rand);
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
