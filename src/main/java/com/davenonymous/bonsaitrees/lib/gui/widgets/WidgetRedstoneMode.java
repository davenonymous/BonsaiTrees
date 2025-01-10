package com.davenonymous.bonsaitrees.lib.gui.widgets;

import com.davenonymous.bonsaitrees.lib.gui.GUI;
import com.davenonymous.bonsaitrees.lib.gui.RedstoneMode;
import com.davenonymous.bonsaitrees.lib.gui.event.ValueChangedEvent;
import com.davenonymous.bonsaitrees.lib.gui.event.WidgetEventResult;


public class WidgetRedstoneMode extends WidgetSpriteSelect<RedstoneMode> {

	public WidgetRedstoneMode() {
		this(RedstoneMode.IGNORE_POWER);
	}

	public WidgetRedstoneMode(RedstoneMode initial) {
		this.addChoiceWithSprite(RedstoneMode.IGNORE_POWER, new SpriteData(GUI.tabIcons, 26, 84, 10, 10));
		this.addChoiceWithSprite(RedstoneMode.REQUIRE_POWER, new SpriteData(GUI.tabIcons, 36, 84, 4, 11));
		this.addChoiceWithSprite(RedstoneMode.STOP_ON_POWER, new SpriteData(GUI.tabIcons, 40, 84, 2, 11));
		this.setValue(initial);
		updateToolTips();

		this.addListener(ValueChangedEvent.class, (event, widget) -> {
			updateToolTips();
			return WidgetEventResult.CONTINUE_PROCESSING;
		});
	}

	public void updateToolTips() {
		this.setTooltipLines(this.getValue().getDescription());
	}
}
