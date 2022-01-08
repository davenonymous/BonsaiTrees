package com.davenonymous.bonsaitrees3.libnonymous.gui.framework;


import com.davenonymous.bonsaitrees3.libnonymous.gui.framework.event.VisibilityChangedEvent;
import com.davenonymous.bonsaitrees3.libnonymous.gui.framework.event.WidgetEventResult;
import com.davenonymous.bonsaitrees3.libnonymous.gui.framework.widgets.Widget;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class WidgetSlot extends SlotItemHandler {
	private boolean enabled;
	private ResourceLocation id;
	private boolean locked = false;

	public WidgetSlot(ResourceLocation slotId, IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);

		this.id = slotId;
		this.enabled = true;
	}

	public void bindToWidget(Widget widget) {
		widget.addListener(VisibilityChangedEvent.class, (event, widget1) -> {
			this.setEnabled(event.newValue && widget.areAllParentsVisible());
			return WidgetEventResult.CONTINUE_PROCESSING;
		});
	}

	public ResourceLocation getGroupId() {
		return this.id;
	}

	public boolean matches(ResourceLocation slotId) {
		return this.id.equals(slotId);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public WidgetSlot setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	public boolean isLocked() {
		return locked;
	}

	public WidgetSlot setLocked(boolean locked) {
		this.locked = locked;
		return this;
	}

	@Override
	public void onTake(Player thePlayer, ItemStack stack) {
		if(locked) {
			return;
		}

		if(stack.getCount() <= stack.getMaxStackSize()) {
			super.onTake(thePlayer, stack);
			return;
		}

		int total = stack.getCount() + getItem().getCount();
		ItemStack before = stack.copy();
		ItemStack after = before.copy();
		after.setCount(total - before.getMaxStackSize());

		stack.setCount(before.getMaxStackSize());
		this.set(after);
		this.setChanged();
	}

	@Override
	public boolean allowModification(Player player) {
		if(locked) {
			return false;
		}

		if(player != null) {
			ItemStack mouseStack = player.getInventory().getSelected();
			if(mouseStack.isEmpty()) {
				return true;
			}

			if(getItem().getCount() > getItem().getMaxStackSize()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public int getMaxStackSize(@Nonnull ItemStack stack) {
		return 64;
	}
}
