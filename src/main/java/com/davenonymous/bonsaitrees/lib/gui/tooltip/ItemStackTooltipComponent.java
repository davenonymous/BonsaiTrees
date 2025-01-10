package com.davenonymous.bonsaitrees.lib.gui.tooltip;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public class ItemStackTooltipComponent implements TooltipComponent, ClientTooltipComponent {
	private final ItemStack stack;
	private boolean showLabel = true;
	private ChatFormatting labelColor = ChatFormatting.GRAY;

	public ItemStackTooltipComponent(ItemStack stack) {
		this.stack = stack;
	}

	public ItemStack getStack() {
		return stack;
	}

	public ItemStackTooltipComponent setShowLabel(boolean showLabel) {
		this.showLabel = showLabel;
		return this;
	}

	public ItemStackTooltipComponent setLabelColor(ChatFormatting labelColor) {
		this.labelColor = labelColor;
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
		return 16 + 2 + font.width(stack.getHoverName());
	}

	@Override
	public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
		guiGraphics.renderItem(stack, x, y);
		guiGraphics.renderItemDecorations(font, stack, x, y);
		if(showLabel) {
			guiGraphics.drawString(font, stack.getHoverName(), x + 18, y + 4, this.labelColor.getColor());
		}
	}

}
