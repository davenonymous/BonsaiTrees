package com.davenonymous.bonsaitrees.lib.gui.event;

import com.davenonymous.bonsaitrees.lib.gui.widgets.Widget;

public class ListSelectionEvent implements IEvent {
	public int selectedEntry;
	public Widget selectedWidget;

	public ListSelectionEvent(int selectedEntry, Widget selectedWidget) {
		this.selectedEntry = selectedEntry;
		this.selectedWidget = selectedWidget;
	}

	@Override
	public String toString() {
		return "ListSelectionEvent{" + "selectedEntry=" + selectedEntry + '}';
	}
}
