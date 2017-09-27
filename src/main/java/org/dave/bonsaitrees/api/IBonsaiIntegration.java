package org.dave.bonsaitrees.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;
import java.util.Random;

public interface IBonsaiIntegration {
    void registerTrees(ITreeTypeRegistry registry);

    void generateTree(IBonsaiTreeType type, World world, BlockPos pos, Random rand);

    default void modifyTreeShape(IBonsaiTreeType type, Map<BlockPos, IBlockState> blocks) {
    }
}
