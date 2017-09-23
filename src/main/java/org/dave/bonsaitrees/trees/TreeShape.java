package org.dave.bonsaitrees.trees;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import org.dave.bonsaitrees.base.BaseTreeType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
