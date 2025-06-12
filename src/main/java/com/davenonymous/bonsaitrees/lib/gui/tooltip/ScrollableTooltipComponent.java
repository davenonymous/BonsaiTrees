package com.davenonymous.bonsaitrees.lib.gui.tooltip;

import com.davenonymous.bonsaitrees.BonsaiTrees;
import com.davenonymous.bonsaitrees.lib.gui.event.MouseScrollEvent;
import com.davenonymous.bonsaitrees.lib.gui.event.WidgetEventResult;
import com.davenonymous.bonsaitrees.lib.gui.widgets.Widget;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public class ScrollableTooltipComponent implements TooltipComponent, ClientTooltipComponent {
	private ClientTooltipComponent component;
	private int maxHeight;

	private float scrollOffset;
	private Widget scollSource;

	public ScrollableTooltipComponent(ClientTooltipComponent component, int maxHeight, Widget scrollSource) {
		this.component = component;
		this.maxHeight = maxHeight;
		this.scollSource = scrollSource;

		scollSource.addListener(
			MouseScrollEvent.class, (event, widget) -> {
				this.scrollUp((float) event.rawScrollValue);
				return WidgetEventResult.HANDLED;
			}
		);
	}

	public void setScrollOffset(float scrollOffset) {
		this.scrollOffset = scrollOffset;
	}

	public float getScrollOffset() {
		return scrollOffset;
	}

	public void scrollUp(float amount) {
		int scale = Widget.computeGuiScale(Minecraft.getInstance());

		scrollOffset += amount * scale;

		if(-scrollOffset < 0) {
			scrollOffset = 0;
		}

		BonsaiTrees.LOGGER.info("Widget={}, offset={}, compHeight={}, maxHeight={}", component.toString(), scrollOffset, component.getHeight(), maxHeight);
		//		if(-scrollOffset < 0) {
		//			scrollOffset = 0;
		//		}
		//
		//		if(-scrollOffset > component.getHeight() - maxHeight) {
		//			scrollOffset = component.getHeight() - maxHeight;
		//		}
	}


	@Override
	public int getHeight() {
		return Math.min(maxHeight, component.getHeight());
	}

	@Override
	public int getWidth(Font font) {
		return component.getWidth(font);
	}

	@Override
	public void renderImage(Font font, int x, int y, GuiGraphics pGuiGraphics) {

		int scale = Widget.computeGuiScale(Minecraft.getInstance());
		int bottomOffset = (int) (((double) (Minecraft.getInstance().getWindow().getHeight() / scale) - (y + getHeight())) * scale);
		int heightTmp = (getHeight() * scale) - 1;
		if(heightTmp < 0) {
			heightTmp = 0;
		}
		RenderSystem.enableScissor(x * scale - 3, bottomOffset + 2, getWidth(font) * scale, heightTmp);
		pGuiGraphics.pose().pushPose();
		pGuiGraphics.pose().translate(0, scrollOffset * 8, 0);
		component.renderImage(font, x, y, pGuiGraphics);
		pGuiGraphics.pose().popPose();
		RenderSystem.disableScissor();
	}
}
