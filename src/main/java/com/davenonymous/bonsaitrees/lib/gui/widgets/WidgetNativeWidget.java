package com.davenonymous.bonsaitrees.lib.gui.widgets;


import com.davenonymous.bonsaitrees.lib.gui.WidgetScreen;
import com.davenonymous.bonsaitrees.lib.gui.event.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;


public class WidgetNativeWidget<T extends AbstractWidget> extends Widget {
	private final T nativeWidget;
	private boolean drawBackground = false;
	private int backgroundColor = (0x80 << 24) + ChatFormatting.DARK_GRAY.getColor();

	public WidgetNativeWidget(T nativeWidget) {
		super();
		this.nativeWidget = nativeWidget;

		this.addListener(
			FocusChangedEvent.class, ((event, widget) -> {
				nativeWidget.setFocused(false);
				return WidgetEventResult.CONTINUE_PROCESSING;
			})
		);

		this.addListener(
			VisibilityChangedEvent.class, ((event, widget) -> {
				nativeWidget.visible = this.isVisible();
				return WidgetEventResult.CONTINUE_PROCESSING;
			})
		);

		this.addListener(
			WidgetSizeChangeEvent.class, ((event, widget) -> {
				nativeWidget.setWidth(this.width);
				nativeWidget.setHeight(this.height);
				return WidgetEventResult.CONTINUE_PROCESSING;
			})
		);

		this.addListener(
			KeyPressedEvent.class, ((event, widget) -> {
				if(nativeWidget.keyPressed(event.keyCode, event.scanCode, event.modifiers)) {
					return WidgetEventResult.HANDLED;
				}
				return WidgetEventResult.CONTINUE_PROCESSING;
			})
		);

		this.addListener(
			CharTypedEvent.class, ((event, widget) -> {
				if(nativeWidget.charTyped(event.chr, event.scanCode)) {
					return WidgetEventResult.HANDLED;
				}
				return WidgetEventResult.CONTINUE_PROCESSING;
			})
		);

		this.addListener(
			MouseScrollEvent.class, ((event, widget) -> {
				if(nativeWidget.mouseScrolled(event.mouseX - this.getActualX(), event.mouseY - this.getActualY(), 0, event.rawScrollValue)) {
					return WidgetEventResult.HANDLED;
				}
				return WidgetEventResult.CONTINUE_PROCESSING;
			})
		);

		this.addListener(
			KeyReleasedEvent.class, ((event, widget) -> {
				if(nativeWidget.keyReleased(event.keyCode, event.scanCode, event.modifiers)) {
					return WidgetEventResult.HANDLED;
				}
				return WidgetEventResult.CONTINUE_PROCESSING;
			})
		);

		this.addListener(
			MouseClickEvent.class, ((event, widget) -> {
				nativeWidget.setFocused(true);
				if(nativeWidget.mouseClicked(event.x - this.getActualX(), event.y - this.getActualY(), event.button)) {
					return WidgetEventResult.HANDLED;
				}
				return WidgetEventResult.CONTINUE_PROCESSING;
			})
		);

		this.addListener(
			MouseReleasedEvent.class, ((event, widget) -> {
				if(nativeWidget.mouseReleased(event.x - this.getActualX(), event.y - this.getActualY(), event.button)) {
					return WidgetEventResult.HANDLED;
				}
				return WidgetEventResult.CONTINUE_PROCESSING;
			})
		);

		this.addListener(
			MouseDraggedEvent.class, ((event, widget) -> {
				if(nativeWidget.mouseDragged(event.mouseX() - this.getActualX(), event.mouseY() - this.getActualY(), event.button(), event.dragX(), event.dragY())) {
					return WidgetEventResult.HANDLED;
				}
				return WidgetEventResult.CONTINUE_PROCESSING;
			})
		);

		this.addListener(
			MouseMoveEvent.class, ((event, widget) -> {
				nativeWidget.mouseMoved(event.x - this.getActualX(), event.y - this.getActualY());
				return WidgetEventResult.CONTINUE_PROCESSING;
			})
		);
	}

	public T nativeWidget() {
		return nativeWidget;
	}

	public WidgetNativeWidget<T> setBackgroundColor(int backgroundColor) {
		this.backgroundColor = backgroundColor;
		return this;
	}

	public WidgetNativeWidget<T> setDrawBackground(boolean drawBackground) {
		this.drawBackground = drawBackground;
		return this;
	}

	@Override
	public void draw(GuiGraphics pGuiGraphics, Screen screen) {
		int mouseX = 0;
		int mouseY = 0;
		float partialTicks = 0;
		if(screen instanceof WidgetScreen ws) {
			mouseX = ws.mouseX();
			mouseY = ws.mouseY();
			partialTicks = ws.partialTicks();
		}

		if(drawBackground) {
			pGuiGraphics.fill(0, 0, this.width, this.height, backgroundColor);
		}
		nativeWidget.render(pGuiGraphics, mouseX, mouseY, partialTicks);
	}
}
