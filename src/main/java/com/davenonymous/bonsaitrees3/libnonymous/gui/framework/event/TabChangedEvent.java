package com.davenonymous.bonsaitrees3.libnonymous.gui.framework.event;

import com.davenonymous.bonsaitrees3.libnonymous.gui.framework.widgets.WidgetPanel;

public class TabChangedEvent extends ValueChangedEvent<WidgetPanel> {
	public TabChangedEvent(WidgetPanel oldValue, WidgetPanel newValue) {
		super(oldValue, newValue);
	}
}
