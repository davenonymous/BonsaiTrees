package com.davenonymous.bonsaitrees.lib.gui.widgets;


import com.davenonymous.bonsaitrees.lib.gui.ISelectable;
import com.davenonymous.bonsaitrees.lib.gui.event.ListEntrySelectionEvent;
import net.minecraft.resources.ResourceLocation;

public abstract class WidgetSelectablePanelWithValue<T> extends WidgetPanel implements IValueProvider<T>, ISelectable {
	private ResourceLocation id;
	boolean isSelected = false;

	@Override
	public ResourceLocation getId() {
		return this.id;
	}

	@Override
	public void setId(ResourceLocation location) {
		this.id = location;
	}

	@Override
	public boolean isSelected() {
		return this.isSelected;
	}

	@Override
	public void setSelected(boolean state) {
		this.isSelected = state;
		this.fireEvent(new ListEntrySelectionEvent(state));
	}
}
