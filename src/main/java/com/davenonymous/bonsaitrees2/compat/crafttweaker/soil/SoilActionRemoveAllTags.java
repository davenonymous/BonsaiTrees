package com.davenonymous.bonsaitrees2.compat.crafttweaker.soil;

import com.davenonymous.bonsaitrees2.BonsaiTrees2;

public class SoilActionRemoveAllTags extends SoilAction {
    public SoilActionRemoveAllTags(String soilId) {
        super(soilId);
    }

    @Override
    public void apply() {
        this.soil.tags.clear();
    }

    @Override
    public String describe() {
        return String.format("[%s] Removed all tags from soil '%s'.", BonsaiTrees2.MODID, this.soilId);
    }
}
