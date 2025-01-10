package com.davenonymous.bonsaitrees.lib.gui;


import com.davenonymous.bonsaitrees.lib.gui.event.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public abstract class WidgetScreen extends Screen {
	protected GUI gui;
	private int previousMouseX = Integer.MAX_VALUE;
	private int previousMouseY = Integer.MAX_VALUE;

	protected ResourceLocation id;

	protected WidgetScreen(Component title) {
		super(title);
	}

	protected abstract GUI createGUI();

	public GUI getOrCreateGui() {
		if(gui == null) {
			this.gui = createGUI();
			this.gui.setVisible(true);
		}
		return gui;
	}

	@Override
	public void tick() {
		super.tick();

		getOrCreateGui().fireEvent(new UpdateScreenEvent());
		this.resetMousePositions();
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		if(getOrCreateGui().fireEvent(new MouseClickEvent(mouseX, mouseY, mouseButton)) == WidgetEventResult.CONTINUE_PROCESSING) {
			return super.mouseClicked(mouseX, mouseY, mouseButton);
		}

		return false;
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
		if(getOrCreateGui().fireEvent(new MouseReleasedEvent(mouseX, mouseY, mouseButton)) == WidgetEventResult.CONTINUE_PROCESSING) {
			return super.mouseReleased(mouseX, mouseY, mouseButton);
		}

		return false;
	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		if(getOrCreateGui().fireEvent(new KeyReleasedEvent(keyCode, scanCode, modifiers)) == WidgetEventResult.CONTINUE_PROCESSING) {
			return super.keyReleased(keyCode, scanCode, modifiers);
		}
		return false;
	}

	@Override
	public boolean charTyped(char chr, int code) {
		if(getOrCreateGui().fireEvent(new CharTypedEvent(chr, code)) == WidgetEventResult.CONTINUE_PROCESSING) {
			return super.charTyped(chr, code);
		}
		return false;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if(getOrCreateGui().fireEvent(new KeyPressedEvent(keyCode, scanCode, modifiers)) == WidgetEventResult.CONTINUE_PROCESSING) {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
		return false;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		if(getOrCreateGui().fireEvent(new MouseScrollEvent(mouseX, mouseY, scrollY)) == WidgetEventResult.CONTINUE_PROCESSING) {
			return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
		}
		return false;
	}

	@Override
	public void render(GuiGraphics pGuiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(pGuiGraphics, mouseX, mouseY, partialTicks);

		if(mouseX != previousMouseX || mouseY != previousMouseY) {
			getOrCreateGui().fireEvent(new MouseMoveEvent(mouseX, mouseY));

			previousMouseX = mouseX;
			previousMouseY = mouseY;
		}

		getOrCreateGui().drawGUI(pGuiGraphics, this);
		getOrCreateGui().drawTooltips(pGuiGraphics, this, mouseX, mouseY);
		//renderHoveredToolTip(mouseX, mouseY);

		// RenderHelper.disableStandardItemLighting();
	}

	@Override
	public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
	}

	protected void resetMousePositions() {
		this.previousMouseX = Integer.MIN_VALUE;
		this.previousMouseY = Integer.MIN_VALUE;
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}