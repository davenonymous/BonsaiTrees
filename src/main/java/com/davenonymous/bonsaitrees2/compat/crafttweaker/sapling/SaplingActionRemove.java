package com.davenonymous.bonsaitrees2.compat.crafttweaker.sapling;

import com.blamejared.crafttweaker.api.actions.IRuntimeAction;
import com.blamejared.crafttweaker.impl.managers.CTCraftingTableManager;
import com.davenonymous.bonsaitrees2.BonsaiTrees2;
import com.davenonymous.bonsaitrees2.registry.RecipeTypes;
import com.davenonymous.libnonymous.utils.RecipeHelper;

public class SaplingActionRemove implements IRuntimeAction {
    String id;

    public SaplingActionRemove(String id) {
        this.id = id;
    }

    @Override
    public void apply() {
        RecipeHelper.removeRecipe(CTCraftingTableManager.recipeManager, RecipeTypes.saplingRecipeType, this.id);
    }

    @Override
    public String describe() {
        return String.format("[%s] Removed sapling with id '%s'.", BonsaiTrees2.MODID, this.id);
    }
}
