package com.davenonymous.bonsaitrees.lib.gui.widgets;


import com.davenonymous.bonsaitrees.lib.gui.event.MouseClickEvent;
import com.davenonymous.bonsaitrees.lib.gui.event.MouseEnterEvent;
import com.davenonymous.bonsaitrees.lib.gui.event.MouseExitEvent;
import com.davenonymous.bonsaitrees.lib.gui.event.WidgetEventResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

import java.util.function.Function;


public class WidgetButton extends Widget {
	public boolean hovered = false;
	protected static final WidgetSprites SPRITES = new WidgetSprites(
		ResourceLocation.withDefaultNamespace("widget/button"), ResourceLocation.withDefaultNamespace("widget/button_disabled"),
		ResourceLocation.withDefaultNamespace("widget/button_highlighted")
	);
	public Component label;
	public String fixedLabel;
	protected Style style = Style.EMPTY;

	private WidgetButton() {
		this.setHeight(20);
		this.setWidth(100);

		this.addListener(
			MouseClickEvent.class, ((event, widget) -> {
				Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
				return WidgetEventResult.CONTINUE_PROCESSING;
			})
		);

		this.addListener(
			MouseEnterEvent.class, (event, widget) -> {
				((WidgetButton) widget).hovered = true;
				return WidgetEventResult.CONTINUE_PROCESSING;
			}
		);
		this.addListener(
			MouseExitEvent.class, (event, widget) -> {
				((WidgetButton) widget).hovered = false;
				return WidgetEventResult.CONTINUE_PROCESSING;
			}
		);
	}

	public WidgetButton(Component label) {
		this();
		this.label = label;
	}

	public WidgetButton(String label) {
		this();
		this.fixedLabel = label;
	}

	public WidgetButton setStyle(Function<Style, Style> style) {
		this.style = style.apply(this.style);
		return this;
	}

	public void autoWidth() {
		String toDraw = fixedLabel != null ? fixedLabel : I18n.get(label.getString());
		this.setWidth(Minecraft.getInstance().font.width(FormattedText.of(toDraw, style)) + 10);
	}

	public WidgetButton setLabel(Component label) {
		this.label = label;
		return this;
	}

	public WidgetButton setLabel(String label) {
		this.fixedLabel = label;
		return this;
	}

	@Override
	public void draw(GuiGraphics pGuiGraphics, Screen screen) {
		pGuiGraphics.blitSprite(SPRITES.get(this.enabled, this.isHovered()), 0, 0, this.width, this.height);

		pGuiGraphics.pose().pushPose();
		Font fontrenderer = screen.getMinecraft().font;
		pGuiGraphics.pose().translate(0f, 0f, 10f);
		drawButtonContent(pGuiGraphics, screen, fontrenderer);
		pGuiGraphics.pose().translate(0f, 0f, -10f);
		pGuiGraphics.pose().popPose();
	}

	protected void drawButtonContent(GuiGraphics pGuiGraphics, Screen screen, Font renderer) {
		drawString(pGuiGraphics, screen, renderer);
	}

	protected void drawString(GuiGraphics pGuiGraphics, Screen screen, Font renderer) {
		int color = 0xFFFFFF;
		String toDraw = fixedLabel != null ? fixedLabel : I18n.get(label.getString());
		pGuiGraphics.drawCenteredString(screen.getMinecraft().font, toDraw, (int) (width / 2.0f), (int) ((height - 8) / 2.0f), color);
	}
}
