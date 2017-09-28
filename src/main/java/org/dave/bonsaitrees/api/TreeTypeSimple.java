package org.dave.bonsaitrees.api;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class TreeTypeSimple implements IBonsaiTreeType {
    private String name;
    private ItemStack sapling = ItemStack.EMPTY;
    private int growTime = 600;

    public TreeTypeSimple(String name, ItemStack sapling) {
        this.name = name;
        this.sapling = sapling;
    }

    public TreeTypeSimple(String name, String saplingName, int saplingMeta) {
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(saplingName));
        this.name = name;
        this.sapling = new ItemStack(block, 1, saplingMeta);
    }

    public void setGrowTime(int growTime) {
        this.growTime = growTime;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getGrowTime() {
        return growTime;
    }

    @Override
    public boolean worksWith(ItemStack stack) {
        boolean sameItem = sapling.getItem() == stack.getItem();
        boolean sameMeta = sapling.getMetadata() == stack.getMetadata();

        if(sameItem && sameMeta) {
            return true;
        }

        return false;
    }

    @Override
    public ItemStack getExampleStack() {
        return this.sapling.copy();
    }
}
