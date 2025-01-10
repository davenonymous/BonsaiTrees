package com.davenonymous.bonsaitrees.datagen;

import com.davenonymous.bonsaitrees.BonsaiTrees;
import com.davenonymous.bonsaitrees.lib.recipe.TransformItemRecipeBuilder;
import com.davenonymous.bonsaitrees.setup.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class DGRecipes extends RecipeProvider {
	public DGRecipes(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	protected void buildRecipes(RecipeOutput recipeOutput) {
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BONSAI_POT_ITEM.get())
			.pattern("   ")
			.pattern("b b")
			.pattern("bbb")
			.define('b', Tags.Items.BRICKS)
			.unlockedBy("has_bricks", has(Tags.Items.BRICKS))
			.save(recipeOutput);

		TransformItemRecipeBuilder.simple(ModItems.BONSAI_POT_ITEM.get(), ModItems.BONSAI_POT_SMALL_ITEM.get())
			.save(recipeOutput, BonsaiTrees.resource("bonsai_pot_to_small"));

		TransformItemRecipeBuilder.simple(ModItems.BONSAI_POT_SMALL_ITEM.get(), ModItems.BONSAI_POT_ITEM.get())
			.save(recipeOutput, BonsaiTrees.resource("bonsai_pot_to_big"));
	}
}
