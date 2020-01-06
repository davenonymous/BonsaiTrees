package com.davenonymous.bonsaitrees2.config;

public enum WaterLogEffect {
    NOTHING(),
    DROP_LOOT(true),
    DROP_SAPLING(true, true);

    boolean shouldDropLoot;

    WaterLogEffect() {
        this.shouldDropLoot = false;
    }

    WaterLogEffect(boolean shouldDropLoot) {
        this.shouldDropLoot = shouldDropLoot;
    }

    WaterLogEffect(boolean shouldDropLoot, boolean preventsGrowth) {
        this.shouldDropLoot = shouldDropLoot;
    }

    public boolean shouldDropLoot() {
        return shouldDropLoot;
    }
}
