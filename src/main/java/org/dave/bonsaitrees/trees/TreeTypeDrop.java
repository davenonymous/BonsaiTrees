package org.dave.bonsaitrees.trees;

import net.minecraft.item.ItemStack;

public class TreeTypeDrop {
    public ItemStack stack = ItemStack.EMPTY;
    public int chance = 0;

    public TreeTypeDrop(ItemStack stack, int chance) {
        this.stack = stack;
        this.chance = chance;
    }
}
