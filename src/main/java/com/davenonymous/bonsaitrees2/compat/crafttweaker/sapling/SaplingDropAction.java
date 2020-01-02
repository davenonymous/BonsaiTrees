package com.davenonymous.bonsaitrees2.compat.crafttweaker.sapling;

import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.logger.ILogger;
import com.davenonymous.bonsaitrees2.BonsaiTrees2;
import com.davenonymous.bonsaitrees2.registry.sapling.SaplingDrop;

public abstract class SaplingDropAction extends SaplingAction {
    protected final IItemStack drop;
    public SaplingDrop saplingDrop;

    public SaplingDropAction(String saplingId, IItemStack drop) {
        super(saplingId);
        this.drop = drop;
        if(this.sapling != null) {
            this.saplingDrop = this.sapling.drops.stream().filter(d -> d.resultStack.isItemEqual(this.drop.getInternal())).findFirst().orElse(null);
        }
    }

    @Override
    public boolean validate(ILogger logger) {
        boolean success = super.validate(logger);
        if(this.saplingDrop == null) {
            logger.error(String.format("[%s] No matching drop '%s' found for sapling '%s'.", BonsaiTrees2.MODID, this.drop.getDisplayName(), this.saplingId));
            success = false;
        }

        return success;
    }
}
