package org.dave.bonsaitrees.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public interface IBonsaiTreeType {
    String getName();
    List<TreeTypeDrop> getDrops();

    boolean worksWith(ItemStack stack);
    ItemStack getExampleStack();

    default double getGrowthRate(World world, BlockPos pos, IBlockState state, double progress) {
        return 1.0d;
    }
}
