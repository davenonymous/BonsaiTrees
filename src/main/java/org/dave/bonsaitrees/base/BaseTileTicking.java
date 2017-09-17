package org.dave.bonsaitrees.base;

import net.minecraft.util.ITickable;

public class BaseTileTicking extends BaseTile implements ITickable {
    private boolean initialized = false;

    private void initialize() {
    }

    @Override
    public void update() {
        if (!this.getWorld().isRemote && !this.initialized && !this.isInvalid()) {
            initialize();
            this.initialized = true;
        }
    }
}