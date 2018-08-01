package org.dave.bonsaitrees.api;

import net.minecraft.item.ItemStack;

import java.util.Set;

public interface IBonsaiSoil {
    String getName();
    ItemStack getSoilStack();
    boolean ignoreMeta();

    default boolean matchesStack(ItemStack stack) {
        ItemStack soilStack = this.getSoilStack();
        if(soilStack.getItem() != stack.getItem()) {
            return false;
        }

        if(!this.ignoreMeta() && soilStack.getMetadata() != stack.getMetadata()) {
            return false;
        }

        return true;
    }

    float getModifierGrowTime();
    float getModifierDropChance();

    Set<String> getProvidedTags();
}
