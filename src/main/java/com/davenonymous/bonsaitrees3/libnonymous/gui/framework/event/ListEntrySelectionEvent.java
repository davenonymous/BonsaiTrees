package com.davenonymous.bonsaitrees3.libnonymous.gui.framework.event;

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
