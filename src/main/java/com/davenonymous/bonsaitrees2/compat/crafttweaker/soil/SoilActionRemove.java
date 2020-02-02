package com.davenonymous.bonsaitrees2.compat.crafttweaker.soil;

import com.blamejared.crafttweaker.api.actions.IRuntimeAction;
import com.blamejared.crafttweaker.impl.managers.CTCraftingTableManager;
import com.davenonymous.bonsaitrees2.BonsaiTrees2;
import com.davenonymous.bonsaitrees2.block.ModObjects;
import com.davenonymous.libnonymous.utils.RecipeHelper;

public class SoilActionRemove implements IRuntimeAction {
    String id;

    public SoilActionRemove(String id) {
        this.id = id;
    }

    @Override
    public void apply() {
        RecipeHelper.removeRecipe(CTCraftingTableManager.recipeManager, ModObjects.soilRecipeType, this.id);
    }

    @Override
    public String describe() {
        return String.format("[%s] Removed soil with id '%s'.", BonsaiTrees2.MODID, this.id);
    }
}
