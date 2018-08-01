package org.dave.bonsaitrees.compat.CraftTweaker2.handlers;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.item.IItemStack;
import org.dave.bonsaitrees.compat.CraftTweaker2.CraftTweakerUtils;
import org.dave.bonsaitrees.compat.CraftTweaker2.registries.TreeDropModificationsRegistry;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.bonsaitrees.TreeDrops")
public class TreeDropHandler {
    @ZenMethod
    public static void addTreeDrop(String treeName, IItemStack input, float chance) {
        CraftTweakerAPI.apply(new AddTreeDrop(treeName, input, chance));
    }

    @ZenMethod
    public static void removeTreeDrop(String treeName, IItemStack input) {
        CraftTweakerAPI.apply(new RemoveTreeDrop(treeName, input));
    }

    private static class AddTreeDrop implements IAction {
        String treeName;
        IItemStack input;
        float chance;

        public AddTreeDrop(String treeName, IItemStack input, float chance) {
            this.treeName = treeName;
            this.input = input;
            this.chance = chance;
        }

        @Override
        public void apply() {
            TreeDropModificationsRegistry.add(treeName, CraftTweakerUtils.getItemStack(input), chance);
        }

        @Override
        public String describe() {
            return "Adding '" + input.getDisplayName() + "' drop to tree type '" + treeName + "'";
        }
    }

    private static class RemoveTreeDrop implements IAction {
        String treeName;
        IItemStack input;

        public RemoveTreeDrop(String treeName, IItemStack input) {
            this.treeName = treeName;
            this.input = input;
        }

        @Override
        public void apply() {
            TreeDropModificationsRegistry.remove(treeName, CraftTweakerUtils.getItemStack(input));
        }

        @Override
        public String describe() {
            return "Removing '" + input.getDisplayName() + "' drop from tree type '" + treeName + "'";
        }
    }
}
