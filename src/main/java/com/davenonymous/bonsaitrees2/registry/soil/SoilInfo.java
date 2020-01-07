package com.davenonymous.bonsaitrees2.registry.soil;

import com.davenonymous.bonsaitrees2.registry.RecipeTypes;
import com.davenonymous.libnonymous.utils.RecipeData;
import net.minecraft.block.BlockState;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import java.util.HashSet;
import java.util.Set;

public class SoilInfo extends RecipeData {
    private final ResourceLocation id;

    public Ingredient ingredient;
    public BlockState renderState;

    public float tickModifier;

    public Set<String> tags;

    public SoilInfo(ResourceLocation id, Ingredient ingredient, BlockState renderState, float tickModifier) {
        this.id = id;
        this.ingredient = ingredient;
        this.renderState = renderState;
        this.tickModifier = tickModifier;
        this.tags = new HashSet<>();
    }

    public void addTag(String tag) {
        this.tags.add(tag);
    }

    public boolean isValidTag(String tag) {
        return this.tags.contains(tag);
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RecipeTypes.soilRecipeSerializer;
    }

    @Override
    public IRecipeType<?> getType() {
        return RecipeTypes.soilRecipeType;
    }

    public float getTickModifier() {
        return tickModifier;
    }
}
