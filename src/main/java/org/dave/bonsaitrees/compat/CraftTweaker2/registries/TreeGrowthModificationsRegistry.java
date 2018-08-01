package org.dave.bonsaitrees.compat.CraftTweaker2.registries;

import org.dave.bonsaitrees.api.IBonsaiTreeType;
import org.dave.bonsaitrees.misc.ConfigurationHandler;

import java.util.HashMap;
import java.util.Map;

public class TreeGrowthModificationsRegistry {
    public static Map<String, Float> multipliers = new HashMap<>();

    public static void setMultiplier(String treeName, float multiplier) {
        multipliers.put(treeName, multiplier);
    }

    public static int getModifiedGrowTime(IBonsaiTreeType type) {
        return (int)Math.floor(ConfigurationHandler.GeneralSettings.baseGrowTicks * multipliers.getOrDefault(type.getName(), 1.0f));
    }
}
