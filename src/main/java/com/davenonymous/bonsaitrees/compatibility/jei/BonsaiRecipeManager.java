package com.davenonymous.bonsaitrees.compatibility.jei;

import com.davenonymous.bonsaitrees.setup.cache.JeiRecipeCache;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.advanced.ISimpleRecipeManagerPlugin;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.List;

public class BonsaiRecipeManager implements ISimpleRecipeManagerPlugin<BonsaiRecipe> {

	@Override
	public boolean isHandledInput(ITypedIngredient<?> input) {
		if(input.getItemStack().isEmpty()) {
			return false;
		}

		Item item = input.getItemStack().get().getItem();
		return JeiRecipeCache.RECIPES_BY_INPUT.containsKey(item);
	}

	@Override
	public boolean isHandledOutput(ITypedIngredient<?> output) {
		if(output.getItemStack().isEmpty()) {
			return false;
		}

		Item item = output.getItemStack().get().getItem();
		return JeiRecipeCache.RECIPES_BY_OUTPUT.containsKey(item);
	}

	@Override
	public List<BonsaiRecipe> getRecipesForInput(ITypedIngredient<?> input) {
		if(input.getItemStack().isEmpty()) {
			return Collections.emptyList();
		}
		ItemStack stack = input.getItemStack().get();
		Item item = stack.getItem();

		return JeiRecipeCache.RECIPES_BY_INPUT.getOrDefault(item, Collections.emptySet()).stream().toList();
	}

	@Override
	public List<BonsaiRecipe> getRecipesForOutput(ITypedIngredient<?> output) {
		if(output.getItemStack().isEmpty()) {
			return Collections.emptyList();
		}
		ItemStack stack = output.getItemStack().get();
		Item item = stack.getItem();

		return JeiRecipeCache.RECIPES_BY_OUTPUT.getOrDefault(item, Collections.emptySet()).stream().toList();
	}

	@Override
	public List<BonsaiRecipe> getAllRecipes() {
		return JeiRecipeCache.ALL_RECIPES;
	}
}
