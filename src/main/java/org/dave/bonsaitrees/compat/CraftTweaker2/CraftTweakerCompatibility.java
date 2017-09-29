package org.dave.bonsaitrees.compat.CraftTweaker2;

import crafttweaker.CraftTweakerAPI;
import org.dave.bonsaitrees.compat.CraftTweaker2.handlers.TreeDropHandler;

public class CraftTweakerCompatibility {
    private static boolean registered;

    public static void register() {
        if (registered) {
            return;
        }

        registered = true;

        CraftTweakerAPI.registerClass(TreeDropHandler.class);
    }
}
