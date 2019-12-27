package com.davenonymous.bonsaitrees2.registry.soil;

import com.davenonymous.bonsaitrees2.registry.RecipeTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;
import java.util.stream.Collectors;

public class SoilHelper {
    public static SoilInfo getRandomSoil(World world) {
        List<SoilInfo> soils = world.getRecipeManager().getRecipes().stream().filter(r -> r.getType() == RecipeTypes.soilRecipeType).map(r -> (SoilInfo)r).collect(Collectors.toList());
        int randomSlot = world.rand.nextInt(soils.size());
        return soils.get(randomSlot);
    }

    public static SoilInfo getSoilForItem(World world, ItemStack stack) {
        List<SoilInfo> soils = world.getRecipeManager().getRecipes().stream().filter(r -> r.getType() == RecipeTypes.soilRecipeType).map(r -> (SoilInfo)r).collect(Collectors.toList());
        for(SoilInfo soil : soils) {
            if(soil.ingredient.test(stack)) {
                return soil;
            }
        }

        return null;
    }
}
