package com.davenonymous.bonsaitrees3.compat.jei;

import com.davenonymous.bonsaitrees3.BonsaiTrees3;
import com.davenonymous.bonsaitrees3.libnonymous.base.BaseLanguageProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

public class BonsaiUpgradeCategory implements IRecipeCategory<BonsaiUpgradeWrapper> {
	public static final ResourceLocation ID = new ResourceLocation(BonsaiTrees3.MODID, "upgrades");
	private final IDrawableStatic background;
	private final IDrawableStatic slotDrawable;

	public BonsaiUpgradeCategory(IGuiHelper guiHelper) {
		background = guiHelper.createBlankDrawable(155, 20);
		slotDrawable = guiHelper.getSlotDrawable();

	}

	@Override
	public void draw(BonsaiUpgradeWrapper recipe, PoseStack stack, double mouseX, double mouseY) {
		var font = Minecraft.getInstance().font;
		var text = I18n.get(BaseLanguageProvider.getTranslatableLanguageKey(recipe.upgradeDescription));
		var split = font.getSplitter().splitLines(text, 120, Style.EMPTY);

		var yOffset = 0;
		if(split.size() == 1) {
			yOffset = 5;
		} else if(split.size() == 2) {
			yOffset = 1;
		} else if(split.size() == 3) {
			yOffset = -3;
		}

		for(var line : split) {
			Minecraft.getInstance().font.draw(stack, line.getString(), 24, yOffset, 0x404040);
			yOffset += 9;
		}

	}

	@Override
	public ResourceLocation getUid() {
		return ID;
	}

	@Override
	public Class<? extends BonsaiUpgradeWrapper> getRecipeClass() {
		return BonsaiUpgradeWrapper.class;
	}

	@Override
	public Component getTitle() {
		return new TranslatableComponent("jei.bonsaitrees3.upgrades.title");
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
	public void setIngredients(BonsaiUpgradeWrapper recipe, IIngredients ingredients) {
		recipe.setIngredients(ingredients);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, BonsaiUpgradeWrapper recipe, IIngredients ingredients) {
		recipeLayout.getItemStacks().init(0, true, 0, 0);
		recipeLayout.getItemStacks().addTooltipCallback(recipe);
		recipeLayout.getItemStacks().set(ingredients);
	}
}
