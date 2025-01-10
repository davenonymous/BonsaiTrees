package com.davenonymous.bonsaitrees.setup;

import com.davenonymous.bonsaitrees.BonsaiTrees;
import com.davenonymous.bonsaitrees.lib.recipe.TransformItemRecipe;
import com.davenonymous.bonsaitrees.lib.recipe.TransformItemRecipeSerializer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModRecipes {
	public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
		DeferredRegister.create(Registries.RECIPE_TYPE, BonsaiTrees.MODID);

	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
		DeferredRegister.create(Registries.RECIPE_SERIALIZER, BonsaiTrees.MODID);

	public static final Supplier<RecipeSerializer<TransformItemRecipe>> TRANSFORM_ITEM_RECIPE_SERIALIZER = RECIPE_SERIALIZERS
		.register("crafting_special_copycomponents", TransformItemRecipeSerializer::new);
}
