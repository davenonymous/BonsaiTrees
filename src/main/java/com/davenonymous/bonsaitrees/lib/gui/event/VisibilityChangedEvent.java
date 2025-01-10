package com.davenonymous.bonsaitrees.lib.gui.event;

public class VisibilityChangedEvent extends ValueChangedEvent<Boolean> {
	public VisibilityChangedEvent(Boolean oldValue, Boolean newValue) {
		super(oldValue, newValue);
	}
}