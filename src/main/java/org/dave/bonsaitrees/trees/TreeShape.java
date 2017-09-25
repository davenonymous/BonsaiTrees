package org.dave.bonsaitrees.trees;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.dave.bonsaitrees.base.BaseTreeType;
import org.dave.bonsaitrees.misc.ConfigurationHandler;
import org.dave.bonsaitrees.misc.FloodFill;
import org.dave.bonsaitrees.utility.SerializationHelper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TreeShape {
    private Map<BlockPos, IBlockState> blocks;
    private final String typeName;
    private String fileName;

    private int width = 0;
    private int height = 0;
    private int depth = 0;

    public TreeShape(String typeName) {
        this.typeName = typeName;
    }

    public BaseTreeType getTreeType() {
        return TreeTypeRegistry.getTypeByName(typeName);
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public String saveToFile() {
        String json = SerializationHelper.GSON.toJson(this);

        String sane = typeName.replaceAll("[^a-zA-Z0-9\\._]+", "_").toLowerCase();
        File dstFile = getNextFile(sane.replace(".json", ""));

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(dstFile));
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return dstFile.getName();
    }

    private static final Pattern p = Pattern.compile("0*([0-9]+)\\.json$", Pattern.CASE_INSENSITIVE);
    private static File getNextFile(String prefix) {
        int highestNum = 0;
        for(File file : ConfigurationHandler.treeShapesDir.listFiles()) {
            String fileName = file.getName();
            if (!fileName.endsWith(".json") || !fileName.toLowerCase().startsWith(prefix)) {
                continue;
            }

            Matcher m = p.matcher(fileName);
            if(!m.find()) {
                continue;
            }

            int num = Integer.parseInt(m.group(1));
            if(num > highestNum) {
                highestNum = num;
            }
        }

        int nextNum = highestNum+1;
        String fileName = String.format("%s%03d.json", prefix, nextNum);
        return new File(ConfigurationHandler.treeShapesDir, fileName);
    }

    public void setBlocksByFloodFill(World world, BlockPos pos) {
        FloodFill floodFill = new FloodFill(world, pos);
        Map<BlockPos, IBlockState> connectedBlocks = floodFill.getConnectedBlocks();
        if(connectedBlocks.size() == 0) {
            return;
        }

        this.setBlocks(connectedBlocks);
    }

    public void setBlocks(Map<BlockPos, IBlockState> blocks) {
        this.blocks = blocks;
        width = 0;
        height = 0;
        depth = 0;
        for(BlockPos pos : blocks.keySet()) {
            if(pos.getX() > width)width = pos.getX();
            if(pos.getY() > height)height = pos.getY();
            if(pos.getZ() > depth)depth = pos.getZ();
        }
    }

    public List<BlockPos> getToRenderPositions() {
        return new ArrayList<>(blocks.keySet());
    }

    public Map<BlockPos,IBlockState> getBlocks() {
        return blocks;
    }

    public IBlockState getStateAtPos(BlockPos pos) {
        return blocks.get(pos);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getDepth() {
        return depth;
    }

    public double getScaleRatio(boolean inclHeight) {
        int dim = Math.max(width, depth);
        if(inclHeight) {
            dim = Math.max(height, dim);
        }

        dim += 1;
        return 1.0d / (double)dim;
    }
}
