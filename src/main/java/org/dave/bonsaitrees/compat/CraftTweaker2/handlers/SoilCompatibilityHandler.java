package org.dave.bonsaitrees.compat.CraftTweaker2.handlers;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import org.dave.bonsaitrees.compat.CraftTweaker2.registries.TagModificationsRegistry;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.Locale;

@ZenClass("mods.bonsaitrees.SoilCompatibility")
public class SoilCompatibilityHandler {
    @ZenMethod
    public static void addCompatibleTagToTree(String treeName, String tag) {
        TagModificationAction.apply(0, treeName, tag);
    }

    @ZenMethod
    public static void removeCompatibleTagFromTree(String treeName, String tag) {
        TagModificationAction.apply(1, treeName, tag);
    }

    @ZenMethod
    public static void addProvidedTagToSoil(String soilName, String tag) {
        TagModificationAction.apply(2, soilName, tag);
    }

    @ZenMethod
    public static void removeProvidedTagFromSoil(String soilName, String tag) {
        TagModificationAction.apply(3, soilName, tag);
    }

    private static class TagModificationAction implements IAction {
        int action;
        String objName;
        String tag;

        public TagModificationAction(int action, String objName, String tag) {
            this.action = action;
            this.objName = objName;
            this.tag = tag;
        }

        public static void apply(int action, String objName, String tag) {
            CraftTweakerAPI.apply(new TagModificationAction(action, objName, tag));
        }

        @Override
        public void apply() {
            if(action == 0) {
                TagModificationsRegistry.addCompatibleTagToTree(objName, tag);
            } else if(action == 1) {
                TagModificationsRegistry.removeCompatibleTagFromTree(objName, tag);
            } else if(action == 2) {
                TagModificationsRegistry.addProvidedTagToSoil(objName, tag);
            } else if(action == 3) {
                TagModificationsRegistry.removeProvidedTagFromSoil(objName, tag);
            }
        }

        @Override
        public String describe() {
            String verb = action == 0 || action == 2 ? "Adding" : "Removing";
            String subject = action == 0 || action == 1 ? "tree" : "soil";

            return String.format(Locale.ENGLISH, "%s tag '%s' to %s '%s'", verb, tag, subject, objName);
        }
    }
}
