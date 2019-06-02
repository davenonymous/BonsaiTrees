package org.dave.bonsaitrees.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;
import org.dave.bonsaitrees.BonsaiTrees;
import org.dave.bonsaitrees.api.IBonsaiTreeType;
import org.dave.bonsaitrees.init.Blockss;
import org.dave.bonsaitrees.misc.ConfigurationHandler;

@JEIPlugin
public class BonsaiTreesJEIPlugin implements IModPlugin {
    @Override
    public void register(IModRegistry registry) {
        registry.addRecipeCatalyst(new ItemStack(Blockss.bonsaiPot), BonsaiTreeRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(Blockss.bonsaiPot, 1, 1), BonsaiTreeRecipeCategory.UID);
        registry.handleRecipes(IBonsaiTreeType.class, new BonsaiTreeRecipeWrapperFactory(), BonsaiTreeRecipeCategory.UID);
        registry.addRecipes(BonsaiTrees.instance.typeRegistry.getAllTypes(), BonsaiTreeRecipeCategory.UID);

        if(ConfigurationHandler.GeneralSettings.disableHoppingBonsaiPot) {
            registry.getJeiHelpers().getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(Blockss.bonsaiPot, 1, 1));
        }
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(new BonsaiTreeRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
    }
}
