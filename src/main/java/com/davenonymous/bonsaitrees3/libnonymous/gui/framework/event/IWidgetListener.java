package com.davenonymous.bonsaitrees3.libnonymous.gui.framework.event;


import com.davenonymous.bonsaitrees3.libnonymous.gui.framework.widgets.Widget;

public interface IWidgetListener<T extends IEvent> {
	WidgetEventResult call(T event, Widget widget);
}
