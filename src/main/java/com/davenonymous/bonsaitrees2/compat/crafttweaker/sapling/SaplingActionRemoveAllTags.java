package com.davenonymous.bonsaitrees2.compat.crafttweaker.sapling;

import com.davenonymous.bonsaitrees2.BonsaiTrees2;

public class SaplingActionRemoveAllTags extends SaplingAction {

    public SaplingActionRemoveAllTags(String id) {
        super(id);
    }

    @Override
    public void apply() {
        this.sapling.tags.clear();
    }

    @Override
    public String describe() {
        return String.format("[%s] Removing all soil tag from sapling '%s'.", BonsaiTrees2.MODID, this.saplingId);
    }
}
