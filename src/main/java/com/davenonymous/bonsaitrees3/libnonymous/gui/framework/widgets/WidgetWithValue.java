package com.davenonymous.bonsaitrees3.libnonymous.gui.framework.widgets;


import com.davenonymous.bonsaitrees3.libnonymous.gui.framework.event.ValueChangedEvent;
import net.minecraft.resources.ResourceLocation;

public class WidgetWithValue<T> extends Widget implements IValueProvider<T> {
	public ResourceLocation id;
	T value;

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public T getValue() {
		return this.value;
	}

	public void setValue(T newValue) {
		T tmpVal = this.value;
		this.value = newValue;
		this.fireEvent(new ValueChangedEvent<T>(tmpVal, this.value));
	}

	public void valueChanged(T oldValue, T newValue) {
		this.value = newValue;
		this.fireEvent(new ValueChangedEvent<T>(oldValue, this.value));
	}

	@Override
	public void setId(ResourceLocation id) {
		this.id = id;
	}
}
