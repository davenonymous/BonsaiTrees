package com.davenonymous.bonsaitrees.lib.gui.widgets;


import com.davenonymous.bonsaitrees.lib.gui.event.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;


public abstract class AbstractWidgetButton<T extends Widget> extends WidgetPanel {
	protected static final WidgetSprites SPRITES = new WidgetSprites(
		ResourceLocation.withDefaultNamespace("widget/button"), ResourceLocation.withDefaultNamespace("widget/button_disabled"),
		ResourceLocation.withDefaultNamespace("widget/button_highlighted")
	);

	public boolean hovered = false;
	private T widget;

	protected AbstractWidgetButton(T pWidget) {
		this.setHeight(20);
		this.setWidth(100);
		this.widget = pWidget;
		this.add(pWidget);

		this.addListener(
			WidgetSizeChangeEvent.class, (event, widget) -> {
				int yOffset = (this.height - this.widget.height) / 2;
				if(yOffset < 0) {
					yOffset = 0;
				}

				int xOffset = (this.width - this.widget.width) / 2;
				if(xOffset < 0) {
					xOffset = 0;
				}

				this.widget.setPosition(xOffset, yOffset);
				return WidgetEventResult.HANDLED;
			}
		);

		this.addListener(
			MouseClickEvent.class, ((event, widget) -> {
				Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
				return WidgetEventResult.CONTINUE_PROCESSING;
			})
		);

		this.addListener(
			MouseEnterEvent.class, (event, widget) -> {
				this.hovered = true;
				return WidgetEventResult.CONTINUE_PROCESSING;
			}
		);
		this.addListener(
			MouseExitEvent.class, (event, widget) -> {
				this.hovered = false;
				return WidgetEventResult.CONTINUE_PROCESSING;
			}
		);
	}

	public T widget() {
		return widget;
	}

	public AbstractWidgetButton<T> setWidget(T widget) {
		this.widget = widget;
		return this;
	}

	@Override
	public void draw(GuiGraphics pGuiGraphics, Screen screen) {
		pGuiGraphics.blitSprite(SPRITES.get(this.enabled, this.isHovered()), 0, 0, this.width, this.height);

		pGuiGraphics.pose().pushPose();
		pGuiGraphics.pose().translate(0f, 0f, 10f);
		super.draw(pGuiGraphics, screen);
		pGuiGraphics.pose().popPose();
	}
}
