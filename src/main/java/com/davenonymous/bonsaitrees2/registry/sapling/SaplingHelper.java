package com.davenonymous.bonsaitrees2.registry.sapling;

import com.davenonymous.bonsaitrees2.registry.RecipeTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;
import java.util.stream.Collectors;

public class SaplingHelper {
    public static SaplingInfo getSaplingInfoForItem(World world, ItemStack stack) {
        List<SaplingInfo> saplings = world.getRecipeManager().getRecipes().stream().filter(r -> r.getType() == RecipeTypes.saplingRecipeType).map(r -> (SaplingInfo)r).collect(Collectors.toList());
        for(SaplingInfo sapling : saplings) {
            if(sapling.ingredient.test(stack)) {
                return sapling;
            }
        }

        return null;
    }
}
