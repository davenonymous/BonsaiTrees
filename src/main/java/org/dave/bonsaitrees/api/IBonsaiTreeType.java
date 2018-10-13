package org.dave.bonsaitrees.api;

import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.Set;

public interface IBonsaiTreeType {
    String getName();
    List<TreeTypeDrop> getDrops();

    boolean worksWith(ItemStack stack);
    ItemStack getExampleStack();

    Set<String> getCompatibleSoilTags();
}
