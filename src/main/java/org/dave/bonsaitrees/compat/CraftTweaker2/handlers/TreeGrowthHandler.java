package org.dave.bonsaitrees.compat.CraftTweaker2.handlers;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import org.dave.bonsaitrees.compat.CraftTweaker2.registries.TreeGrowthModificationsRegistry;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.bonsaitrees.TreeGrowth")
public class TreeGrowthHandler {
    @ZenMethod
    public static void setTickMultiplier(String treeName, float multiplier) {
        CraftTweakerAPI.apply(new SetTreeGrowthTimeMultiplier(treeName, multiplier));
    }

    private static class SetTreeGrowthTimeMultiplier implements IAction {
        String treeName;
        float multiplier;

        public SetTreeGrowthTimeMultiplier(String treeName, float multiplier) {
            this.treeName = treeName;
            this.multiplier = multiplier;
        }

        @Override
        public void apply() {
            TreeGrowthModificationsRegistry.setMultiplier(treeName, multiplier);
        }

        @Override
        public String describe() {
            return String.format("Setting grow time multiplier of tree '%s' to: %.2f", treeName, multiplier);
        }
    }
}
