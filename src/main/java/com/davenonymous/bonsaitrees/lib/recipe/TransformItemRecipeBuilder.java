package com.davenonymous.bonsaitrees.lib.recipe;

import com.davenonymous.bonsaitrees.lib.SimpleRecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;

public class TransformItemRecipeBuilder extends SimpleRecipeBuilder {
	Ingredient input;
	CraftingBookCategory category;

	public TransformItemRecipeBuilder(CraftingBookCategory category, Ingredient input, Item output) {
		super(new ItemStack(output));
		this.input = input;
		this.category = category;
	}

	public static TransformItemRecipeBuilder simple(Item input, Item output) {
		return new TransformItemRecipeBuilder(CraftingBookCategory.MISC, Ingredient.of(input), output);
	}

	public TransformItemRecipe build() {
		return new TransformItemRecipe(this.category, this.input, this.result);
	}

	@Override
	public void save(RecipeOutput recipeOutput, ResourceLocation resourceLocation) {
		TransformItemRecipe recipe = build();
		recipeOutput.accept(resourceLocation, recipe, null);
	}
}
