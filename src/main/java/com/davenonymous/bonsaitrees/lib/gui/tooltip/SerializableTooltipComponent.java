package com.davenonymous.bonsaitrees.lib.gui.tooltip;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public interface SerializableTooltipComponent<T extends SerializableTooltipComponent<T>> extends TooltipComponent, ClientTooltipComponent {
	StreamCodec<?, T> getCodec();
}