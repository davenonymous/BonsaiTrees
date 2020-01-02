package com.davenonymous.bonsaitrees2.registry.sapling;

import com.davenonymous.bonsaitrees2.registry.RecipeTypes;
import com.davenonymous.bonsaitrees2.util.RecipeData;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import java.util.*;

public class SaplingInfo extends RecipeData {
    private final ResourceLocation id;

    public Ingredient ingredient;
    public int baseTicks;

    public ItemStack sapling;
    public ArrayList<SaplingDrop> drops;
    public Set<String> tags;

    public SaplingInfo(ResourceLocation id, Ingredient ingredient, int baseTicks) {
        this.id = id;
        this.ingredient = ingredient;
        this.baseTicks = baseTicks;
        this.drops = new ArrayList<>();
        this.tags = new HashSet<>();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RecipeTypes.saplingRecipeSerializer;
    }

    @Override
    public IRecipeType<?> getType() {
        return RecipeTypes.saplingRecipeType;
    }

    public int getRequiredTicks() {
        return baseTicks;
    }

    public void addDrop(SaplingDrop drop) {
        this.drops.add(drop);
        this.drops.sort((a, b) -> (int)(b.chance*1000) - (int)(a.chance*1000));
    }

    public void addTag(String tag) {
        this.tags.add(tag);
    }

    public boolean isValidTag(String tag) {
        return this.tags.contains(tag);
    }

    public List<ItemStack> getRandomizedDrops(Random rand) {
        ArrayList<ItemStack> result = new ArrayList<>();
        for(SaplingDrop drop : this.drops) {
            ItemStack dropStack = drop.getRandomDrop(rand);
            if(dropStack.isEmpty()) {
                continue;
            }

            result.add(dropStack);
        }

        return result;
    }
}
