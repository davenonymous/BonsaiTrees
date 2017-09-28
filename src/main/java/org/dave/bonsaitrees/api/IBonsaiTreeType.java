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
