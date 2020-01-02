package com.davenonymous.bonsaitrees2.compat.crafttweaker.soil;

import com.davenonymous.bonsaitrees2.BonsaiTrees2;

public class SoilActionRemoveTag extends SoilAction  {
    String tag;

    public SoilActionRemoveTag(String soilId, String tag) {
        super(soilId);
        this.tag = tag;
    }

    @Override
    public void apply() {
        this.soil.tags.remove(this.tag);
    }

    @Override
    public String describe() {
        return String.format("[%s] Removing tag '%s' from soil '%s'.", BonsaiTrees2.MODID, this.tag, this.soilId);
    }
}
