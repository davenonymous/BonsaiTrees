package com.davenonymous.bonsaitrees2.registry.soil;

import com.davenonymous.bonsaitrees2.block.ModObjects;
import com.davenonymous.libnonymous.base.BaseRecipeHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class SoilRecipeHelper extends BaseRecipeHelper<SoilInfo> {
    public SoilRecipeHelper() {
        super(ModObjects.soilRecipeType);
    }

    public SoilInfo getSoilForItem(World world, ItemStack stack) {
        return getRecipeStream(world.getRecipeManager()).filter(recipe -> recipe.ingredient.test(stack)).findFirst().orElse(null);
    }
}
