package org.dave.bonsaitrees.jei;

import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;
import org.dave.bonsaitrees.base.BaseTreeType;

public class BonsaiTreeRecipeWrapperFactory implements IRecipeWrapperFactory<BaseTreeType>{
    @Override
    public IRecipeWrapper getRecipeWrapper(BaseTreeType recipe) {
        return new BonsaiTreeRecipeWrapper(recipe);
    }
}
