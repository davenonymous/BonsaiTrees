package com.davenonymous.bonsaitrees.lib.gui.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class IngredientBoxTooltipComponent implements TooltipComponent, ClientTooltipComponent {
	private final List<Ingredient> items;
	private int columns = 9;
	private int maxRows = 3;
	private int padding = 1;

	private VBoxTooltipComponent container;

	public IngredientBoxTooltipComponent(Ingredient... items) {
		this.items = new ArrayList<>();
		Collections.addAll(this.items, items);
		rebuild();
	}

	public IngredientBoxTooltipComponent(Collection<Item> items) {
		this.items = items.stream().map(Ingredient::of).toList();
		rebuild();
	}

	public IngredientBoxTooltipComponent setPadding(int padding) {
		this.padding = padding;
		rebuild();
		return this;
	}

	public IngredientBoxTooltipComponent setColumns(int columns) {
		this.columns = columns;
		rebuild();
		return this;
	}

	public IngredientBoxTooltipComponent setMaxRows(int maxRows) {
		this.maxRows = maxRows;
		rebuild();
		return this;
	}

	public IngredientBoxTooltipComponent rebuild() {
		this.container = new VBoxTooltipComponent().setPadding(padding);
		int shown = 0;
		List<ItemStack> finalTooltipStacks = new ArrayList<>();

		var hbox = new HBoxTooltipComponent().setPadding(padding);
		for(Ingredient ingredient : items) {
			if(shown >= (maxRows * columns) - 1) {
				Collections.addAll(finalTooltipStacks, ingredient.getItems());
				continue;
			}

			if(hbox.count() >= columns) {
				container.add(hbox);
				hbox = new HBoxTooltipComponent().setPadding(padding);
			}

			IngredientTooltipComponent ingredientTooltipComponent = new IngredientTooltipComponent(ingredient);
			ingredientTooltipComponent.setShowLabel(false);
			hbox.add(ingredientTooltipComponent);
			shown++;
		}
		if(!finalTooltipStacks.isEmpty()) {
			var ingTT = new IngredientTooltipComponent(Ingredient.of(finalTooltipStacks.stream()));
			ingTT.setShowLabel(false);
			hbox.add(ingTT);
		}
		container.add(hbox);

		if(!finalTooltipStacks.isEmpty()) {
			container.add(StringTooltipComponent.white(""));
			container.add(new TranslatableTooltipComponent("bonsaitrees4.tooltip.and_more", null, new Object[]{finalTooltipStacks.size()}));
		}

		return this;
	}

	@Override
	public int getHeight() {
		return container.getHeight();
	}

	@Override
	public int getWidth(Font font) {
		return container.getWidth(font);
	}

	@Override
	public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
		container.renderImage(font, x, y, guiGraphics);
	}

}
