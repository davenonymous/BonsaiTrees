package com.davenonymous.bonsaitrees3.libnonymous.gui.framework.event;

public class MouseScrollEvent implements IEvent {
	public boolean up = false;
	public boolean down = false;


	public double mouseX;
	public double mouseY;
	public double rawScrollValue;

	public MouseScrollEvent(double mouseX, double mouseY, double rawScrollValue) {
		this.mouseX = mouseX;
		this.mouseY = mouseY;

		this.rawScrollValue = rawScrollValue;
		if(rawScrollValue < 0) {
			this.down = true;
		} else {
			this.up = true;
		}
	}

	@Override
	public String toString() {
		return String.format("MouseScroll[x=%.1f,y=%.1f,value=%.0f]", this.mouseX, this.mouseY, this.rawScrollValue);
	}
}
