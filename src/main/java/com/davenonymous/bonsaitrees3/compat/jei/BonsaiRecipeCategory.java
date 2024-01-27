package com.davenonymous.bonsaitrees3.compat.jei;

import com.davenonymous.bonsaitrees3.setup.Registration;
import com.mojang.blaze3d.vertex.PoseStack;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;


public class BonsaiRecipeCategory implements IRecipeCategory<BonsaiRecipeWrapper> {
	private final IGuiHelper guiHelper;
	private final RecipeType<BonsaiRecipeWrapper> type;
	private final IDrawableStatic background;
	private final IDrawable icon;
    private final Component localizedName;

	public BonsaiRecipeCategory(IGuiHelper guiHelper, RecipeType<BonsaiRecipeWrapper> type) {
		this.guiHelper = guiHelper;
		this.type = type;
		this.background = guiHelper.createBlankDrawable(155, 40);
		this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Registration.BONSAI_POT_ITEM.get()));
		this.localizedName = Component.translatable("jei.bonsaitrees3.recipes.title");
	}
	
    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BonsaiRecipeWrapper recipe, IFocusGroup focuses) {
        recipe.setRecipe(builder, focuses);
    }
    
    @Override
    public void draw(BonsaiRecipeWrapper recipe, IRecipeSlotsView view, PoseStack stack, double mouseX, double mouseY) {
    	recipe.draw(view, stack, mouseX, mouseY, this.guiHelper);
    }

	@Override
	public RecipeType<BonsaiRecipeWrapper> getRecipeType() {
		return this.type;
	}

	@Override
	public Component getTitle() {
		return this.localizedName;
	}

	@Override
	public IDrawable getBackground() {
		return this.background;
	}

	@Override
	public IDrawable getIcon() {
		return this.icon;
	}
}
