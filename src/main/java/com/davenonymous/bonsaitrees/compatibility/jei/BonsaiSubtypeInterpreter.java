package com.davenonymous.bonsaitrees.compatibility.jei;

import com.davenonymous.bonsaitrees.datacomponents.CamouflageDataComponent;
import com.davenonymous.bonsaitrees.datacomponents.SaplingDataComponent;
import com.davenonymous.bonsaitrees.datacomponents.SoilDataComponent;
import com.davenonymous.bonsaitrees.setup.ModDataComponents;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class BonsaiSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {
	@Override
	public @Nullable Object getSubtypeData(ItemStack ingredient, UidContext context) {
		SaplingDataComponent property = ingredient.get(ModDataComponents.SAPLING_COMPONENT.get());
		SoilDataComponent soilProperty = ingredient.get(ModDataComponents.SOIL_COMPONENT.get());
		CamouflageDataComponent camouflageProperty = ingredient.get(ModDataComponents.CAMOUFLAGE_COMPONENT.get());

		return new BonsaiSubtypeData(property, soilProperty, camouflageProperty);
	}

	@Override
	public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
		return getSubtypeData(ingredient, context).toString();
	}

	record BonsaiSubtypeData(SaplingDataComponent sapling, SoilDataComponent soil, CamouflageDataComponent camouflage) {
		@Override
		public String toString() {
			return String.format("sapling=%s, soil=%s, camouflage=%s", sapling.sapling().toString(), soil.soil().toString(), camouflage.camouflage().toString());
		}
	}
}
