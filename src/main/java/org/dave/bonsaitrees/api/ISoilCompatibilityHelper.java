package org.dave.bonsaitrees.api;

import net.minecraft.item.ItemStack;

import java.util.Set;

public interface ISoilCompatibilityHelper {
    Set<IBonsaiSoil> getValidSoilsForTree(IBonsaiTreeType tree);
    Set<IBonsaiTreeType> getValidTreesForSoil(IBonsaiSoil soil);
    boolean canTreeGrowOnSoil(IBonsaiTreeType tree, IBonsaiSoil soil);

    boolean isValidSoil(ItemStack soilStack);

}
