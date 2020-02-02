package com.davenonymous.bonsaitrees2.compat.crafttweaker.sapling;

import com.blamejared.crafttweaker.api.actions.IRuntimeAction;
import com.blamejared.crafttweaker.api.logger.ILogger;
import com.blamejared.crafttweaker.impl.managers.CTCraftingTableManager;
import com.davenonymous.bonsaitrees2.BonsaiTrees2;
import com.davenonymous.bonsaitrees2.block.ModObjects;
import com.davenonymous.bonsaitrees2.registry.sapling.SaplingInfo;
import net.minecraft.util.ResourceLocation;

public abstract class SaplingAction implements IRuntimeAction {
    public String saplingId;
    public SaplingInfo sapling;

    public SaplingAction(String saplingId) {
        this.saplingId = saplingId;
        this.sapling = ModObjects.saplingRecipeHelper.getRecipe(CTCraftingTableManager.recipeManager, ResourceLocation.tryCreate(saplingId));
    }

    @Override
    public boolean validate(ILogger logger) {
        if(this.sapling == null) {
            logger.error(String.format("[%s] No sapling found for provided id '%s'.", BonsaiTrees2.MODID, this.saplingId));
            return false;
        }

        return true;
    }
}
