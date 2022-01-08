package com.davenonymous.bonsaitrees3.libnonymous.gui.framework.widgets;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;

public class WidgetTextBox extends Widget {
	private String text;
	private int textColor = 0xFFFFFF;

	public WidgetTextBox(String text) {
		this.text = text;
		this.setWidth(100);
		this.setHeight(9);
	}

	public WidgetTextBox(String text, int textColor) {
		this.text = text;
		this.textColor = textColor;
		this.setWidth(100);
		this.setHeight(9);
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	@Override
	public void draw(PoseStack pPoseStack, Screen screen) {
		if(text == null) {
			return;
		}

		pPoseStack.pushPose();
		RenderSystem.enableBlend();

		int scale = computeGuiScale(screen.getMinecraft());
		int bottomOffset = (int) (((double) (screen.getMinecraft().getWindow().getHeight() / scale) - (getActualY() + height)) * scale);
		int heightTmp = (height * scale) - 1;
		if(heightTmp < 0) {
			heightTmp = 0;
		}

		RenderSystem.enableScissor(getActualX() * scale, bottomOffset + 2, width * scale, heightTmp);
		screen.getMinecraft().font.draw(pPoseStack, text, 0, 0, textColor);
		RenderSystem.disableScissor();

		RenderSystem.disableBlend();
		pPoseStack.popPose();
	}
}
