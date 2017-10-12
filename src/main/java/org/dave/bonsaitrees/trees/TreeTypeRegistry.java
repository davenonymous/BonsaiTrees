package org.dave.bonsaitrees.trees;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.dave.bonsaitrees.api.IBonsaiIntegration;
import org.dave.bonsaitrees.api.IBonsaiTreeType;
import org.dave.bonsaitrees.api.ITreeTypeRegistry;
import org.dave.bonsaitrees.misc.ConfigurationHandler;
import org.dave.bonsaitrees.utility.Logz;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TreeTypeRegistry implements ITreeTypeRegistry {
    private Map<String, IBonsaiTreeType> treeTypes = new HashMap<>();
    private Map<IBonsaiTreeType, IBonsaiIntegration> integrationMap = new HashMap<>();

    public void checkMissingShapes() {
        for(IBonsaiTreeType type : getAllTypes()) {
            int shapes = TreeShapeRegistry.getShapeCountForType(type);
            if(shapes == 0) {
                Logz.warn("Tree type '%s' has no shapes configured", type.getName());
            }
        }
    }

    public double growTick(IBonsaiTreeType treeType, World world, BlockPos pos, IBlockState state, double progress) {
        boolean hasAir = world.isAirBlock(pos.up());

        float actualGrowTime = this.getGrowTime(treeType);

        // Only grow if the space above it is AIR, otherwise reset to third of the progress
        if(!hasAir && progress > actualGrowTime / 3) {
            return actualGrowTime / 3;
        }

        if(progress < actualGrowTime && hasAir) {
            return progress + treeType.getGrowthRate(world, pos, state, progress);
        }

        return progress;
    }

    public String getGrowTimeHuman(IBonsaiTreeType treeType) {
        float actualGrowTime = this.getGrowTime(treeType);
        int fullSeconds = (int)actualGrowTime / 20;
        int minutes = fullSeconds / 60;
        int seconds = fullSeconds % 60;

        return String.format("%d:%02d", minutes, seconds);
    }

    public int getGrowTime(IBonsaiTreeType treeType) {
        return TreeGrowthModificationsRegistry.getModifiedGrowTime(treeType);
    }

    public Collection<IBonsaiTreeType> getAllTypes() {
        return treeTypes.values();
    }

    public IBonsaiTreeType getTypeByName(String name) {
        return treeTypes.get(name);
    }

    public IBonsaiTreeType getTypeByStack(ItemStack stack) {
        if(stack == ItemStack.EMPTY) {
            return null;
        }

        // Find the first treetype that works with the given stack
        return treeTypes.values().stream().filter(treeType -> treeType.worksWith(stack)).findFirst().orElse(null);
    }

    public IBonsaiIntegration getIntegrationForType(IBonsaiTreeType treeType) {
        return integrationMap.get(treeType);
    }

    @Override
    public void registerTreeType(IBonsaiIntegration integrator, IBonsaiTreeType treeType) {
        if(Arrays.asList(ConfigurationHandler.IntegrationSettings.disabledTreeTypes).contains(treeType.getName())) {
            Logz.info("Tree type %s has been disabled via config. Skipping...", treeType.getName());
            return;
        }

        if(treeTypes.containsKey(treeType.getName())) {
            Logz.info("Tree type %s has already been loaded before. Skipping...", treeType.getName());
            return;
        }

        if(treeType.getExampleStack().isEmpty()) {
            Logz.info("Tree type %s has no sapling stack. Skipping...", treeType.getName());
            return;
        }

        treeTypes.put(treeType.getName(), treeType);
        integrationMap.put(treeType, integrator);
    }
}
