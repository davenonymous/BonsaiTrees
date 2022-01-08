package com.davenonymous.bonsaitrees3.libnonymous.base;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

public abstract class RecipeData implements Recipe<Inventory> {
	public RecipeData() {
		if(this.getSerializer() == null) {
			throw new IllegalStateException("No serializer found for " + this.getClass().getName());
		}

		if(this.getType() == null) {
			throw new IllegalStateException("No recipe type found for " + this.getClass().getName());
		}
	}

	@Override
	public boolean matches(Inventory inv, Level level) {
		// Not used
		return false;
	}

	@Override
	public ItemStack assemble(Inventory inv) {
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getResultItem() {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return false;
	}
}
