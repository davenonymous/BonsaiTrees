package com.davenonymous.bonsaitrees3.compat.jei;

import com.davenonymous.bonsaitrees3.libnonymous.helper.Translatable;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.ingredient.ITooltipCallback;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.List;

public class BonsaiUpgradeWrapper implements IRecipeCategoryExtension, ITooltipCallback<ItemStack> {
	List<ItemStack> upgradeItems;
	Translatable upgradeDescription;

	public BonsaiUpgradeWrapper(Translatable upgradeDescription, ItemStack... upgradeItem) {
		this.upgradeItems = Arrays.stream(upgradeItem).toList();
		this.upgradeDescription = upgradeDescription;
	}

	public BonsaiUpgradeWrapper(Translatable upgradeDescription, List<ItemStack> upgradeItems) {
		this.upgradeItems = upgradeItems;
		this.upgradeDescription = upgradeDescription;
	}

	@Override
	public void onTooltip(int slotIndex, boolean input, ItemStack ingredient, List<Component> tooltip) {

	}

	@Override
	public void setIngredients(IIngredients ingredients) {
		ingredients.setInputLists(VanillaTypes.ITEM, List.of(this.upgradeItems));
	}
}
