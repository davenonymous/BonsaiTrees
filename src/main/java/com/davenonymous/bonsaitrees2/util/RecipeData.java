package com.davenonymous.bonsaitrees2.util;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

/*
This class is basically taken as is from
https://github.com/Darkhax-Minecraft/BotanyPots/blob/6aa8b1dbeb/src/main/java/net/darkhax/botanypots/RecipeData.java

Thanks @Darkhax
 */
public abstract class RecipeData implements IRecipe<IInventory> {
    public RecipeData() {
        if (this.getSerializer() == null) {
            throw new IllegalStateException("No serializer found for " + this.getClass().getName());
        }

        if (this.getType() == null) {
            throw new IllegalStateException("No recipe type found for " + this.getClass().getName());
        }
    }

    @Override
    public boolean matches (IInventory inv, World worldIn) {
        // Not used
        return false;
    }

    @Override
    public ItemStack getCraftingResult (IInventory inv) {
        // Not used
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canFit (int width, int height) {
        // Not used
        return false;
    }

    @Override
    public ItemStack getRecipeOutput () {
        // Not used
        return ItemStack.EMPTY;
    }
}
