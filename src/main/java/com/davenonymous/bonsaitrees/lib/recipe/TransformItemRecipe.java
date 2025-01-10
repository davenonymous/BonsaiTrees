package com.davenonymous.bonsaitrees.lib.recipe;

import com.davenonymous.bonsaitrees.setup.ModRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class TransformItemRecipe extends CustomRecipe {
	Ingredient input;
	ItemStack output;

	public TransformItemRecipe(CraftingBookCategory category, Ingredient input, ItemStack output) {
		super(category);
		this.input = input;
		this.output = output;
	}


	public Ingredient input() {
		return input;
	}

	public ItemStack output() {
		return output;
	}

	@Override
	public boolean matches(CraftingInput craftingInput, Level level) {
		if(craftingInput.size() != 1) {
			return false;
		}
		ItemStack stack = craftingInput.items().getFirst();
		if(stack.isEmpty()) {
			return false;
		}

		if(this.input().test(stack)) {
			return true;
		}

		return false;
	}

	@Override
	public ItemStack assemble(CraftingInput craftingInput, HolderLookup.Provider provider) {
		ItemStack stack = craftingInput.items().getFirst();
		if(stack.isEmpty()) {
			return ItemStack.EMPTY;
		}

		ItemStack result = ItemStack.EMPTY;
		if(this.input().test(stack)) {
			result = this.output().copy();
		}

		var copyableComponentList = stack.getComponents().stream().map(TypedDataComponent::type).toList();
		result.copyFrom(stack, copyableComponentList.toArray(new DataComponentType<?>[0]));

		return result;
	}

	@Override
	public boolean canCraftInDimensions(int x, int y) {
		return true;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModRecipes.TRANSFORM_ITEM_RECIPE_SERIALIZER.get();
	}
}
