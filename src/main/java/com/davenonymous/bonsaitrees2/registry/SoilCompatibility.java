package com.davenonymous.bonsaitrees2.registry;

import com.davenonymous.bonsaitrees2.block.ModObjects;
import com.davenonymous.bonsaitrees2.registry.sapling.SaplingInfo;
import com.davenonymous.bonsaitrees2.registry.soil.SoilInfo;
import com.davenonymous.bonsaitrees2.util.Logz;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

import java.util.*;
import java.util.stream.Collectors;

public class SoilCompatibility {
    public static final SoilCompatibility INSTANCE = new SoilCompatibility();

    private Map<SoilInfo, Set<SaplingInfo>> treeCompatibility;
    private Map<SaplingInfo, Set<SoilInfo>> soilCompatibility;

    private void addCompatEntry(SoilInfo soil, SaplingInfo tree) {
        if(!soilCompatibility.containsKey(tree)) {
            soilCompatibility.put(tree, new HashSet<>());
        }

        soilCompatibility.get(tree).add(soil);

        if(!treeCompatibility.containsKey(soil)) {
            treeCompatibility.put(soil, new HashSet<>());
        }

        treeCompatibility.get(soil).add(tree);
    }


    public Set<SoilInfo> getValidSoilsForSapling(SaplingInfo sapling) {
        return soilCompatibility.getOrDefault(sapling, new HashSet<>());
    }

    public boolean canTreeGrowOnSoil(SaplingInfo sapling, SoilInfo soil) {
        if(!soilCompatibility.containsKey(sapling) || soilCompatibility.get(sapling) == null) {
            return false;
        }

        return soilCompatibility.get(sapling).contains(soil);
    }

    public boolean isValidSoil(ItemStack soilStack) {
        for(SoilInfo soil : treeCompatibility.keySet()) {
            if(soil.ingredient.test(soilStack)) {
                return true;
            }
        }

        return false;
    }

    public void update(Collection<IRecipe<?>> recipes) {
        if(recipes == null || recipes.size() <= 0) {
            return;
        }

        List<SaplingInfo> saplings = recipes.stream().filter(r -> r.getType() == ModObjects.saplingRecipeType).map(r -> (SaplingInfo)r).collect(Collectors.toList());
        List<SoilInfo> soils = recipes.stream().filter(r -> r.getType() == ModObjects.soilRecipeType).map(r -> (SoilInfo)r).collect(Collectors.toList());

        treeCompatibility = new HashMap<>();
        soilCompatibility = new HashMap<>();

        Map<String, Set<SoilInfo>> reverseSoilTagMap = new HashMap<>();
        for(SoilInfo soil : soils) {
            for(String tag : soil.tags) {
                if(!reverseSoilTagMap.containsKey(tag)) {
                    reverseSoilTagMap.put(tag, new HashSet<>());
                }

                reverseSoilTagMap.get(tag).add(soil);
            }
        }

        for(SaplingInfo sapling : saplings) {
            for(String tag : sapling.tags) {
                if(!reverseSoilTagMap.containsKey(tag)) {
                    continue;
                }

                for(SoilInfo soil : reverseSoilTagMap.get(tag)) {
                    Logz.debug("Tree '{}' grows on '{}' because of '{}'", sapling.getId(), soil.getId(), tag);
                    this.addCompatEntry(soil, sapling);
                }
            }
        }

        Logz.info("Updated soil compatibility");
    }
}
