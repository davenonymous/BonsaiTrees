package com.davenonymous.bonsaitrees.lib.gui.tooltip;

import com.davenonymous.bonsaitrees.lib.gui.GUI;
import com.davenonymous.bonsaitrees.lib.gui.RedstoneMode;

public class RedstoneModeTooltipComponent extends HBoxTooltipComponent {
	public RedstoneModeTooltipComponent(RedstoneMode mode) {
		super();

		this.setAlignment(BoxAlignment.CENTER);

		switch(mode) {
			case IGNORE_POWER -> {
				add(new SpriteTooltipComponent(GUI.tabIcons, 16, 16, 26, 84, 10, 10));
			}
			case REQUIRE_POWER -> {
				add(new SpriteTooltipComponent(GUI.tabIcons, 16, 16, 36, 84, 4, 11));
			}
			case STOP_ON_POWER -> {
				add(new SpriteTooltipComponent(GUI.tabIcons, 16, 16, 40, 84, 2, 11));
			}
		}

		add(StringTooltipComponent.gray(mode.getDescription().getString()));
	}
}
