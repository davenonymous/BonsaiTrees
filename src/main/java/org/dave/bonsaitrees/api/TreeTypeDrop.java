package org.dave.bonsaitrees.api;

import net.minecraft.item.ItemStack;

public class TreeTypeDrop {
    public ItemStack stack = ItemStack.EMPTY;
    public float chance = 0.0f;

    public TreeTypeDrop(ItemStack stack, float chance) {
        this.stack = stack;
        this.chance = chance;
    }
}
