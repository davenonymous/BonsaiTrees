package com.davenonymous.bonsaitrees.lib.gui.event;

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