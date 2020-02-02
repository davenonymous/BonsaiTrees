package com.davenonymous.bonsaitrees2.registry.sapling;

import com.davenonymous.bonsaitrees2.block.ModObjects;
import com.davenonymous.libnonymous.base.BaseRecipeHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class SaplingRecipeHelper extends BaseRecipeHelper<SaplingInfo> {
    public SaplingRecipeHelper() {
        super(ModObjects.saplingRecipeType);
    }

    public SaplingInfo getSaplingInfoForItem(World world, ItemStack stack) {
        return getRecipeStream(world.getRecipeManager()).filter(recipe -> recipe.ingredient.test(stack)).findFirst().orElse(null);
    }
}
