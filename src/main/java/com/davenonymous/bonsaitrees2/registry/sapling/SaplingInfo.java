package com.davenonymous.bonsaitrees2.registry.sapling;

import com.davenonymous.bonsaitrees2.block.ModObjects;
import com.davenonymous.libnonymous.utils.GsonHelper;
import com.davenonymous.libnonymous.utils.RecipeData;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

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
    //TODO from here
    @Override
    public boolean matches(IInventory inv, World worldIn) {
        return false;
    }

    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        return null;
    }

    @Override
    public boolean canFit(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }
    //TODO till here
    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModObjects.saplingRecipeSerializer;
    }

    @Override
    public IRecipeType<?> getType() {
        return ModObjects.saplingRecipeType;
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

    public String serializePretty() {
        JsonObject result = new JsonObject();
        result.addProperty("type", "bonsaitrees2:sapling");

        JsonObject saplingObj = new JsonObject();
        saplingObj.addProperty("item", this.sapling.getItem().getRegistryName().toString());
        result.add("sapling", saplingObj);

        JsonArray drops = new JsonArray();
        for(SaplingDrop drop : this.drops) {
            JsonObject itemObj = new JsonObject();
            itemObj.addProperty("item", drop.resultStack.getItem().getRegistryName().toString());

            JsonObject dropObj = new JsonObject();
            dropObj.add("result", itemObj);
            dropObj.addProperty("rolls", drop.rolls);
            dropObj.addProperty("chance", drop.chance / 100);
            drops.add(dropObj);
        }
        result.add("drops", drops);

        JsonArray soilTags = new JsonArray();
        tags.forEach(soilTags::add);
        result.add("compatibleSoilTags", soilTags);

        return GsonHelper.GSON.toJson(result);
    }
}
