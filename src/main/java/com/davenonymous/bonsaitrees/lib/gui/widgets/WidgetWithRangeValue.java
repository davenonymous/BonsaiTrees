package com.davenonymous.bonsaitrees.lib.gui.widgets;

public class WidgetWithRangeValue<T extends Number> extends WidgetWithValue<T> {
	protected T rangeMin;
	protected T rangeMax;

	public T getRangeMin() {
		return rangeMin;
	}

	public T getRangeMax() {
		return rangeMax;
	}

	public WidgetWithRangeValue<T> setRange(T min, T max) {
		this.rangeMin = min;
		this.rangeMax = max;
		return this;
	}

	public WidgetWithRangeValue<T> setRangeMin(T min) {
		this.rangeMin = min;
		return this;
	}

	public WidgetWithRangeValue<T> setRangeMax(T max) {
		this.rangeMax = max;
		return this;
	}
}