package com.davenonymous.bonsaitrees3.libnonymous.gui.framework.widgets;


import com.davenonymous.bonsaitrees3.libnonymous.gui.framework.CircularPointedArrayList;
import com.davenonymous.bonsaitrees3.libnonymous.gui.framework.event.MouseClickEvent;
import com.davenonymous.bonsaitrees3.libnonymous.gui.framework.event.ValueChangedEvent;
import com.davenonymous.bonsaitrees3.libnonymous.gui.framework.event.WidgetEventResult;

import java.util.Collection;

public class WidgetWithChoiceValue<T> extends Widget {
	CircularPointedArrayList<T> choices;

	public WidgetWithChoiceValue() {
		choices = new CircularPointedArrayList<>();
	}

	public T getValue() {
		return this.choices.getPointedElement();
	}

	public void setValue(T choice) {
		this.setValue(choice, true);
	}

	public void setValue(T choice, boolean fireEvent) {
		T oldValue = choices.getPointedElement();
		choices.setPointerTo(choice);
		if(fireEvent) {
			this.fireEvent(new ValueChangedEvent<T>(oldValue, choice));
		}
	}

	public void addChoice(T... newChoices) {
		for(T newChoice : newChoices) {
			this.choices.add(newChoice);
		}
	}

	public void addChoiceFromArray(T[] newChoices) {
		for(T newChoice : newChoices) {
			this.choices.add(newChoice);
		}
	}

	public void addChoice(Collection<T> newChoices) {
		this.choices.addAll(newChoices);
	}

	public void next() {
		T oldValue = choices.getPointedElement();
		T newValue = choices.next();
		this.fireEvent(new ValueChangedEvent<T>(oldValue, newValue));
	}

	public void prev() {
		T oldValue = choices.getPointedElement();
		T newValue = choices.prev();
		this.fireEvent(new ValueChangedEvent<T>(oldValue, newValue));
	}

	public void addClickListener() {
		this.addListener(MouseClickEvent.class, (event, widget) -> {
			if(event.isLeftClick()) {
				((WidgetWithChoiceValue<T>) widget).next();
			} else {
				((WidgetWithChoiceValue<T>) widget).prev();
			}

			return WidgetEventResult.HANDLED;
		});
	}
}
