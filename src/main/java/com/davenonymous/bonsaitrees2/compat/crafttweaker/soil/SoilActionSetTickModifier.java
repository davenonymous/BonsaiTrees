package com.davenonymous.bonsaitrees2.compat.crafttweaker.soil;

import com.davenonymous.bonsaitrees2.BonsaiTrees2;

public class SoilActionSetTickModifier extends SoilAction {
    private final float tickModifier;

    public SoilActionSetTickModifier(String soilId, float tickModifier) {
        super(soilId);
        this.tickModifier = tickModifier;
    }

    @Override
    public void apply() {
        this.soil.tickModifier = this.tickModifier;
    }

    @Override
    public String describe() {
        return String.format("[%s] Changing soil tickModifier of '%s' from '%.2f' to '%.2f'", BonsaiTrees2.MODID, this.soilId, this.soil.tickModifier, this.tickModifier);
    }
}
