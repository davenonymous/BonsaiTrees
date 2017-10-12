package org.dave.bonsaitrees.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.dave.bonsaitrees.misc.ConfigurationHandler;

import java.util.List;

public interface IBonsaiTreeType {
    String getName();
    List<TreeTypeDrop> getDrops();

    boolean worksWith(ItemStack stack);
    ItemStack getExampleStack();

    default int getGrowTime() {
        return ConfigurationHandler.GeneralSettings.baseGrowTicks;
    }

    default float getGrowTimeMultiplier() {
        return 1.0f;
    }

    default double growRate(World world, BlockPos pos, IBlockState state, double progress) {
        return 1.0d;
    }

    default double growTick(World world, BlockPos pos, IBlockState state, double progress) {
        boolean hasAir = world.isAirBlock(pos.up());

        float actualGrowTime = this.getGrowTime() * this.getGrowTimeMultiplier();

        // Only grow if the space above it is AIR, otherwise reset to third of the progress
        if(!hasAir && progress > actualGrowTime / 3) {
            return actualGrowTime / 3;
        }

        if(progress < actualGrowTime && hasAir) {
            return progress + growRate(world, pos, state, progress);
        }

        return progress;
    }

    default String getGrowTimeHuman() {
        float actualGrowTime = this.getGrowTime() * this.getGrowTimeMultiplier();
        int fullSeconds = (int)actualGrowTime / 20;
        int minutes = fullSeconds / 60;
        int seconds = fullSeconds % 60;

        return String.format("%d:%02d", minutes, seconds);
    }
}
