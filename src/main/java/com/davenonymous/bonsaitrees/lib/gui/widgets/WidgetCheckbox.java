package com.davenonymous.bonsaitrees.lib.gui.widgets;


import com.davenonymous.bonsaitrees.lib.gui.GUI;

public class WidgetCheckbox extends WidgetSpriteSelect<Boolean> {
	public WidgetCheckbox() {
		this.setSize(14, 11);

		this.addChoiceWithSprite(
			false,
			new WidgetSpriteSelect.SpriteData(GUI.tabIcons, 104, 0, 14, 11)
		);

		this.addChoiceWithSprite(
			true,
			new WidgetSpriteSelect.SpriteData(GUI.tabIcons, 76, 0, 14, 11)
		);

		this.setValue(false);
	}

}
