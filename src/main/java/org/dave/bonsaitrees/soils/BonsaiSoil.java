package org.dave.bonsaitrees.soils;

import net.minecraft.item.ItemStack;
import org.dave.bonsaitrees.api.IBonsaiSoil;

import java.util.HashSet;
import java.util.Set;

public class BonsaiSoil implements IBonsaiSoil {
    private String name;
    private ItemStack soilStack;

    private float modifierSpeed = 1.0f;
    private float modifierDropChance = 1.0f;

    private Set<String> providedTags;

    private boolean ignoreMeta = false;

    public BonsaiSoil(String name, ItemStack soilStack) {
        this.name = name;
        this.soilStack = soilStack;
        this.providedTags = new HashSet<>();
    }

    public void setModifierSpeed(float modifierSpeed) {
        this.modifierSpeed = modifierSpeed;
    }

    public void setModifierDropChance(float modifierDropChance) {
        this.modifierDropChance = modifierDropChance;
    }

    public void setIgnoreMeta(boolean ignoreMeta) {
        this.ignoreMeta = ignoreMeta;
    }

    public void addProvidedTag(String tag) {
        this.providedTags.add(tag);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ItemStack getSoilStack() {
        return soilStack.copy();
    }

    @Override
    public float getModifierGrowTime() {
        return modifierSpeed;
    }

    @Override
    public float getModifierDropChance() {
        return modifierDropChance;
    }

    @Override
    public Set<String> getProvidedTags() {
        return providedTags;
    }

    @Override
    public boolean ignoreMeta() {
        return ignoreMeta;
    }
}
