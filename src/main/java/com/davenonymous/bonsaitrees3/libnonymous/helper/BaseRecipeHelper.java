package com.davenonymous.bonsaitrees3.libnonymous.helper;

import com.davenonymous.bonsaitrees3.libnonymous.base.RecipeData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BaseRecipeHelper<T extends RecipeData> {
	RecipeType<T> recipeType;

	public BaseRecipeHelper(RecipeType<T> type) {
		this.recipeType = type;
	}

	public boolean hasRecipes(RecipeManager manager) {
		Map<ResourceLocation, T> recipes = getRecipes(manager);
		return recipes != null && recipes.size() > 0;
	}

	public int getRecipeCount(RecipeManager manager) {
		Map<ResourceLocation, T> recipes = getRecipes(manager);
		return recipes != null ? recipes.size() : 0;
	}

	public T getRecipe(RecipeManager manager, ResourceLocation id) {
		Map<ResourceLocation, T> recipes = getRecipes(manager);
		if(recipes == null) {
			return null;
		}

		return recipes.getOrDefault(id, null);
	}

	public Stream<T> getRecipeStream(RecipeManager manager) {
		return getRecipes(manager).values().stream().map(r -> (T) r);
	}

	public Map<ResourceLocation, T> getRecipes(RecipeManager manager) {
		HashMap<ResourceLocation, T> result = new HashMap<>();
		var foo = manager.getAllRecipesFor(this.recipeType);
		for(T t : foo) {
			result.put(t.getId(), t);
		}
		return result;
	}

	public List<T> getRecipesList(RecipeManager manager) {
		return getRecipeStream(manager).collect(Collectors.toList());
	}

	public T getRandomRecipe(RecipeManager manager, Random rand) {
		Map<ResourceLocation, T> recipes = getRecipes(manager);
		if(recipes == null || recipes.size() == 0) {
			return null;
		}
		Set<ResourceLocation> ids = recipes.keySet();
		ResourceLocation randomId = (ResourceLocation) ids.toArray()[rand.nextInt(ids.size())];
		return (T) recipes.get(randomId);

	}

}
