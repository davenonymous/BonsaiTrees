package com.davenonymous.bonsaitrees2.compat.crafttweaker.sapling;

import com.blamejared.crafttweaker.api.actions.IRuntimeAction;
import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.impl.managers.CTCraftingTableManager;
import com.davenonymous.bonsaitrees2.BonsaiTrees2;
import com.davenonymous.bonsaitrees2.block.ModObjects;
import com.davenonymous.bonsaitrees2.registry.sapling.SaplingInfo;
import com.davenonymous.libnonymous.utils.RecipeHelper;
import net.minecraft.util.ResourceLocation;

public class SaplingActionCreate implements IRuntimeAction {
    String id;
    IIngredient ingredient;
    int baseTicks;
    String[] tags;
    SaplingInfo sapling;

    public SaplingActionCreate(String id, IIngredient ingredient, int baseTicks, String[] tags) {
        this.id = id;
        this.ingredient = ingredient;
        this.baseTicks = baseTicks;
        this.tags = tags;

        this.sapling = new SaplingInfo(ResourceLocation.tryCreate(id), ingredient.asVanillaIngredient(), baseTicks);
        for(String tag : tags) {
            this.sapling.addTag(tag);
        }
    }

    @Override
    public void apply() {
        RecipeHelper.registerRecipe(CTCraftingTableManager.recipeManager, ModObjects.saplingRecipeType, this.sapling);
    }

    @Override
    public String describe() {
        return String.format("[%s] Created new sapling '%s'.", BonsaiTrees2.MODID, this.id);
    }
}
