package com.davenonymous.bonsaitrees2.compat.crafttweaker.sapling;

import com.blamejared.crafttweaker.api.item.IItemStack;
import com.davenonymous.bonsaitrees2.BonsaiTrees2;

public class SaplingDropActionSetChance extends SaplingDropAction {
    private final float chance;
    public SaplingDropActionSetChance(String saplingId, IItemStack drop, float chance) {
        super(saplingId, drop);
        this.chance = chance;
    }

    @Override
    public void apply() {
        this.saplingDrop.chance = this.chance;
    }

    @Override
    public String describe() {
        return String.format(
                "[%s] Changing drop chance of '%s' in sapling '%s' from '%.2f' to '%.2f'.", BonsaiTrees2.MODID,
                this.saplingDrop.resultStack, this.saplingId, this.saplingDrop.chance, this.chance);
    }
}
