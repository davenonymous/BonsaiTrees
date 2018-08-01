package org.dave.bonsaitrees.compat.CraftTweaker2.handlers;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import org.dave.bonsaitrees.compat.CraftTweaker2.registries.SoilStatsModificationsRegistry;
import org.dave.bonsaitrees.soils.EnumSoilStat;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.Locale;

@ZenClass("mods.bonsaitrees.SoilStats")
public class SoilStatsHandler {
    @ZenMethod
    public static void setGrowTimeModifier(String soilName, float modifier) {
        CraftTweakerAPI.apply(new StatModificationAction(EnumSoilStat.GROW_TIME, soilName, modifier));
    }

    @ZenMethod
    public static void setDropChanceModifier(String soilName, float modifier) {
        CraftTweakerAPI.apply(new StatModificationAction(EnumSoilStat.DROP_CHANCE, soilName, modifier));
    }

    private static class StatModificationAction implements IAction {
        EnumSoilStat type;
        String soilName;
        float modifier;

        public StatModificationAction(EnumSoilStat type, String soilName, float modifier) {
            this.type = type;
            this.soilName = soilName;
            this.modifier = modifier;
        }

        @Override
        public void apply() {
            SoilStatsModificationsRegistry.setMultiplier(type, this.soilName, this.modifier);
        }

        @Override
        public String describe() {
            return String.format(Locale.ENGLISH, "Setting '%s' modifier for soil '%s' to %.2f", type.getHumanName(), soilName, modifier);
        }
    }
}
