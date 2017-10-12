package org.dave.bonsaitrees.api;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class TreeTypeSimple implements IBonsaiTreeType {
    private String name;
    private ItemStack sapling = ItemStack.EMPTY;
    private List<TreeTypeDrop> drops = new ArrayList<>();
    private WorldGenerator worldGen = null;

    public TreeTypeSimple(String name, ItemStack sapling) {
        this.name = name;
        this.sapling = sapling;
    }

    public TreeTypeSimple(String name, String saplingName, int saplingMeta) {
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(saplingName));
        this.name = name;
        this.sapling = new ItemStack(block, 1, saplingMeta);
    }

    public void addDrop(ItemStack stack, float chance) {
        this.drops.add(new TreeTypeDrop(stack.copy(), chance));
    }

    public void addDrop(String itemName, int count, int itemMeta, float chance) {
        Block dropBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(itemName));
        if(dropBlock != null && dropBlock != Blocks.AIR) {
            this.drops.add(new TreeTypeDrop(new ItemStack(dropBlock, count, itemMeta), chance));
            return;
        }

        Item dropItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName));
        if(dropItem != null) {
            this.drops.add(new TreeTypeDrop(new ItemStack(dropItem, count, itemMeta), chance));
            return;
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<TreeTypeDrop> getDrops() {
        return drops;
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

    public WorldGenerator getWorldGen() {
        return worldGen;
    }

    public void setWorldGen(WorldGenerator worldGen) {
        this.worldGen = worldGen;
    }
}
