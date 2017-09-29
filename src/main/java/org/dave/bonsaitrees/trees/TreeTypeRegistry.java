package org.dave.bonsaitrees.trees;

import net.minecraft.item.ItemStack;
import org.dave.bonsaitrees.api.IBonsaiIntegration;
import org.dave.bonsaitrees.api.IBonsaiTreeType;
import org.dave.bonsaitrees.api.ITreeTypeRegistry;
import org.dave.bonsaitrees.misc.ConfigurationHandler;
import org.dave.bonsaitrees.utility.Logz;

import java.util.*;

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
