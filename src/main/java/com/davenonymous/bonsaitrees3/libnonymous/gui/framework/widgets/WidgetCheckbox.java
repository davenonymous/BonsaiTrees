package com.davenonymous.bonsaitrees3.libnonymous.gui.framework.widgets;


import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;

public class WidgetCheckbox extends WidgetSelectButton<Boolean> {
	public WidgetCheckbox() {
		this.addChoice(true, false);
		this.setWidth(10);
		this.setHeight(10);

		this.addClickListener();
	}

	@Override
	protected void drawButtonContent(PoseStack pPoseStack, Screen screen, Font fontrenderer) {
		if(this.getValue()) {
			// fontrenderer.drawString("x", 2.2f, 0.3f, 0xEEEEEE);
		}
	}
}
