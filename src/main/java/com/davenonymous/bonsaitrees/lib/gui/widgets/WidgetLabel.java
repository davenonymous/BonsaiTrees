package com.davenonymous.bonsaitrees.lib.gui.widgets;


import com.davenonymous.bonsaitrees.lib.gui.GUIHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;

import java.util.function.Function;

public class WidgetLabel extends Widget {
	private FormattedText text;
	int textColor = 0xFFFFFF;
	protected Style style = Style.EMPTY;

	public WidgetLabel(String text) {
		super();
		this.text = FormattedText.of(text, style);

		this.setWidth(100);
		this.setHeight(9);
	}

	public WidgetLabel(String text, Function<Style, Style> styleSetter) {
		this(text);
		this.applyStyle(styleSetter);
	}

	public int getLabelWidth() {
		return Minecraft.getInstance().font.width(text);
	}

	public void autoWidth() {
		this.setWidth(getLabelWidth() + 2);
	}

	public void autoWidth(int maxWidth) {
		this.setWidth(Math.min(getLabelWidth() + 2, maxWidth));
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public int textColor() {
		return textColor;
	}

	public FormattedText formattedText() {
		return text;
	}

	public String text() {
		return text.getString();
	}

	public WidgetLabel setText(String text) {
		this.text = FormattedText.of(text, style);
		return this;
	}

	public WidgetLabel applyStyle(Function<Style, Style> styleSetter) {
		this.style = styleSetter.apply(this.style);
		this.text = FormattedText.of(text.getString(), style);
		return this;
	}

	public WidgetLabel clearStyle() {
		this.style = Style.EMPTY;
		this.text = FormattedText.of(text.getString(), style);
		return this;
	}

	@Override
	public void draw(GuiGraphics pGuiGraphics, Screen screen) {
		if(text == null) {
			return;
		}

		pGuiGraphics.pose().pushPose();
		RenderSystem.enableBlend();

		int scale = computeGuiScale(screen.getMinecraft());
		int bottomOffset = (int) (((double) (screen.getMinecraft().getWindow().getHeight() / scale) - (getActualY() + height)) * scale);
		int heightTmp = (height * scale) - 1;
		if(heightTmp < 0) {
			heightTmp = 0;
		}

		RenderSystem.enableScissor(getActualX() * scale - 3, bottomOffset + 2, width * scale, heightTmp);
		GUIHelper.drawWordWrap(pGuiGraphics, screen.getMinecraft().font, text, 0, 0, Integer.MAX_VALUE, textColor);
		RenderSystem.disableScissor();

		RenderSystem.disableBlend();
		pGuiGraphics.pose().popPose();
	}
}
