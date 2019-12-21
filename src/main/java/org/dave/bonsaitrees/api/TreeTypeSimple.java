package org.dave.bonsaitrees.api;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.dave.bonsaitrees.utility.Logz;

import java.util.*;

public class TreeTypeSimple implements IBonsaiTreeType {
    private String name;
    private ItemStack sapling = ItemStack.EMPTY;
    private List<TreeTypeDrop> drops = new ArrayList<>();
    private WorldGenerator worldGen = null;
    private Set<String> compatibleSoilTags = new HashSet<>();

    public TreeTypeSimple(String name, ItemStack sapling) {
        this.name = name;
        this.sapling = sapling;
    }

    public TreeTypeSimple(String name, String saplingName, int saplingMeta) {
        ResourceLocation saplingNameResource = new ResourceLocation(saplingName);
        Block block = ForgeRegistries.BLOCKS.getValue(saplingNameResource);
        if(block != null && block != Blocks.AIR) {
            this.sapling = new ItemStack(block, 1, saplingMeta);
        }

        if(this.sapling == null || this.sapling.isEmpty()) {
            Item item = ForgeRegistries.ITEMS.getValue(saplingNameResource);
            if(item != null) {
                this.sapling = new ItemStack(item, 1, saplingMeta);
            }
        }

        if(this.sapling == null || this.sapling.isEmpty()) {
            Logz.warn("Could not find an ItemStack for the sapling resource: %s", saplingName);
        }

        this.name = name;

    }

    public void addDrop(ItemStack stack, float chance) {
        this.drops.add(new TreeTypeDrop(stack.copy(), chance));
    }

    public void addDrop(String itemName, int count, int itemMeta, float chance) {
        Block dropBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(itemName));
        if(dropBlock != null && dropBlock != Blocks.AIR) {
            ItemStack dropStack = new ItemStack(dropBlock, count, itemMeta);
            if(!dropStack.isEmpty()) {
                this.drops.add(new TreeTypeDrop(dropStack, chance));
                return;
            }
        }

        Item dropItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName));
        if(dropItem != null) {
            ItemStack dropStack = new ItemStack(dropItem, count, itemMeta);
            if(!dropStack.isEmpty()) {
                this.drops.add(new TreeTypeDrop(dropStack, chance));
                return;
            }
        }

        Logz.warn("Could not find an ItemStack for the drop resource: %s", itemName);
    }

    public void addCompatibleSoilTag(String tag) {
        this.compatibleSoilTags.add(tag);
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
    public Set<String> getCompatibleSoilTags() {
        if(compatibleSoilTags == null || compatibleSoilTags.size() == 0) {
            return new HashSet<>(Arrays.asList("dirt", "grass"));
        }

        return compatibleSoilTags;
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
