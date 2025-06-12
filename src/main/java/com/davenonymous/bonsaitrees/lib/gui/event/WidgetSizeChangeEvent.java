package com.davenonymous.bonsaitrees.lib.gui.event;

import com.davenonymous.bonsaitrees.lib.gui.widgets.Widget;

public record WidgetSizeChangeEvent(int oldWidth, int oldHeight, int newWidth, int newHeight, Widget changedWidget) implements IEvent {
	public boolean xChanged() {
		return oldWidth != newWidth;
	}

	public boolean yChanged() {
		return oldHeight != newHeight;
	}
}
