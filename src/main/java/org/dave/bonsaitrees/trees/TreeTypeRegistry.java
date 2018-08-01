package org.dave.bonsaitrees.trees;

import net.minecraft.item.ItemStack;
import org.dave.bonsaitrees.api.IBonsaiIntegration;
import org.dave.bonsaitrees.api.IBonsaiSoil;
import org.dave.bonsaitrees.api.IBonsaiTreeType;
import org.dave.bonsaitrees.api.ITreeTypeRegistry;
import org.dave.bonsaitrees.compat.CraftTweaker2.registries.SoilStatsModificationsRegistry;
import org.dave.bonsaitrees.compat.CraftTweaker2.registries.TreeGrowthModificationsRegistry;
import org.dave.bonsaitrees.misc.ConfigurationHandler;
import org.dave.bonsaitrees.utility.Logz;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TreeTypeRegistry implements ITreeTypeRegistry {
    private Map<String, IBonsaiTreeType> treeTypes = new HashMap<>();
    private Map<IBonsaiTreeType, IBonsaiIntegration> integrationMap = new HashMap<>();

    public void clear() {
        treeTypes = new HashMap<>();
        integrationMap = new HashMap<>();
    }

    public void checkMissingShapes() {
        for(IBonsaiTreeType type : getAllTypes()) {
            int shapes = TreeShapeRegistry.getShapeCountForType(type);
            if(shapes == 0) {
                Logz.warn("Tree type '%s' has no shapes configured", type.getName());
            }
        }
    }

    public String getBaseGrowTimeHuman(IBonsaiTreeType treeType) {
        float actualGrowTime = this.getBaseGrowTime(treeType);
        int fullSeconds = (int)actualGrowTime / 20;
        int minutes = fullSeconds / 60;
        int seconds = fullSeconds % 60;

        return String.format("%d:%02d", minutes, seconds);
    }

    public int getBaseGrowTime(IBonsaiTreeType treeType) {
        return TreeGrowthModificationsRegistry.getModifiedGrowTime(treeType);
    }

    public int getFinalGrowTime(IBonsaiTreeType treeType, IBonsaiSoil soil) {
        return (int) Math.floor(this.getBaseGrowTime(treeType) * SoilStatsModificationsRegistry.getModifiedGrowTimeModifier(soil));
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
