package com.davenonymous.bonsaitrees3.compat.jei;

import com.davenonymous.bonsaitrees3.BonsaiTrees3;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;


public class BonsaiRecipeCategory implements IRecipeCategory<BonsaiRecipeWrapper> {
	public static final ResourceLocation ID = new ResourceLocation(BonsaiTrees3.MODID, "bonsais");
	private final IDrawableStatic background;
	private final IDrawableStatic slotDrawable;

	public BonsaiRecipeCategory(IGuiHelper guiHelper) {
		background = guiHelper.createBlankDrawable(155, 40);
		slotDrawable = guiHelper.getSlotDrawable();
	}

	/*@Override
	public ResourceLocation getUid() {
		return ID;
	}

	@Override
	public Class<? extends BonsaiRecipeWrapper> getRecipeClass() {
		return BonsaiRecipeWrapper.class;
	}*/

	@Override
	public Component getTitle() {
		return Component.translatable("jei.bonsaitrees3.recipes.title");
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public IDrawable getIcon() {
		return null;
	}

	/*@Override
	public void setIngredients(BonsaiRecipeWrapper bonsaiRecipeWrapper, IIngredients iIngredients) {
		bonsaiRecipeWrapper.setIngredients(iIngredients);
	}

	@Override
	public void draw(BonsaiRecipeWrapper recipe, PoseStack stack, double mouseX, double mouseY) {
		slotDrawable.draw(stack, 0, 19 * 0);
		slotDrawable.draw(stack, 0, 19 * 1);

		slotDrawable.draw(stack, 80 + 19 * 0, 19 * 0);
		slotDrawable.draw(stack, 80 + 19 * 1, 19 * 0);
		slotDrawable.draw(stack, 80 + 19 * 2, 19 * 0);
		slotDrawable.draw(stack, 80 + 19 * 3, 19 * 0);

		slotDrawable.draw(stack, 80 + 19 * 0, 19 * 1);
		slotDrawable.draw(stack, 80 + 19 * 1, 19 * 1);
		slotDrawable.draw(stack, 80 + 19 * 2, 19 * 1);
		slotDrawable.draw(stack, 80 + 19 * 3, 19 * 1);
		recipe.drawInfo(getBackground().getWidth(), getBackground().getHeight(), stack, mouseX, mouseY);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, BonsaiRecipeWrapper recipeWrapper, IIngredients ingredients) {
		recipeLayout.getItemStacks().init(0, true, 0, 19 * 0);
		recipeLayout.getItemStacks().init(1, true, 0, 19 * 1);

		recipeLayout.getItemStacks().init(2, false, 80 + 19 * 0, 19 * 0);
		recipeLayout.getItemStacks().init(3, false, 80 + 19 * 1, 19 * 0);
		recipeLayout.getItemStacks().init(4, false, 80 + 19 * 2, 19 * 0);
		recipeLayout.getItemStacks().init(5, false, 80 + 19 * 3, 19 * 0);

		recipeLayout.getItemStacks().init(6, false, 80 + 19 * 0, 19 * 1);
		recipeLayout.getItemStacks().init(7, false, 80 + 19 * 1, 19 * 1);
		recipeLayout.getItemStacks().init(8, false, 80 + 19 * 2, 19 * 1);
		recipeLayout.getItemStacks().init(9, false, 80 + 19 * 3, 19 * 1);

		recipeLayout.getItemStacks().addTooltipCallback(recipeWrapper);
		recipeLayout.getItemStacks().set(ingredients);
	}*/

	@Override
	public RecipeType<BonsaiRecipeWrapper> getRecipeType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, BonsaiRecipeWrapper recipe, IFocusGroup focuses) {
		// TODO Auto-generated method stub
	}
}
