package com.davenonymous.bonsaitrees2.registry;

import com.davenonymous.bonsaitrees2.BonsaiTrees2;
import com.davenonymous.bonsaitrees2.registry.sapling.SaplingInfo;
import com.davenonymous.bonsaitrees2.registry.soil.SoilInfo;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class RecipeTypes {
    public static IRecipeType<SoilInfo> soilRecipeType;
    public static IRecipeSerializer<SoilInfo> soilRecipeSerializer;

    public static IRecipeType<SaplingInfo> saplingRecipeType;
    public static IRecipeSerializer<SaplingInfo> saplingRecipeSerializer;

    public static <T extends IRecipe<?>> IRecipeType<T> registerRecipeType (String typeId) {
        final IRecipeType<T> type = new IRecipeType<T>() {
            @Override
            public String toString () {
                return BonsaiTrees2.MODID + ":" + typeId;
            }
        };

        return Registry.register(Registry.RECIPE_TYPE,  new ResourceLocation(type.toString()), type);
    }


}
