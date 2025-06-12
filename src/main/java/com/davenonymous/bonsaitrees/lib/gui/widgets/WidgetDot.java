package com.davenonymous.bonsaitrees.lib.gui.widgets;

import com.davenonymous.bonsaitrees.lib.gui.Icons;
import net.minecraft.ChatFormatting;

public class WidgetDot extends WidgetImage {
	public WidgetDot(int color) {
		super(Icons.guiDot);
		this.setSize(10, 10);
		this.setTextureSize(10, 10);
		if(color > 0xFFFFFF) {
			color = color & 0x00FFFFFF;
			this.alpha = (color >> 24 & 0xFF) / 255.0F;
		}
		this.setColor(color);
		this.setAlpha(1.0f);
	}

	public WidgetDot(ChatFormatting color) {
		this(color.getColor());
	}
}
