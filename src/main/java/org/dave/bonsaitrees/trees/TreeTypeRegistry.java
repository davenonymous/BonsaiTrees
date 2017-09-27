package org.dave.bonsaitrees.trees;

import net.minecraft.item.ItemStack;
import org.dave.bonsaitrees.api.IBonsaiIntegration;
import org.dave.bonsaitrees.api.IBonsaiTreeType;
import org.dave.bonsaitrees.api.ITreeTypeRegistry;
import org.dave.bonsaitrees.utility.Logz;

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
        if(treeTypes.containsKey(treeType.getName())) {
            Logz.warn("Overwriting tree type: %s", treeType.getName());
        } else {
            Logz.debug("Registering tree type: %s", treeType.getName());
        }

        treeTypes.put(treeType.getName(), treeType);
        integrationMap.put(treeType, integrator);
    }
}
