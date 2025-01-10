package com.davenonymous.bonsaitrees.lib.gui.tooltip;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class IngredientTooltipComponent implements TooltipComponent, ClientTooltipComponent {
	private final Ingredient items;
	private boolean showLabel = true;

	public IngredientTooltipComponent(Ingredient items) {
		this.items = items;
	}

	public Ingredient getStack() {
		return items;
	}

	public IngredientTooltipComponent setShowLabel(boolean showLabel) {
		this.showLabel = showLabel;
		return this;
	}

	@Override
	public int getHeight() {
		return 16;
	}

	@Override
	public int getWidth(Font font) {
		if(!showLabel) {
			return 16;
		}
		ItemStack item = activeItem();
		return 16 + 2 + font.width(item.getHoverName());
	}

	private ItemStack activeItem() {
		long time = Minecraft.getInstance().level.getGameTime();
		long displayTime = time >> 4;
		ItemStack[] items = this.items.getItems();
		int i = (int) (displayTime % items.length);
		return items[i];
	}

	@Override
	public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
		ItemStack item = activeItem();
		guiGraphics.renderItem(item, x, y);
		guiGraphics.renderItemDecorations(font, item, x, y);
		if(showLabel) {
			guiGraphics.drawString(font, item.getHoverName(), x + 18, y + 4, 0xFFFFFF);
		}
	}

}