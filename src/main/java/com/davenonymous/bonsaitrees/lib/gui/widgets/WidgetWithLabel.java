package com.davenonymous.bonsaitrees.lib.gui.widgets;

import com.davenonymous.bonsaitrees.lib.gui.ISelectable;
import com.davenonymous.bonsaitrees.lib.gui.event.WidgetEventResult;
import com.davenonymous.bonsaitrees.lib.gui.event.WidgetSizeChangeEvent;
import net.minecraft.network.chat.Style;

import java.util.function.Function;

public class WidgetWithLabel<T extends Widget> extends WidgetPanel implements ISelectable {
	T widget;
	WidgetLabel label;
	int labelXOffset = 0;
	int labelYOffset = 0;

	public WidgetWithLabel(T pWidget, String labelText) {
		super();

		this.widget = pWidget;
		this.widget.setPosition(0, 0);

		this.label = new WidgetLabel(labelText);
		this.add(label);
		this.add(widget);

		this.addListener(
			WidgetSizeChangeEvent.class, (event, ignored) -> {
				label.setX(widget.width + 2 + labelXOffset);
				label.setY(((widget.height - (label.height - 2)) / 2) + labelYOffset);
				label.setWidth(this.width - widget.width - 2 - labelXOffset);
				return WidgetEventResult.HANDLED;
			}
		);
	}

	public WidgetLabel label() {
		return label;
	}

	public void setText(String text) {
		this.label.setText(text);
	}

	public void applyLabelStyle(Function<Style, Style> styleSetter) {
		this.label.applyStyle(styleSetter);
	}

	public WidgetWithLabel<T> setLabelOffset(int labelXOffset, int labelYOffset) {
		this.labelXOffset = labelXOffset;
		this.labelYOffset = labelYOffset;
		return this;
	}

	public WidgetWithLabel<T> setLabelXOffset(int labelXOffset) {
		this.labelXOffset = labelXOffset;
		return this;
	}

	public WidgetWithLabel<T> setLabelYOffset(int labelYOffset) {
		this.labelYOffset = labelYOffset;
		return this;
	}

	@Override
	public boolean isSelected() {
		return false;
	}

	@Override
	public void setSelected(boolean state) {
	}
}
