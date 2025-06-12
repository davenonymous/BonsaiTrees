package com.davenonymous.bonsaitrees.lib.gui.widgets;

import com.davenonymous.bonsaitrees.lib.gui.event.ValueChangedEvent;
import com.davenonymous.bonsaitrees.lib.gui.event.WidgetEventResult;
import com.davenonymous.bonsaitrees.lib.gui.event.WidgetSizeChangeEvent;
import net.minecraft.ChatFormatting;

public class WidgetCheckboxWithLabel extends WidgetPanelWithValue<Boolean> {
	private WidgetCheckbox checkbox;
	private WidgetTextBox label;

	public WidgetCheckboxWithLabel(String label) {
		super();

		this.checkbox = new WidgetCheckbox();
		this.setHeight(this.checkbox.height);
		this.add(checkbox);

		this.label = new WidgetTextBox(label);
		this.label.setPosition(this.checkbox.width + 4, 2);
		this.label.setHeight(this.checkbox.height);
		this.label.setWordWrap(true);
		this.label.autoWidth();
		this.label.setStyle(style -> style.withColor(ChatFormatting.DARK_GRAY));
		this.add(this.label);
		this.addChildListener(ValueChangedEvent.class, this.checkbox);

		this.addListener(
			WidgetSizeChangeEvent.class, (event, widget) -> {
				this.label.autoWidth();
				if(this.label.width > this.width - checkbox.width - 4) {
					this.label.setWidth(this.width - checkbox.width - 4);
				}

				return WidgetEventResult.CONTINUE_PROCESSING;
			}
		);
	}

	public WidgetTextBox label() {
		return label;
	}

	@Override
	public Boolean getValue() {
		return checkbox.getValue();
	}

	@Override
	public void setValue(Boolean value) {
		checkbox.setValue(value);
	}
}
