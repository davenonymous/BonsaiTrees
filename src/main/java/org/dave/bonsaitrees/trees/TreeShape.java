package org.dave.bonsaitrees.trees;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.dave.bonsaitrees.utility.Logz;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TreeShape {
    ItemStack sapling = ItemStack.EMPTY;
    private Map<BlockPos, IBlockState> blocks;

    private int width = 0;
    private int height = 0;
    private int depth = 0;

    public TreeShape(String blockName, int meta) {
        Block saplingBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockName));
        this.sapling = new ItemStack(saplingBlock, meta);
    }

    public TreeShape(ItemStack sapling) {
        this.sapling = sapling;
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

    public double getScaleRatio() {
        int dim = Math.max(width, depth)+1;
        return 1.0d / (double)dim;
    }
}
