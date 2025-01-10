package com.davenonymous.bonsaitrees.lib.gui.widgets;

import com.davenonymous.bonsaitrees.lib.gui.GUI;
import com.davenonymous.bonsaitrees.lib.gui.event.*;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.*;

public class Widget {
	public int x;
	public int y;
	public int width;
	public int height;

	boolean enabled = true;
	boolean focused = false;
	boolean visible = true;
	boolean hovered = false;
	Widget parent;

	List<Either<Component, TooltipComponent>> tooltipElements;

	Map<Class<? extends IEvent>, List<IWidgetListener>> eventListeners = new HashMap<>();
	List<IWidgetListener> anyEventListener = new ArrayList<>();

	public Widget() {
		this.addListener(MouseClickEvent.class, (event, widget) -> {
			widget.getRootWidget().fireEvent(new FocusChangedEvent());

			if(widget.focusable()) {
				widget.focused = true;
			}

			return WidgetEventResult.CONTINUE_PROCESSING;
		});

		this.addListener(FocusChangedEvent.class, ((event, widget) -> {
			widget.focused = false;
			return WidgetEventResult.CONTINUE_PROCESSING;
		}));

		this.addListener(MouseEnterEvent.class, (event, widget) -> {
			widget.hovered = true;
			return WidgetEventResult.CONTINUE_PROCESSING;
		});
		this.addListener(MouseExitEvent.class, (event, widget) -> {
			widget.hovered = false;
			return WidgetEventResult.CONTINUE_PROCESSING;
		});

	}

	public void setPosition(int x, int y) {
		this.setX(x);
		this.setY(y);
	}

	public void setSize(int width, int height) {
		this.setWidth(width);
		this.setHeight(height);
	}

	public void setDimensions(int x, int y, int width, int height) {
		this.setSize(width, height);
		this.setPosition(x, y);
	}

	public boolean hasToolTip() {
		return tooltipElements != null && !tooltipElements.isEmpty();
	}

	public List<Either<Component, TooltipComponent>> getTooltip() {
		if(tooltipElements == null) {
			return Collections.emptyList();
		}

		return tooltipElements;
	}

	public List<Either<FormattedText, TooltipComponent>> getTooltipFormatted() {
		if(tooltipElements == null) {
			return Collections.emptyList();
		}

		var result = new ArrayList<Either<FormattedText, TooltipComponent>>();
		for(Either<Component, TooltipComponent> element : tooltipElements) {
			if(element.left().isPresent()) {
				result.add(Either.left(FormattedText.of(element.left().get().getString(), element.left().get().getStyle())));
			} else {
				result.add(Either.right(element.right().get()));
			}
		}
		return result;
	}

	public Widget setTooltipLinesEither(List<Either<Component, TooltipComponent>> tooltipElements) {
		this.tooltipElements = tooltipElements;
		return this;
	}

	public Widget setTooltipLines(List<Component> tooltipElements) {
		this.tooltipElements = new ArrayList<>();
		for(Component line : tooltipElements) {
			this.tooltipElements.add(Either.left(line));
		}
		return this;
	}

	public Widget setTooltipLines(Component... tooltipElements) {
		this.tooltipElements = new ArrayList<>();
		for(Component line : tooltipElements) {
			this.tooltipElements.add(Either.left(line));
		}
		return this;
	}

	public Widget setTooltipElements(List<TooltipComponent> tooltipElements) {
		this.tooltipElements = new ArrayList<>();
		for(TooltipComponent line : tooltipElements) {
			this.tooltipElements.add(Either.right(line));
		}
		return this;
	}

	public Widget setTooltipElements(TooltipComponent... tooltipElements) {
		this.tooltipElements = new ArrayList<>();
		for(TooltipComponent line : tooltipElements) {
			this.tooltipElements.add(Either.right(line));
		}
		return this;
	}

	public Widget setTooltipEither(Either<Component, TooltipComponent>... tooltipElements) {
		this.tooltipElements = new ArrayList<>();
		Collections.addAll(this.tooltipElements, tooltipElements);
		return this;
	}

	public Widget addTooltipLine(Component... tooltipElements) {
		for(Component line : tooltipElements) {
			this.tooltipElements.add(Either.left(line));
		}
		return this;
	}

	public Widget addTooltipLine(List<Component> tooltipElements) {
		for(Component line : tooltipElements) {
			this.tooltipElements.add(Either.left(line));
		}
		return this;
	}

	public Widget addTooltipElement(TooltipComponent... tooltipElements) {
		for(TooltipComponent line : tooltipElements) {
			this.tooltipElements.add(Either.right(line));
		}
		return this;
	}

	public Widget addTooltipElement(List<TooltipComponent> tooltipElements) {
		for(TooltipComponent line : tooltipElements) {
			this.tooltipElements.add(Either.right(line));
		}
		return this;
	}

	public Widget addTooltipEither(Either<Component, TooltipComponent>... tooltipElements) {
		Collections.addAll(this.tooltipElements, tooltipElements);
		return this;
	}

	public Widget addTooltipEither(List<Either<Component, TooltipComponent>> elements) {
		this.tooltipElements.addAll(elements);
		return this;
	}

	public Widget insertTooltipLine(int index, Component line) {
		this.tooltipElements.add(index, Either.left(line));
		return this;
	}

	public Widget insertTooltipElement(int index, TooltipComponent line) {
		this.tooltipElements.add(index, Either.right(line));
		return this;
	}

	public Widget insertTooltipEither(int index, Either<Component, TooltipComponent> line) {
		this.tooltipElements.add(index, line);
		return this;
	}

	public boolean areAllParentsVisible() {
		return isVisible() && (parent == null || parent.areAllParentsVisible());
	}

	public boolean focusable() {
		return true;
	}

	public static int computeGuiScale(Minecraft mc) {
		int scaleFactor = 1;

		int k = mc.options.guiScale().get();

		if(k == 0) {
			k = 1000;
		}

		while(scaleFactor < k && mc.getWindow().getWidth() / (scaleFactor + 1) >= 320 && mc.getWindow().getHeight() / (scaleFactor + 1) >= 240) {
			++scaleFactor;
		}
		return scaleFactor;
	}


	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[?]";
	}

	public boolean isPosInside(double x, double y) {
		boolean isInsideX = this.getActualX() <= x && x < this.getActualX() + this.width;
		//Logz.info("[%s] insideX: %d < %d <= %d --> %s", this.id, this.lastDrawX, x, this.lastDrawX + this.lastDrawWidth, this.toString());

		boolean isInsideY = this.getActualY() <= y && y < this.getActualY() + this.height;
		//Logz.info("[%s] insideY: %d < %d <= %d --> %s", this.id, this.lastDrawY, y, this.lastDrawY + this.lastDrawHeight, this.toString());

		return isInsideX && isInsideY;
	}

	public int getActualX() {
		int result = this.x;
		Widget parent = this.parent;
		while(parent != null) {
			result += parent.x;
			parent = parent.parent;
		}

		return result;
	}

	public int getActualY() {
		int result = this.y;
		Widget parent = this.parent;
		while(parent != null) {
			result += parent.y;
			parent = parent.parent;
		}

		return result;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setVisible(boolean visible) {
		boolean oldVisible = this.visible;
		this.visible = visible;
		this.fireEvent(new VisibilityChangedEvent(oldVisible, visible));
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setDisabled() {
		this.enabled = false;
	}

	public void setParent(Widget parent) {
		this.parent = parent;
	}

	public void setFocused(boolean focused) {
		this.focused = focused;
	}

	public Widget getRootWidget() {
		if(this.parent == null) {
			return this;
		}

		return this.parent.getRootWidget();
	}

	public GUI getGUI() {
		Widget root = getRootWidget();
		if(root instanceof GUI) {
			return (GUI) root;
		}

		return null;
	}

	/**
	 * Use this in your Screens drawScreen() method and pass it as parameter.
	 * This draws the Gui on the screen.
	 * <p>
	 * Do not override this. Override the draw() method instead.
	 *
	 * @param pGuiGraphics
	 * @param screen
	 */
	public void shiftAndDraw(GuiGraphics pGuiGraphics, Screen screen) {
		this.drawBeforeShift(pGuiGraphics, screen);

		pGuiGraphics.pose().pushPose();
		pGuiGraphics.pose().translate(this.x, this.y, 0);
		this.draw(pGuiGraphics, screen);
		pGuiGraphics.pose().popPose();
	}

	/**
	 * Override this or draw() to implement your own drawing logic.
	 * <p>
	 * The GLState is not shifted to this widgets x and y coordinates when
	 * overriding this method.
	 *
	 * @param screen
	 */
	public void drawBeforeShift(GuiGraphics pGuiGraphics, Screen screen) {

	}

	/**
	 * Override this or drawBeforeShift() to implement your own drawing logic.
	 * <p>
	 * The GLState is already positioned at the correct coordinates, i.e. your
	 * x and y coordinates start at 0.
	 *
	 * @param screen
	 */
	public void draw(GuiGraphics pGuiGraphics, Screen screen) {
		//Logz.debug("Drawing widget: %s, x=%d, y=%d, width=%d, height=%d", this, layoutResult.getX(), layoutResult.getY(), layoutResult.getWidth(), layoutResult.getHeight());
	}

	public <T extends IEvent> void addListener(Class<T> eventClass, IWidgetListener<? super T> listener) {
		if(!eventListeners.containsKey(eventClass)) {
			eventListeners.put(eventClass, new ArrayList<>());
		}

		eventListeners.get(eventClass).add(listener);
	}

	public void addAnyListener(IWidgetListener listener) {
		anyEventListener.add(listener);
	}

	public <T extends IEvent> void addChildListener(Class<T> eventClass, Widget receiveEventsFromWidget) {
		Widget self = this;
		receiveEventsFromWidget.addListener(eventClass, (event, widget) -> self.fireEvent(event));
	}

	public WidgetEventResult fireEvent(IEvent event) {
		for(IWidgetListener listener : anyEventListener) {
			WidgetEventResult immediateResult = listener.call(event, this);
			if(immediateResult == WidgetEventResult.HANDLED) {
				return WidgetEventResult.HANDLED;
			}
		}

		if(!eventListeners.containsKey(event.getClass())) {
			return WidgetEventResult.CONTINUE_PROCESSING;
		}

		for(IWidgetListener listener : eventListeners.get(event.getClass())) {
			WidgetEventResult immediateResult = listener.call(event, this);
			if(immediateResult == WidgetEventResult.HANDLED) {
				return WidgetEventResult.HANDLED;
			}
		}

		return WidgetEventResult.CONTINUE_PROCESSING;
	}

	public boolean isVisible() {
		return this.visible;
	}


}