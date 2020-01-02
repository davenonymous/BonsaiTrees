package com.davenonymous.bonsaitrees2.registry.soil;

import com.davenonymous.bonsaitrees2.registry.RecipeTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SoilHelper {
    public static SoilInfo getSoil(RecipeManager recipeManager, ResourceLocation id) {
        return getSoils(recipeManager).filter(s -> s.getId().equals(id)).findFirst().orElse(null);
    }

    public static Stream<SoilInfo> getSoils(RecipeManager recipeManager) {
        return recipeManager.getRecipes().stream().filter(r -> r.getType() == RecipeTypes.soilRecipeType).map(r -> (SoilInfo)r);
    }

    public static SoilInfo getRandomSoil(World world) {
        List<SoilInfo> soils = getSoils(world.getRecipeManager()).collect(Collectors.toList());
        int randomSlot = world.rand.nextInt(soils.size());
        return soils.get(randomSlot);
    }

    public static SoilInfo getSoilForItem(World world, ItemStack stack) {
        List<SoilInfo> soils = getSoils(world.getRecipeManager()).collect(Collectors.toList());
        for(SoilInfo soil : soils) {
            if(soil.ingredient.test(stack)) {
                return soil;
            }
        }

        return null;
    }
}
