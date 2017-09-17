package org.dave.bonsaitrees.trees;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class TreeType {
    ItemStack sapling = ItemStack.EMPTY;
    List<TreeTypeDrop> drops = new ArrayList<>();
    int growTime = 600;

    public TreeType(String blockName, int meta) {
        Block saplingBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockName));
        this.sapling = new ItemStack(saplingBlock, 1, meta);
    }

    public TreeType(ItemStack sapling) {
        this.sapling = sapling;
    }

    public void setGrowTime(int growTime) {
        this.growTime = growTime;
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

    public int getGrowTime() {
        return growTime;
    }

    public List<TreeTypeDrop> getDrops() {
        return drops;
    }
}
