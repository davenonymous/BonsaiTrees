package org.dave.bonsaitrees.api;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public interface IBonsaiTreeType {
    String getName();

    List<TreeTypeDrop> drops = new ArrayList<>();

    boolean worksWith(ItemStack stack);
    ItemStack getExampleStack();

    default void addDrop(ItemStack stack, float chance) {
        this.drops.add(new TreeTypeDrop(stack.copy(), chance));
    }

    default void addDrop(String itemName, int count, int itemMeta, float chance) {
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

    default int getGrowTime() {
        return 600;
    }

    default double growRate(World world, BlockPos pos, IBlockState state, double progress) {
        return 1.0d;
    }

    default double growTick(World world, BlockPos pos, IBlockState state, double progress) {
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

    default String getGrowTimeHuman() {
        int fullSeconds = this.getGrowTime() / 20;
        int minutes = fullSeconds / 60;
        int seconds = fullSeconds % 60;

        return minutes + ":" + seconds;
    }
}
