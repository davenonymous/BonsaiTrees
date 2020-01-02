package com.davenonymous.bonsaitrees2.compat.crafttweaker.sapling;

import com.blamejared.crafttweaker.api.item.IItemStack;
import com.davenonymous.bonsaitrees2.BonsaiTrees2;

public class SaplingDropActionSetRolls extends SaplingDropAction {
    private final int rolls;

    public SaplingDropActionSetRolls(String saplingId, IItemStack drop, int rolls) {
        super(saplingId, drop);
        this.rolls = rolls;
    }

    @Override
    public void apply() {
        this.saplingDrop.rolls = this.rolls;
    }

    @Override
    public String describe() {
        return String.format(
                "[%s] Changing drop rolls of '%s' in sapling '%s' from '%d' to '%d'.", BonsaiTrees2.MODID,
                this.saplingDrop.resultStack, this.saplingId, this.saplingDrop.rolls, this.rolls);
    }
}
