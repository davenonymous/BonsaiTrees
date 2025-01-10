package com.davenonymous.bonsaitrees.lib.gui.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HBoxTooltipComponent implements TooltipComponent, ClientTooltipComponent {
	private final List<TooltipComponent> components = new ArrayList<>();
	private int padding = 2;

	private BoxAlignment alignment = BoxAlignment.START;

	public HBoxTooltipComponent(TooltipComponent... components) {
		this.components.addAll(Arrays.asList(components));
	}

	public HBoxTooltipComponent add(TooltipComponent... component) {
		this.components.addAll(Arrays.asList(component));
		return this;
	}

	public HBoxTooltipComponent setAlignment(BoxAlignment alignment) {
		this.alignment = alignment;
		return this;
	}

	public HBoxTooltipComponent setPadding(int padding) {
		this.padding = padding;
		return this;
	}

	public int count() {
		return components.size();
	}

	public HBoxTooltipComponent clear() {
		components.clear();
		return this;
	}

	@Override
	public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
		int currentX = x;
		int maxHeight = getHeight();
		for(TooltipComponent component : components) {
			if(component instanceof ClientTooltipComponent clientComponent) {
				int yOffset = y;
				int componentHeight = clientComponent.getHeight();
				if(alignment == BoxAlignment.CENTER) {
					yOffset += (maxHeight - componentHeight) / 2;
				} else if(alignment == BoxAlignment.END) {
					yOffset += maxHeight - componentHeight;
				}

				clientComponent.renderImage(font, currentX, yOffset, guiGraphics);
				currentX += clientComponent.getWidth(font) + padding;
			}
		}
	}

	@Override
	public int getHeight() {
		int max = 0;
		for(TooltipComponent component : components) {
			if(component instanceof ClientTooltipComponent clientComponent) {
				max = Math.max(max, clientComponent.getHeight());
			}
		}
		return max;
	}

	@Override
	public int getWidth(Font font) {
		int sum = 0;
		for(TooltipComponent component : components) {
			if(component instanceof ClientTooltipComponent clientComponent) {
				sum += clientComponent.getWidth(font) + padding;
			}
		}
		return sum;
	}
}