package com.davenonymous.bonsaitrees2.registry.sapling;

import com.davenonymous.bonsaitrees2.registry.RecipeTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SaplingHelper {
    public static SaplingInfo getSapling(RecipeManager recipeManager, ResourceLocation id) {
        return getSaplings(recipeManager).filter(s -> s.getId().equals(id)).findFirst().orElse(null);
    }

    public static Stream<SaplingInfo> getSaplings(RecipeManager recipeManager) {
        return recipeManager.getRecipes().stream().filter(r -> r.getType() == RecipeTypes.saplingRecipeType).map(r -> (SaplingInfo)r);
    }

    public static SaplingInfo getSaplingInfoForItem(World world, ItemStack stack) {
        List<SaplingInfo> saplings =getSaplings(world.getRecipeManager()).collect(Collectors.toList());
        for(SaplingInfo sapling : saplings) {
            if(sapling.ingredient.test(stack)) {
                return sapling;
            }
        }

        return null;
    }
}
