package com.davenonymous.bonsaitrees.lib.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class TransformItemRecipeSerializer implements RecipeSerializer<TransformItemRecipe> {
	private final MapCodec<TransformItemRecipe> codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
			CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(CraftingRecipe::category),
			Ingredient.CODEC.fieldOf("input").forGetter(TransformItemRecipe::input),
			ItemStack.CODEC.fieldOf("output").forGetter(TransformItemRecipe::output)
		).apply(instance, TransformItemRecipe::new)
	);

	private final StreamCodec<RegistryFriendlyByteBuf, TransformItemRecipe> streamCodec = StreamCodec.composite(
		CraftingBookCategory.STREAM_CODEC, CraftingRecipe::category,
		Ingredient.CONTENTS_STREAM_CODEC, TransformItemRecipe::input,
		ItemStack.STREAM_CODEC, TransformItemRecipe::output,
		TransformItemRecipe::new
	);

	@Override
	public MapCodec<TransformItemRecipe> codec() {
		return this.codec;
	}

	@Override
	public StreamCodec<RegistryFriendlyByteBuf, TransformItemRecipe> streamCodec() {
		return this.streamCodec;
	}
}
