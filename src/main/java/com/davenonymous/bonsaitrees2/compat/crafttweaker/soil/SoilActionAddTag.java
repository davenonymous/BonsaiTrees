package com.davenonymous.bonsaitrees2.compat.crafttweaker.soil;

import com.davenonymous.bonsaitrees2.BonsaiTrees2;

public class SoilActionAddTag extends SoilAction {
    String tag;

    public SoilActionAddTag(String soilId, String tag) {
        super(soilId);
        this.tag = tag;
    }

    @Override
    public void apply() {
        this.soil.tags.add(this.tag);
    }

    @Override
    public String describe() {
        return String.format("[%s] Adding tag '%s' to soil '%s'.", BonsaiTrees2.MODID, this.tag, this.soilId);
    }
}
