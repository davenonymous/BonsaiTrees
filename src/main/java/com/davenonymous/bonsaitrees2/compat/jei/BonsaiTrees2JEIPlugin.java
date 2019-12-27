package com.davenonymous.bonsaitrees2.compat.jei;

import com.davenonymous.bonsaitrees2.BonsaiTrees2;
import com.davenonymous.bonsaitrees2.block.Blockz;
import com.davenonymous.bonsaitrees2.registry.sapling.SaplingInfo;
import com.davenonymous.bonsaitrees2.util.Logz;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@JeiPlugin
public class BonsaiTrees2JEIPlugin implements IModPlugin {
    public static List<SaplingInfo> saplings;

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(BonsaiTrees2.MODID, "jei");
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(Blockz.BONSAIPOT), BonsaiRecipeCategory.ID);
        registration.addRecipeCatalyst(new ItemStack(Blockz.HOPPING_BONSAIPOT), BonsaiRecipeCategory.ID);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        if(saplings == null) {
            return;
        }

        Logz.info("Registering {} saplings", saplings.size());
        registration.addRecipes(asRecipes(saplings, BonsaiRecipeWrapper::new), BonsaiRecipeCategory.ID);
    }

    /*
        This has been copied from JustEnoughResources to make life in the lines above easier.
        https://github.com/way2muchnoise/JustEnoughResources/blob/d04c9a4a12dda05348a3397144cc885714ee89a4/src/main/java/jeresources/jei/JEIConfig.java#L92-L94

        See license here:
        https://github.com/way2muchnoise/JustEnoughResources/blob/d04c9a4a12/LICENSE.md
     */
    private static <T, R> Collection<R> asRecipes(Collection<T> collection, Function<T, R> transformer) {
        return collection.stream().map(transformer).collect(Collectors.toList());
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new BonsaiRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }
}
