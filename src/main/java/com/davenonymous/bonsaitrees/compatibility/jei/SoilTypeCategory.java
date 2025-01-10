package com.davenonymous.bonsaitrees.compatibility.jei;

import com.davenonymous.bonsaitrees.datacomponents.CamouflageDataComponent;
import com.davenonymous.bonsaitrees.datacomponents.SoilDataComponent;
import com.davenonymous.bonsaitrees.lib.util.Sorting;
import com.davenonymous.bonsaitrees.setup.ModBlocks;
import com.davenonymous.bonsaitrees.setup.ModDataComponents;
import com.davenonymous.bonsaitrees.setup.cache.SoilCache;
import com.davenonymous.bonsaitrees.setup.data.SoilType;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class SoilTypeCategory implements IRecipeCategory<SoilType> {
	private final IJeiHelpers jeiHelpers;

	public SoilTypeCategory(IJeiHelpers jeiHelpers) {
		this.jeiHelpers = jeiHelpers;
	}

	@Override
	public RecipeType<SoilType> getRecipeType() {
		return BonsaiJEIPlugin.SOIL_TYPES;
	}

	@Override
	public int getWidth() {
		return 165;
	}

	@Override
	public int getHeight() {
		return 50;
	}

	@Override
	public Component getTitle() {
		return Component.translatable("jei.bonsaitrees4.soiltypes.title");
	}

	@Override
	public @Nullable IDrawable getIcon() {
		ItemStack stack = new ItemStack(ModBlocks.BONSAI_POT.get());
		stack.set(
			ModDataComponents.CAMOUFLAGE_COMPONENT.get(),
			new CamouflageDataComponent(BuiltInRegistries.BLOCK.getKey(Blocks.LIGHT_GRAY_CONCRETE_POWDER))
		);
		List<Item> soilItems = Sorting.toSortedList(SoilCache.SOILS.keySet().stream().map(ItemStack::getItem).toList());
		if(!soilItems.isEmpty()) {
			int index = ((int) Minecraft.getInstance().level.getGameTime() >> 4) % soilItems.size();
			Item soilToShow = soilItems.get(index);
			stack.set(ModDataComponents.SOIL_COMPONENT.get(), new SoilDataComponent(new ItemStack(soilToShow)));
		}

		return jeiHelpers.getGuiHelper().createDrawableIngredient(VanillaTypes.ITEM_STACK, stack);
	}

	private void addItemLine(IRecipeLayoutBuilder builder, int y, Collection<Item> items) {
		int index = 0;

		Queue<Item> soilQueue = Sorting.toSortedQueue(items);
		while(!soilQueue.isEmpty()) {
			Item soil = soilQueue.poll();
			builder.addInputSlot(index * 18, y).setSlotName("input_" + y + "_" + index).addItemLike(soil);
			index++;

			if(index > 7 && !soilQueue.isEmpty()) {
				Ingredient ingredient = Ingredient.of(soilQueue.stream().map(ItemStack::new));
				builder.addInputSlot(index * 18, y).setSlotName("input_" + y + "_" + index).addIngredients(ingredient);
				break;
			}
		}
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, SoilType recipe, IFocusGroup focuses) {
		if(SoilCache.BONSAIS_BY_SOIL.containsKey(recipe)) {
			Set<Item> bonsais = SoilCache.BONSAIS_BY_SOIL.get(recipe);
			addItemLine(builder, 14, bonsais);
		}

		if(SoilCache.SOIL_BY_TYPE.containsKey(recipe.id())) {
			Set<Item> validSoils = SoilCache.SOIL_BY_TYPE.get(recipe.id()).keySet();
			addItemLine(builder, 32, validSoils);
		}
	}

	@Override
	public void draw(SoilType recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
		guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable(recipe.translationKey()), 2, 2, ChatFormatting.DARK_GRAY.getColor(), false);


	}
}
