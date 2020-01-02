package com.davenonymous.bonsaitrees2.compat.crafttweaker.soil;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.impl.blocks.MCBlockState;
import com.blamejared.crafttweaker.impl.managers.CTCraftingTableManager;
import com.davenonymous.bonsaitrees2.registry.soil.SoilHelper;
import com.davenonymous.bonsaitrees2.registry.soil.SoilInfo;
import net.minecraft.util.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Name("mods.bonsaitrees2.Soil")
public class Soil {
    @ZenCodeType.Method
    public static void create(String id, IIngredient ingredient, MCBlockState displayState, float tickModifier, String[] tags) {
        CraftTweakerAPI.apply(new SoilActionCreate(id, ingredient, displayState, tickModifier, tags));
    }

    @ZenCodeType.Method
    public static void remove(String id) {
        CraftTweakerAPI.apply(new SoilActionRemove(id));
    }

    @ZenCodeType.Method
    public static void setTickModifier(String id, float modifier) {
        CraftTweakerAPI.apply(new SoilActionSetTickModifier(id, modifier));
    }

    @ZenCodeType.Method
    public static void addSoilTag(String id, String tag) {
        CraftTweakerAPI.apply(new SoilActionAddTag(id, tag));
    }

    @ZenCodeType.Method
    public static void removeSoilTag(String id, String tag) {
        CraftTweakerAPI.apply(new SoilActionRemoveTag(id, tag));
    }

    @ZenCodeType.Method
    public static void removeAllSoilTags(String id) {
        CraftTweakerAPI.apply(new SoilActionRemoveAllTags(id));
    }

    @ZenCodeType.Method
    public static String[] getTags(String id) {
        SoilInfo soil = SoilHelper.getSoil(CTCraftingTableManager.recipeManager, ResourceLocation.tryCreate(id));
        if(soil == null) {
            return null;
        }

        String[] tags = new String[soil.tags.size()];
        soil.tags.toArray(tags);
        return tags;
    }

    @ZenCodeType.Method
    public static String[] getAllIds () {
        String[] result = SoilHelper.getSoils(CTCraftingTableManager.recipeManager).map(s -> s.getId().toString()).toArray(String[]::new);

        return result;
    }
}
