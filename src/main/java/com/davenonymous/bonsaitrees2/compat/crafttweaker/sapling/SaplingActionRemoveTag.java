package com.davenonymous.bonsaitrees2.compat.crafttweaker.sapling;

import com.davenonymous.bonsaitrees2.BonsaiTrees2;

public class SaplingActionRemoveTag extends SaplingAction {
    String tag;

    public SaplingActionRemoveTag(String id, String tag) {
        super(id);
        this.tag = tag;
    }

    @Override
    public void apply() {
        this.sapling.tags.remove(this.tag);
    }

    @Override
    public String describe() {
        return String.format("[%s] Removing tag '%s' from sapling '%s'.", BonsaiTrees2.MODID, this.tag, this.saplingId);
    }
}
