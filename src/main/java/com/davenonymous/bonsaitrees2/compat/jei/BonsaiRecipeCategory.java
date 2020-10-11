package com.davenonymous.bonsaitrees2.compat.jei;

import com.davenonymous.bonsaitrees2.BonsaiTrees2;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class BonsaiRecipeCategory implements IRecipeCategory<BonsaiRecipeWrapper> {
    public static final ResourceLocation ID = new ResourceLocation(BonsaiTrees2.MODID, "bonsais");
    private final IDrawableStatic background;
    private final IDrawableStatic slotDrawable;

    public BonsaiRecipeCategory(IGuiHelper guiHelper) {
        background = guiHelper.createBlankDrawable(155, 40);
        slotDrawable = guiHelper.getSlotDrawable();
    }

    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Override
    public Class<? extends BonsaiRecipeWrapper> getRecipeClass() {
        return BonsaiRecipeWrapper.class;
    }

    @Override
    public String getTitle() {
        return I18n.format("bonsaitrees2.title");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return null;
    }

    @Override
    public void setIngredients(BonsaiRecipeWrapper bonsaiRecipeWrapper, IIngredients iIngredients) {
        bonsaiRecipeWrapper.setIngredients(iIngredients);
    }

    //TODO
    /*@Override
    public void draw(BonsaiRecipeWrapper recipe, double mouseX, double mouseY) {
        slotDrawable.draw(0, 19*0);
        slotDrawable.draw(0, 19*1);

        slotDrawable.draw(80 + 19*0, 19*0);
        slotDrawable.draw(80 + 19*1, 19*0);
        slotDrawable.draw(80 + 19*2, 19*0);
        slotDrawable.draw(80 + 19*3, 19*0);

        slotDrawable.draw(80 + 19*0, 19*1);
        slotDrawable.draw(80 + 19*1, 19*1);
        slotDrawable.draw(80 + 19*2, 19*1);
        slotDrawable.draw(80 + 19*3, 19*1);
        recipe.drawInfo(getBackground().getWidth(), getBackground().getHeight(), mouseX, mouseY);
    }*/

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, BonsaiRecipeWrapper recipeWrapper, IIngredients ingredients) {
        recipeLayout.getItemStacks().init(0, true,        0, 19*0);
        recipeLayout.getItemStacks().init(1, true,        0, 19*1);

        recipeLayout.getItemStacks().init(2, false, 80+19*0, 19*0);
        recipeLayout.getItemStacks().init(3, false, 80+19*1, 19*0);
        recipeLayout.getItemStacks().init(4, false, 80+19*2, 19*0);
        recipeLayout.getItemStacks().init(5, false, 80+19*3, 19*0);

        recipeLayout.getItemStacks().init(6, false, 80+19*0, 19*1);
        recipeLayout.getItemStacks().init(7, false, 80+19*1, 19*1);
        recipeLayout.getItemStacks().init(8, false, 80+19*2, 19*1);
        recipeLayout.getItemStacks().init(9, false, 80+19*3, 19*1);

        recipeLayout.getItemStacks().addTooltipCallback(recipeWrapper);
        recipeLayout.getItemStacks().set(ingredients);
    }
}
