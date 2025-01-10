package com.davenonymous.bonsaitrees.lib.gui.event;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

public class MouseClickEvent implements IEvent {
	public int button;
	public double x;
	public double y;
	public ItemStack carriedStack = ItemStack.EMPTY;

	public MouseClickEvent(double mouseX, double mouseY, int button) {
		this.x = mouseX;
		this.y = mouseY;
		this.button = button;
		if(Minecraft.getInstance().player != null && Minecraft.getInstance().player.getInventory() != null) {
			// TODO: Fix this: How do we know what item the player has picked up with the mouse in a GUI
			//this.carriedStack = Minecraft.getInstance().player.getInventory().getSelected().copy();
		}
	}

	public boolean isLeftClick() {
		return button == 0;
	}

	@Override
	public String toString() {
		return String.format("MouseClick[x=%.2f,y=%.2f,button=%d,stack=%s]", this.x, this.y, this.button, this.carriedStack);
	}
}