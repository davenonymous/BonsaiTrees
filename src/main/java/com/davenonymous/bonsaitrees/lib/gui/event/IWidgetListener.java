package com.davenonymous.bonsaitrees.lib.gui.event;


import com.davenonymous.bonsaitrees.lib.gui.widgets.Widget;

public interface IWidgetListener<T extends IEvent> {
	WidgetEventResult call(T event, Widget widget);
}