package com.davenonymous.bonsaitrees2.compat.crafttweaker.sapling;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.impl.managers.CTCraftingTableManager;
import com.davenonymous.bonsaitrees2.block.ModObjects;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Name("mods.bonsaitrees2.Sapling")
public class Sapling {
    @ZenCodeType.Method
    public static String[] getAllIds () {
        return ModObjects.saplingRecipeHelper.getRecipeStream(CTCraftingTableManager.recipeManager).map(s -> s.getId().toString()).toArray(String[]::new);
    }

    @ZenCodeType.Method
    public static void create(String id, IIngredient ingredient, int baseTicks, String[] tags) {
        CraftTweakerAPI.apply(new SaplingActionCreate(id, ingredient, baseTicks, tags));
    }

    @ZenCodeType.Method
    public static void remove(String id) {
        CraftTweakerAPI.apply(new SaplingActionRemove(id));
    }

    @ZenCodeType.Method
    public static void setBaseTicks(String id, int baseTicks) {
        CraftTweakerAPI.apply(new SaplingActionSetBaseTicks(id, baseTicks));
    }

    @ZenCodeType.Method
    public static void addSoilTag(String id, String tag) {
        CraftTweakerAPI.apply(new SaplingActionAddTag(id, tag));
    }

    @ZenCodeType.Method
    public static void removeSoilTag(String id, String tag) {
        CraftTweakerAPI.apply(new SaplingActionRemoveTag(id, tag));
    }

    @ZenCodeType.Method
    public static void removeAllSoilTags(String id) {
        CraftTweakerAPI.apply(new SaplingActionRemoveAllTags(id));
    }

    @ZenCodeType.Method
    public static void addDrop(String id, IItemStack drop, int rolls, float chance) {
        CraftTweakerAPI.apply(new SaplingActionAddDrop(id, drop, rolls, chance));
    }

    @ZenCodeType.Method
    public static void removeDrop(String id, IItemStack drop) {
        CraftTweakerAPI.apply(new SaplingActionRemoveDrop(id, drop));
    }

    @ZenCodeType.Method
    public static void removeAllDrops(String id) {
        CraftTweakerAPI.apply(new SaplingActionRemoveAllDrops(id));
    }

    @ZenCodeType.Method
    public static void setDropChance(String id, IItemStack drop, float chance) {
        CraftTweakerAPI.apply(new SaplingDropActionSetChance(id, drop, chance));
    }

    @ZenCodeType.Method
    public static void setDropRolls(String id, IItemStack drop, int rolls) {
        CraftTweakerAPI.apply(new SaplingDropActionSetRolls(id, drop, rolls));
    }
}
