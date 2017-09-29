package org.dave.bonsaitrees.compat.CraftTweaker2;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.item.IIngredient;
import net.minecraft.item.ItemStack;

public class CraftTweakerUtils {
    public static ItemStack getItemStack(IIngredient item) {
        if(item == null)
            return ItemStack.EMPTY;

        Object internal = item.getInternal();
        if (!(internal instanceof ItemStack)) {
            CraftTweakerAPI.logError("Not a valid item stack: " + item);
            return ItemStack.EMPTY;
        }
        return (ItemStack) internal;
    }
}
