package com.davenonymous.bonsaitrees.lib.gui.event;

public class ListEntrySelectionEvent implements IEvent {
	public boolean selected;

	public ListEntrySelectionEvent(boolean selected) {
		this.selected = selected;
	}

	@Override
	public String toString() {
		return "ListEntrySelectionEvent{" + "selected=" + selected + '}';
	}
}