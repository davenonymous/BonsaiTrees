package org.dave.bonsaitrees.jei;

import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;
import org.dave.bonsaitrees.trees.TreeType;

public class BonsaiTreeRecipeWrapperFactory implements IRecipeWrapperFactory<TreeType>{
    @Override
    public IRecipeWrapper getRecipeWrapper(TreeType recipe) {
        return new BonsaiTreeRecipeWrapper(recipe);
    }
}
