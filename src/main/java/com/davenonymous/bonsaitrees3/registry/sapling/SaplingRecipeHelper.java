package com.davenonymous.bonsaitrees3.registry.sapling;

import com.davenonymous.libnonymous.helper.BaseRecipeHelper;
import com.davenonymous.bonsaitrees3.setup.Registration;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SaplingRecipeHelper extends BaseRecipeHelper<SaplingInfo> {
	public SaplingRecipeHelper() {
		super(Registration.RECIPE_TYPE_SAPLING.get());
	}

	public SaplingInfo getSaplingInfoForItem(Level level, ItemStack stack) {
		return getRecipeStream(level.getRecipeManager()).filter(recipe -> recipe.ingredient.test(stack)).findFirst().orElse(null);
	}
}