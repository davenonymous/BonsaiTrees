package org.dave.bonsaitrees.trees;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.dave.bonsaitrees.BonsaiTrees;
import org.dave.bonsaitrees.api.IBonsaiSoil;
import org.dave.bonsaitrees.api.IBonsaiTreeType;

public class TreeGrowthHelper {
    public static double growTick(IBonsaiTreeType treeType, IBonsaiSoil bonsaiSoil, World world, BlockPos pos, IBlockState state, double progress) {
        boolean hasAir = world.isAirBlock(pos.up());

        float actualGrowTime = BonsaiTrees.instance.typeRegistry.getFinalGrowTime(treeType, bonsaiSoil);

        // Only grow if the space above it is AIR, otherwise reset to third of the progress
        if(!hasAir && progress > actualGrowTime / 3) {
            return actualGrowTime / 3;
        }

        if(progress < actualGrowTime && hasAir) {
            return progress + treeType.getGrowthRate(world, pos, state, progress);
        }

        return progress;
    }
}
