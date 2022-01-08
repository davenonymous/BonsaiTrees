package com.davenonymous.bonsaitrees3.libnonymous.gui.framework.event;

public class ListSelectionEvent implements IEvent {
	public int selectedEntry;

	public ListSelectionEvent(int selectedEntry) {
		this.selectedEntry = selectedEntry;
	}

	@Override
	public String toString() {
		return "ListSelectionEvent{" + "selectedEntry=" + selectedEntry + '}';
	}
}
