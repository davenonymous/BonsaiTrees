package org.dave.bonsaitrees.compat.CraftTweaker2.registries;

import org.dave.bonsaitrees.api.IBonsaiSoil;
import org.dave.bonsaitrees.soils.EnumSoilStat;

import java.util.HashMap;
import java.util.Map;

public class SoilStatsModificationsRegistry {
    //                Stat        Soil    Multiplier
    public static Map<EnumSoilStat, Map<String, Float>> multipliers = new HashMap<>();

    public static void setMultiplier(EnumSoilStat stat, String soilName, float multiplier) {
        if(!multipliers.containsKey(stat)) {
            multipliers.put(stat, new HashMap<>());
        }

        multipliers.get(stat).put(soilName, multiplier);
    }

    private static float maybeReplaceModifiers(float baseValue, EnumSoilStat type, String soilName) {
        if(multipliers == null || multipliers.get(type) == null) {
            return baseValue;
        }

        return multipliers.get(type).getOrDefault(soilName, baseValue);
    }

    public static float getModifiedGrowTimeModifier(IBonsaiSoil soil) {
        return maybeReplaceModifiers(soil.getModifierGrowTime(), EnumSoilStat.GROW_TIME, soil.getName());
    }

    public static float getModifiedDropChanceModifier(IBonsaiSoil soil) {
        return maybeReplaceModifiers(soil.getModifierDropChance(), EnumSoilStat.DROP_CHANCE, soil.getName());
    }
}
