package org.dave.bonsaitrees.base;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.dave.bonsaitrees.trees.TreeTypeDrop;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseTreeType {
    public List<TreeTypeDrop> drops = new ArrayList<>();
    public int growTime = 600;
    public final String typeName;
    public String source = null;

    public BaseTreeType(String typeName) {
        this.typeName = typeName;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setGrowTime(int growTime) {
        this.growTime = growTime;
    }

    public int getGrowTime() {
        return growTime;
    }

    public String getGrowTimeHuman() {
        int fullSeconds = growTime / 20;
        int minutes = fullSeconds / 60;
        int seconds = fullSeconds % 60;

        return minutes + ":" + seconds;
    }

    public double growTick(World world, BlockPos pos, IBlockState state, double progress) {
        boolean hasAir = world.isAirBlock(pos.up());

        // Only grow if the space above it is AIR, otherwise reset to third of the progress
        if(!hasAir && progress > this.getGrowTime() / 3) {
            return this.getGrowTime() / 3;
        }

        if(progress < this.getGrowTime() && hasAir) {
            return progress + growRate(world, pos, state, progress);
        }

        return progress;
    }


    public List<TreeTypeDrop> getDrops() {
        return drops;
    }

    public void addDrop(ItemStack stack, int chance) {
        this.drops.add(new TreeTypeDrop(stack.copy(), chance));
    }

    public void addDrop(String blockName, int meta, int count, int chance) {
        Block dropBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockName));
        if(dropBlock != null && dropBlock != Blocks.AIR) {
            this.drops.add(new TreeTypeDrop(new ItemStack(dropBlock, count, meta), chance));
            return;
        }

        Item dropItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(blockName));
        if(dropItem != null) {
            this.drops.add(new TreeTypeDrop(new ItemStack(dropItem, count, meta), chance));
            return;
        }
    }

    public abstract double growRate(World world, BlockPos pos, IBlockState state, double progress);

    public abstract boolean worksWith(ItemStack stack);

    public abstract ItemStack getExampleStack();
}
