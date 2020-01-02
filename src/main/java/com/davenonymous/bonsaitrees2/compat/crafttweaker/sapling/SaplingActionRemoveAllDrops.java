package com.davenonymous.bonsaitrees2.compat.crafttweaker.sapling;

import com.davenonymous.bonsaitrees2.BonsaiTrees2;

public class SaplingActionRemoveAllDrops extends SaplingAction {
    public SaplingActionRemoveAllDrops(String id) {
        super(id);
    }

    @Override
    public void apply() {
        this.sapling.drops.clear();
    }

    @Override
    public String describe() {
        return String.format("[%s] Removing all drops from sapling '%s'.", BonsaiTrees2.MODID, this.saplingId);
    }
}
