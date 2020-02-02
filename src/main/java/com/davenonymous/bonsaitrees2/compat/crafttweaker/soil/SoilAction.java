package com.davenonymous.bonsaitrees2.compat.crafttweaker.soil;

import com.blamejared.crafttweaker.api.actions.IRuntimeAction;
import com.blamejared.crafttweaker.api.logger.ILogger;
import com.blamejared.crafttweaker.impl.managers.CTCraftingTableManager;
import com.davenonymous.bonsaitrees2.block.ModObjects;
import com.davenonymous.bonsaitrees2.registry.soil.SoilInfo;
import net.minecraft.util.ResourceLocation;

public abstract class SoilAction implements IRuntimeAction {
    public String soilId;
    public SoilInfo soil;

    public SoilAction(String soilId) {
        this.soilId = soilId;

        ResourceLocation soilResource = ResourceLocation.tryCreate(soilId);
        this.soil = ModObjects.soilRecipeHelper.getRecipe(CTCraftingTableManager.recipeManager, soilResource);
    }

    @Override
    public boolean validate(ILogger logger) {
        if(this.soil == null) {
            logger.error("[BonsaiTrees2] No soil found for provided saplingId " + this.soilId + ".");
            return false;
        }

        return true;
    }
}
