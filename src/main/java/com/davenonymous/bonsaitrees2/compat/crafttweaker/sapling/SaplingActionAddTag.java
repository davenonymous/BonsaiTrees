package com.davenonymous.bonsaitrees2.compat.crafttweaker.sapling;

import com.davenonymous.bonsaitrees2.BonsaiTrees2;

public class SaplingActionAddTag extends SaplingAction {
    String tag;

    public SaplingActionAddTag(String id, String tag) {
        super(id);
        this.tag = tag;
    }


    @Override
    public void apply() {
        this.sapling.addTag(this.tag);
    }

    @Override
    public String describe() {
        return String.format("[%s] Adding tag '%s' to sapling '%s'.", BonsaiTrees2.MODID, this.tag, this.saplingId);
    }
}
