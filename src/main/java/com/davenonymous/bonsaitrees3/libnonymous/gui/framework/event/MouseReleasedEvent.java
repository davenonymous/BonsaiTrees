package com.davenonymous.bonsaitrees3.libnonymous.gui.framework.event;

public class MouseReleasedEvent implements IEvent {
	public int button;
	public double x;
	public double y;

	public MouseReleasedEvent(double mouseX, double mouseY, int button) {
		this.x = mouseX;
		this.y = mouseY;
		this.button = button;
	}

	public boolean isLeftClick() {
		return button == 0;
	}

	@Override
	public String toString() {
		return String.format("MouseReleased[x=%.2f,y=%.2f,button=%d]", this.x, this.y, this.button);
	}
}
