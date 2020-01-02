package com.davenonymous.bonsaitrees2.compat.crafttweaker.sapling;

import com.davenonymous.bonsaitrees2.BonsaiTrees2;

public class SaplingActionSetBaseTicks extends SaplingAction {
    private final int baseTicks;

    public SaplingActionSetBaseTicks(String id, int baseTicks) {
        super(id);
        this.baseTicks = baseTicks;
    }

    @Override
    public void apply() {
        this.sapling.baseTicks = this.baseTicks;
    }

    @Override
    public String describe() {
        return String.format("[%s] Changing base ticks of '%s' from '%d' to '%d'.", BonsaiTrees2.MODID, this.saplingId, this.sapling.baseTicks, this.baseTicks);
    }
}
