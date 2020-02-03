package com.davenonymous.bonsaitrees2.block;

import com.davenonymous.bonsaitrees2.registry.sapling.SaplingInfo;
import com.davenonymous.bonsaitrees2.registry.sapling.SaplingRecipeHelper;
import com.davenonymous.bonsaitrees2.registry.soil.SoilInfo;
import com.davenonymous.bonsaitrees2.registry.soil.SoilRecipeHelper;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;

public class ModObjects {
    public static IRecipeType<SoilInfo> soilRecipeType;
    public static IRecipeSerializer<SoilInfo> soilRecipeSerializer;
    public static SoilRecipeHelper soilRecipeHelper;

    public static IRecipeType<SaplingInfo> saplingRecipeType;
    public static IRecipeSerializer<SaplingInfo> saplingRecipeSerializer;
    public static SaplingRecipeHelper saplingRecipeHelper;
}
