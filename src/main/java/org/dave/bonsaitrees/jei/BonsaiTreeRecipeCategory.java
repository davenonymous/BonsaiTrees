package org.dave.bonsaitrees.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import org.dave.bonsaitrees.BonsaiTrees;

public class BonsaiTreeRecipeCategory implements IRecipeCategory<BonsaiTreeRecipeWrapper> {
    public static final String UID = BonsaiTrees.MODID + ".Growing";
    private final String localizedName;
    private final IDrawableStatic background;
    private final IDrawableStatic slotDrawable;

    public BonsaiTreeRecipeCategory(IGuiHelper guiHelper) {
        localizedName = I18n.format("bonsaitrees.jei.category.growing");
        background = guiHelper.createBlankDrawable(150, 46);
        slotDrawable = guiHelper.getSlotDrawable();
    }

    @Override
    public String getUid() {
        return UID;
    }

    @Override
    public String getTitle() {
        return localizedName;
    }

    @Override
    public String getModName() {
        return "Bonsai Trees";
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        slotDrawable.draw(minecraft,         0, 19*0);

        slotDrawable.draw(minecraft, 80 + 19*0, 19*0);
        slotDrawable.draw(minecraft, 80 + 19*1, 19*0);
        slotDrawable.draw(minecraft, 80 + 19*2, 19*0);
        slotDrawable.draw(minecraft, 80 + 19*3, 19*0);

        slotDrawable.draw(minecraft, 80 + 19*0, 19*1);
        slotDrawable.draw(minecraft, 80 + 19*1, 19*1);
        slotDrawable.draw(minecraft, 80 + 19*2, 19*1);
        slotDrawable.draw(minecraft, 80 + 19*3, 19*1);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, BonsaiTreeRecipeWrapper recipeWrapper, IIngredients ingredients) {
        recipeLayout.getItemStacks().init(0, true,        0, 19*0);

        recipeLayout.getItemStacks().init(1, false, 80+19*0, 19*0);
        recipeLayout.getItemStacks().init(2, false, 80+19*1, 19*0);
        recipeLayout.getItemStacks().init(3, false, 80+19*2, 19*0);
        recipeLayout.getItemStacks().init(4, false, 80+19*3, 19*0);

        recipeLayout.getItemStacks().init(5, false, 80+19*0, 19*1);
        recipeLayout.getItemStacks().init(6, false, 80+19*1, 19*1);
        recipeLayout.getItemStacks().init(7, false, 80+19*2, 19*1);
        recipeLayout.getItemStacks().init(8, false, 80+19*3, 19*1);

        recipeLayout.getItemStacks().addTooltipCallback(recipeWrapper);
        recipeLayout.getItemStacks().set(ingredients);
    }
}
