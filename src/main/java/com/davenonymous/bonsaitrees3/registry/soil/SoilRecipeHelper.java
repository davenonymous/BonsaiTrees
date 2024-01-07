package com.davenonymous.bonsaitrees3.registry.soil;

import com.davenonymous.libnonymous.helper.BaseRecipeHelper;
import com.davenonymous.bonsaitrees3.setup.Registration;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SoilRecipeHelper extends BaseRecipeHelper<SoilInfo> {
	public SoilRecipeHelper() {
		super(Registration.RECIPE_TYPE_SOIL.get());
	}

	public SoilInfo getSoilForItem(Level level, ItemStack stack) {
		return getRecipeStream(level.getRecipeManager()).filter(recipe -> recipe.ingredient.test(stack)).findFirst().orElse(null);
	}
}