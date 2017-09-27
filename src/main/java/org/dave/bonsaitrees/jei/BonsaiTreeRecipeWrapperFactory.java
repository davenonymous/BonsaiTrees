package org.dave.bonsaitrees.jei;

import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;
import org.dave.bonsaitrees.api.IBonsaiTreeType;

public class BonsaiTreeRecipeWrapperFactory implements IRecipeWrapperFactory<IBonsaiTreeType>{
    @Override
    public IRecipeWrapper getRecipeWrapper(IBonsaiTreeType recipe) {
        return new BonsaiTreeRecipeWrapper(recipe);
    }
}
