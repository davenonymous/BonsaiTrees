package com.davenonymous.bonsaitrees.lib.gui.widgets;

import com.davenonymous.bonsaitrees.lib.gui.SmartNumberFormatter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;


public class WidgetProgressBar extends WidgetWithRangeValue<Double> {
	int borderColor = 0xFF000000;
	int foregroundColor = 0xFF33AA33;
	int backgroundColor = 0xFF333333;
	int textColor = 0xFFFFFFFF;

	EnumDisplayMode displayMode = EnumDisplayMode.PERCENTAGE;

	public WidgetProgressBar() {
		this.value = 0D;
		this.setRange(0D, 100D);
	}

	public int getBorderColor() {
		return borderColor;
	}

	public WidgetProgressBar setBorderColor(int borderColor) {
		this.borderColor = borderColor;
		return this;
	}

	public int getForegroundColor() {
		return foregroundColor;
	}

	public WidgetProgressBar setForegroundColor(int foregroundColor) {
		this.foregroundColor = foregroundColor;
		return this;
	}

	public int getBackgroundColor() {
		return backgroundColor;
	}

	public WidgetProgressBar setBackgroundColor(int backgroundColor) {
		this.backgroundColor = backgroundColor;
		return this;
	}

	public int getTextColor() {
		return textColor;
	}

	public WidgetProgressBar setTextColor(int textColor) {
		this.textColor = textColor;
		return this;
	}

	public EnumDisplayMode getDisplayMode() {
		return displayMode;
	}

	public WidgetProgressBar setDisplayMode(EnumDisplayMode displayMode) {
		this.displayMode = displayMode;
		return this;
	}

	@Override
	public void draw(GuiGraphics pGuiGraphics, Screen screen) {
		int x = 0;
		int y = 0;
		int width = this.width;
		int height = this.height;

		pGuiGraphics.fillGradient(x, y, x + width, y + height, borderColor, borderColor);
		pGuiGraphics.fillGradient(x + 1, y + 1, x + width - 1, y + height - 1, backgroundColor, backgroundColor);

		double progress = (getValue() - getRangeMin()) / (getRangeMax() - getRangeMin());
		progress = Math.min(Math.max(progress, 0.0d), 1.0d);
		int progressWidth = (int) ((Math.ceil((double) width - 2) * progress));

		pGuiGraphics.fillGradient(x + 1, y + 1, x + 1 + progressWidth, y + height - 1, foregroundColor, foregroundColor);

		if(displayMode != EnumDisplayMode.NOTHING && displayMode != EnumDisplayMode.CUSTOM) {
			Font fr = screen.getMinecraft().font;
			String content = "";

			if(displayMode == EnumDisplayMode.PERCENTAGE) {
				content = String.format("%.1f%%", progress * 100);
			} else if(displayMode == EnumDisplayMode.VALUE) {
				content = String.valueOf(SmartNumberFormatter.formatNumber(this.getValue()));
			} else if(displayMode == EnumDisplayMode.VALUE_AND_PERCENTAGE) {
				content = String.format("%.1f%% (%s)", progress * 100, SmartNumberFormatter.formatNumber(this.getValue()));
			}


			int xPos = x + 1 + (width - fr.width(content)) / 2;
			int yPos = y + (height + 4 - fr.lineHeight) / 2;
			pGuiGraphics.drawString(fr, content, xPos, yPos, textColor);
		}
	}

	public enum EnumDisplayMode {
		NOTHING, VALUE, PERCENTAGE, VALUE_AND_PERCENTAGE, CUSTOM
	}
}