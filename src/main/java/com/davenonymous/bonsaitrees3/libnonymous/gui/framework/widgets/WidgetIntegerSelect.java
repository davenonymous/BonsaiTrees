package com.davenonymous.bonsaitrees3.libnonymous.gui.framework.widgets;


import com.davenonymous.bonsaitrees3.libnonymous.gui.framework.event.MouseClickEvent;
import com.davenonymous.bonsaitrees3.libnonymous.gui.framework.event.ValueChangedEvent;
import com.davenonymous.bonsaitrees3.libnonymous.gui.framework.event.WidgetEventResult;

public class WidgetIntegerSelect extends WidgetPanelWithValue<Integer> {
	WidgetButton less;
	WidgetButton more;
	WidgetInputField input;
	private int number;


	public WidgetIntegerSelect(int min, int max, int init) {
		this(min, max, init, "");
	}

	public WidgetIntegerSelect(int min, int max, int init, String suffix) {
		super();

		input = new WidgetInputField();
		input.setValidator(s -> {
			boolean validChars = s.matches("^[0-9]*$");
			if(validChars && s.length() > 0) {
				int enteredValue = Integer.parseInt(s);
				if(enteredValue < min || enteredValue > max) {
					return false;
				}
			}

			return validChars;
		});
		input.addListener(ValueChangedEvent.class, (event, widget) -> {
			String newValue = (String) event.newValue;
			if(newValue == null || newValue.length() == 0) {
				this.number = 0;
			} else {
				this.number = Integer.parseInt(newValue);
			}
			return WidgetEventResult.HANDLED;
		});

		less = new WidgetButton("-");
		less.addListener(MouseClickEvent.class, (event, widget) -> {
			if(number - 1 >= min) {
				this.setNumber(number - 1);
			}

			return WidgetEventResult.HANDLED;
		});
		more = new WidgetButton("+");
		more.addListener(MouseClickEvent.class, (event, widget) -> {
			if(number + 1 <= max) {
				this.setNumber(number + 1);
			}

			return WidgetEventResult.HANDLED;
		});

		this.add(less);
		this.add(input);
		this.add(more);

		this.setNumber(init);
	}

	protected void setNumber(int number) {
		int oldNumber = this.number;
		this.number = number;
		this.input.setText("" + this.number);
		this.fireEvent(new ValueChangedEvent<>(oldNumber, this.number));
	}

	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);

		int buttonWidth = 10;

		less.setDimensions(0, -1, buttonWidth, height + 2);
		input.setDimensions(buttonWidth + 2, 0, width - ((2 * buttonWidth) + 4), height);
		more.setDimensions(width - (buttonWidth), -1, buttonWidth, height + 2);
	}

	@Override
	public Integer getValue() {
		return number;
	}

	@Override
	public void setValue(Integer value) {
		this.setNumber(value);
	}
}
