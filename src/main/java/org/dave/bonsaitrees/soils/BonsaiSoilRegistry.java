package org.dave.bonsaitrees.soils;

import net.minecraft.item.ItemStack;
import org.dave.bonsaitrees.api.IBonsaiIntegration;
import org.dave.bonsaitrees.api.IBonsaiSoil;
import org.dave.bonsaitrees.api.IBonsaiSoilRegistry;
import org.dave.bonsaitrees.misc.ConfigurationHandler;
import org.dave.bonsaitrees.utility.Logz;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BonsaiSoilRegistry implements IBonsaiSoilRegistry {
    private Map<String, IBonsaiSoil> soils = new HashMap<>();
    private Map<IBonsaiSoil, IBonsaiIntegration> integrationMap = new HashMap<>();

    public void clear() {
        soils = new HashMap<>();
        integrationMap = new HashMap<>();
    }

    @Override
    public void registerBonsaiSoilIntegration(IBonsaiIntegration integrator, IBonsaiSoil soil) {
        if(Arrays.asList(ConfigurationHandler.IntegrationSettings.disabledSoils).contains(soil.getName())) {
            Logz.info("Soil %s has been disabled via config. Skipping...", soil.getName());
            return;
        }

        if(soils.containsKey(soil.getName())) {
            Logz.info("Soil %s has already been loaded before. Skipping...", soil.getName());
            return;
        }

        if(soil.getSoilStack().isEmpty()) {
            Logz.info("Soil %s has no soil stack. Skipping...", soil.getName());
            return;
        }

        soils.put(soil.getName(), soil);
        integrationMap.put(soil, integrator);
    }

    public boolean isValidSoil(ItemStack stack) {
        return getTypeByStack(stack) != null;
    }

    public Collection<IBonsaiSoil> getAllSoils() {
        return soils.values();
    }

    public IBonsaiSoil getTypeByName(String name) {
        return soils.get(name);
    }

    public IBonsaiSoil getTypeByStack(ItemStack stack) {
        if(stack == null || stack.isEmpty()) {
            return null;
        }

        for(IBonsaiSoil soil : soils.values()) {
            if(soil.matchesStack(stack)) {
                return soil;
            }
        }

        return null;
    }

    public IBonsaiIntegration getIntegrationForSoil(IBonsaiSoil soil) {
        return integrationMap.get(soil);
    }
}
