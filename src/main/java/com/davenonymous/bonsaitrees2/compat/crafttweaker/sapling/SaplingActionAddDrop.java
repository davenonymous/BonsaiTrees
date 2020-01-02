package com.davenonymous.bonsaitrees2.compat.crafttweaker.sapling;

import com.blamejared.crafttweaker.api.item.IItemStack;
import com.davenonymous.bonsaitrees2.BonsaiTrees2;
import com.davenonymous.bonsaitrees2.registry.sapling.SaplingDrop;

public class SaplingActionAddDrop extends SaplingAction {
    protected final IItemStack drop;
    private final int rolls;
    private final float chance;

    public SaplingActionAddDrop(String id, IItemStack drop, int rolls, float chance) {
        super(id);

        this.drop = drop;
        this.rolls = rolls;
        this.chance = chance;
    }

    @Override
    public void apply() {
        SaplingDrop drop = new SaplingDrop(this.drop.getInternal(), this.chance, this.rolls);
        this.sapling.addDrop(drop);
    }

    @Override
    public String describe() {
        return String.format("[%s] Adding drop '%s' to sapling '%s'.", BonsaiTrees2.MODID, this.drop.getInternal().toString(), this.saplingId);
    }
}
