package com.davenonymous.bonsaitrees3.libnonymous.gui.framework;


import com.davenonymous.bonsaitrees3.libnonymous.gui.framework.event.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public abstract class WidgetContainerScreen<T extends WidgetContainer> extends AbstractContainerScreen<T> {
	protected GUI gui;

	private int previousMouseX = Integer.MAX_VALUE;
	private int previousMouseY = Integer.MAX_VALUE;
	public boolean dataUpdated = false;

	public WidgetContainerScreen(T container, Inventory inv, Component name) {
		super(container, inv, name);

		this.gui = createGUI();
		this.gui.setVisible(true);
	}

	protected abstract GUI createGUI();

	@Override
	protected void containerTick() {
		super.containerTick();
		gui.fireEvent(new UpdateScreenEvent());
		this.resetMousePositions();
	}


	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		if(gui.fireEvent(new MouseClickEvent(mouseX, mouseY, mouseButton)) == WidgetEventResult.CONTINUE_PROCESSING) {
			return super.mouseClicked(mouseX, mouseY, mouseButton);
		}

		return false;
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
		Slot slot = this.getSlotUnderMouse();
		//if(slot instanceof WidgetSlot) {
		if(gui.fireEvent(new MouseReleasedEvent(mouseX, mouseY, mouseButton)) == WidgetEventResult.CONTINUE_PROCESSING) {
			return super.mouseReleased(mouseX, mouseY, mouseButton);
		}
		//}

		return false;
	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		if(gui.fireEvent(new KeyReleasedEvent(keyCode, scanCode, modifiers)) == WidgetEventResult.CONTINUE_PROCESSING) {
			return super.keyReleased(keyCode, scanCode, modifiers);
		}
		return false;
	}

	@Override
	public boolean charTyped(char chr, int code) {
		if(gui.fireEvent(new CharTypedEvent(chr, code)) == WidgetEventResult.CONTINUE_PROCESSING) {
			return super.charTyped(chr, code);
		}
		return false;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if(gui.fireEvent(new KeyPressedEvent(keyCode, scanCode, modifiers)) == WidgetEventResult.CONTINUE_PROCESSING) {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
		return false;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
		if(gui.fireEvent(new MouseScrollEvent(mouseX, mouseY, scrollAmount)) == WidgetEventResult.CONTINUE_PROCESSING) {
			return super.mouseScrolled(mouseX, mouseY, scrollAmount);
		}
		return false;
	}

    /*
    // TODO: Mouse drag and scroll events
    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if(gui.fireEvent(new MouseClickMoveEvent(mouseX, mouseY, clickedMouseButton, timeSinceLastClick)) == WidgetEventResult.CONTINUE_PROCESSING) {
            super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        }
    }
    */

	@Override
	protected void renderLabels(PoseStack pPoseStack, int pMouseX, int pMouseY) {
		int minY = height;
		for(Slot slot : this.menu.slots) {
			if(slot instanceof WidgetSlot ws) {
				if(ws.getGroupId() == WidgetContainer.SLOTGROUP_PLAYER && ws.y < minY) {
					minY = ws.y;
				}
			}
		}
		this.inventoryLabelY = minY - (font.lineHeight + 2);
		this.font.draw(pPoseStack, this.playerInventoryTitle, (float) this.inventoryLabelX, (float) this.inventoryLabelY, 0x404040);
		this.font.draw(pPoseStack, this.title, (float) this.titleLabelX, 7.0f, 0x404040);

		pPoseStack.pushPose();
		pPoseStack.translate(-getGuiLeft(), -getGuiTop() + 18, 0.0f);
		gui.drawTooltips(pPoseStack, this, pMouseX, pMouseY);
		this.renderTooltip(pPoseStack, pMouseX, pMouseY);
		pPoseStack.popPose();
	}

	@Override
	public void render(PoseStack pPoseStack, int mouseX, int mouseY, float partialTicks) {
		if(dataUpdated) {
			dataUpdated = false;
			gui.fireEvent(new GuiDataUpdatedEvent());
		}

		if(mouseX != previousMouseX || mouseY != previousMouseY) {
			gui.fireEvent(new MouseMoveEvent(mouseX, mouseY));

			previousMouseX = mouseX;
			previousMouseY = mouseY;
		}

		super.render(pPoseStack, mouseX, mouseY, partialTicks);
	}

	@Override
	protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
		this.renderBackground(pPoseStack);
		gui.drawGUI(pPoseStack, this);

		if(this.menu != null && this.menu.slots != null) {
			for(Slot slot : this.menu.slots) {
				if(slot instanceof WidgetSlot widgetSlot) {
					if(!widgetSlot.isEnabled()) {
						continue;
					}

					gui.drawSlot(pPoseStack, this, slot, this.leftPos, this.topPos);
				}
			}
		}
	}


	protected void resetMousePositions() {
		this.previousMouseX = Integer.MIN_VALUE;
		this.previousMouseY = Integer.MIN_VALUE;
	}

	public void fireDataUpdateEvent() {
		dataUpdated = true;
	}
}