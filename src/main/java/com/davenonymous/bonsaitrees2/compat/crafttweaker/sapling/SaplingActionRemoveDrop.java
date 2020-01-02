package com.davenonymous.bonsaitrees2.compat.crafttweaker.sapling;

import com.blamejared.crafttweaker.api.item.IItemStack;
import com.davenonymous.bonsaitrees2.BonsaiTrees2;
import com.davenonymous.bonsaitrees2.registry.sapling.SaplingDrop;

import java.util.List;
import java.util.stream.Collectors;

public class SaplingActionRemoveDrop extends SaplingAction {
    private List<SaplingDrop> originalDrops;
    private IItemStack drop;

    public SaplingActionRemoveDrop(String id, IItemStack drop) {
        super(id);

        this.drop = drop;
        if(this.sapling != null) {
            this.originalDrops = this.sapling.drops.stream().filter(d -> d.resultStack.isItemEqual(drop.getInternal())).collect(Collectors.toList());
        }
    }

    @Override
    public void apply() {
        for(SaplingDrop drop : this.originalDrops) {
            this.sapling.drops.remove(drop);
        }
    }

    @Override
    public String describe() {
        return String.format("[%s] Removing %d drops matching '%s' from sapling '%s'.", BonsaiTrees2.MODID, this.originalDrops.size(), this.drop.getInternal().toString(), this.saplingId);
    }
}
