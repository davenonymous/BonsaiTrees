package com.davenonymous.bonsaitrees3.libnonymous.gui.framework.widgets;

import com.davenonymous.bonsaitrees3.BonsaiTrees3;
import com.davenonymous.bonsaitrees3.libnonymous.gui.framework.GUI;
import com.davenonymous.bonsaitrees3.libnonymous.gui.framework.event.ValueChangedEvent;
import com.davenonymous.bonsaitrees3.libnonymous.gui.framework.event.WidgetEventResult;
import com.davenonymous.bonsaitrees3.libnonymous.helper.RedstoneMode;
import com.davenonymous.bonsaitrees3.libnonymous.helper.Translatable;

public class WidgetRedstoneMode extends WidgetSpriteSelect<RedstoneMode> {
	public static final Translatable REDSTONE_IGNORE = new Translatable(BonsaiTrees3.MODID, "button.redstone.tooltip.ignore");
	public static final Translatable REDSTONE_REQUIRED = new Translatable(BonsaiTrees3.MODID, "button.redstone.tooltip.required");
	public static final Translatable REDSTONE_REJECTED = new Translatable(BonsaiTrees3.MODID, "button.redstone.tooltip.rejected");

	public WidgetRedstoneMode() {
		this(RedstoneMode.IGNORE_POWER);
	}

	public WidgetRedstoneMode(RedstoneMode initial) {
		this.addChoiceWithSprite(RedstoneMode.IGNORE_POWER, new SpriteData(GUI.tabIcons, 26, 84, 10, 10));
		this.addChoiceWithSprite(RedstoneMode.REQUIRE_POWER, new SpriteData(GUI.tabIcons, 36, 84, 4, 11));
		this.addChoiceWithSprite(RedstoneMode.REJECT_POWER, new SpriteData(GUI.tabIcons, 40, 84, 2, 11));
		this.setValue(initial);
		updateToolTips();

		this.addListener(ValueChangedEvent.class, (event, widget) -> {
			updateToolTips();
			return WidgetEventResult.CONTINUE_PROCESSING;
		});
	}

	public void updateToolTips() {
		if(this.getValue() == RedstoneMode.IGNORE_POWER) {
			this.setTooltipLines(REDSTONE_IGNORE);
		} else if(this.getValue() == RedstoneMode.REJECT_POWER) {
			this.setTooltipLines(REDSTONE_REJECTED);
		} else if(this.getValue() == RedstoneMode.REQUIRE_POWER) {
			this.setTooltipLines(REDSTONE_REQUIRED);
		}
	}
}