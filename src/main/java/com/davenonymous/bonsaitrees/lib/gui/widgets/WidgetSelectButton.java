package com.davenonymous.bonsaitrees.lib.gui.widgets;


import com.davenonymous.bonsaitrees.lib.gui.GUI;
import com.davenonymous.bonsaitrees.lib.gui.GUIHelper;
import com.davenonymous.bonsaitrees.lib.gui.event.MouseClickEvent;
import com.davenonymous.bonsaitrees.lib.gui.event.MouseEnterEvent;
import com.davenonymous.bonsaitrees.lib.gui.event.MouseExitEvent;
import com.davenonymous.bonsaitrees.lib.gui.event.WidgetEventResult;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;


public class WidgetSelectButton<T> extends WidgetWithChoiceValue<T> {
	public boolean hovered = false;

	public ResourceLocation backgroundTexture;
	public TextureAtlasSprite atlasSprite;

	public Holder.Reference<SoundEvent> clickSound;

	public WidgetSelectButton() {
		this.setHeight(20);
		this.setWidth(100);

		this.clickSound = SoundEvents.UI_BUTTON_CLICK;
		this.backgroundTexture = GUI.defaultButtonTexture;
		this.addListener(MouseEnterEvent.class, (event, widget) -> {
			((WidgetSelectButton) widget).hovered = true;
			return WidgetEventResult.CONTINUE_PROCESSING;
		});
		this.addListener(MouseExitEvent.class, (event, widget) -> {
			((WidgetSelectButton) widget).hovered = false;
			return WidgetEventResult.CONTINUE_PROCESSING;
		});
		this.addListener(MouseClickEvent.class, ((event, widget) -> {
			Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(this.clickSound, 1.0F));
			return WidgetEventResult.CONTINUE_PROCESSING;
		}));

		// TODO: Add mouse scroll wheel functionality

		this.addClickListener();
	}

	public WidgetSelectButton<T> setClickSound(Holder.Reference<SoundEvent> clickSound) {
		this.clickSound = clickSound;
		return this;
	}

	public WidgetSelectButton<T> setBackgroundTexture(ResourceLocation backgroundTexture) {
		this.backgroundTexture = backgroundTexture;
		return this;
	}

	public WidgetSelectButton<T> setAtlasSprite(TextureAtlasSprite atlasSprite) {
		this.atlasSprite = atlasSprite;
		return this;
	}

	@Override
	public void draw(GuiGraphics pGuiGraphics, Screen screen) {
		//Logz.info("Width: %d, height: %d", width, height);

		pGuiGraphics.pose().pushPose();
		RenderSystem.enableBlend();
		pGuiGraphics.pose().translate(0f, 0f, 2f);

		// Draw the background
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

		RenderSystem.setShaderTexture(0, backgroundTexture);
		GUIHelper.drawModalRectWithCustomSizedTexture(pGuiGraphics, 0, 0, 0, 0, width, height, 16.0f, 16.0f);

		RenderSystem.setShaderTexture(0, GUI.tabIcons);

		// Top Left corner
		int texOffsetX = 64;
		int texOffsetY = 84;
		int overlayWidth = 20;

		pGuiGraphics.blit(GUI.tabIcons, 0, 0, texOffsetX, texOffsetY, 4, 4);


		// Top right corner
		pGuiGraphics.blit(GUI.tabIcons, 0 + width - 4, 0, texOffsetX + overlayWidth - 4, texOffsetY, 4, 4);

		// Bottom Left corner
		pGuiGraphics.blit(GUI.tabIcons, 0, this.height - 4, texOffsetX, texOffsetY + overlayWidth - 4, 4, 4);

		// Bottom Right corner
		pGuiGraphics.blit(GUI.tabIcons, 0 + width - 4, this.height - 4, texOffsetX + overlayWidth - 4, texOffsetY + overlayWidth - 4, 4, 4);


		// Top edge
		GUIHelper.drawStretchedTexture(pGuiGraphics, 0 + 4, 0, width - 8, 4, texOffsetX + 4, texOffsetY, 12, 4);

		// Bottom edge
		GUIHelper.drawStretchedTexture(pGuiGraphics, 0 + 4, this.height - 4, width - 8, 4, texOffsetX + 4, texOffsetY + overlayWidth - 4, 12, 4);

		// Left edge
		GUIHelper.drawStretchedTexture(pGuiGraphics, 0, 4, 4, this.height - 8, texOffsetX, texOffsetY + 4, 4, 12);

		// Right edge
		GUIHelper.drawStretchedTexture(pGuiGraphics, 0 + width - 4, 4, 4, this.height - 8, texOffsetX + overlayWidth - 4, texOffsetY + 3, 4, 12);

		pGuiGraphics.pose().translate(0f, 0f, 10f);
		drawButtonContent(pGuiGraphics, screen);
		pGuiGraphics.pose().translate(0f, 0f, -10f);

		if(!enabled) {
			GUIHelper.drawColoredRectangle(pGuiGraphics, 1, 1, width - 2, height - 2, 0x80000000);
		} else if(hovered) {
			GUIHelper.drawColoredRectangle(pGuiGraphics, 1, 1, width - 2, height - 2, 0x808090FF);
		}

		pGuiGraphics.pose().popPose();
	}

	protected void drawButtonContent(GuiGraphics pGuiGraphics, Screen screen) {
		drawString(pGuiGraphics, screen, I18n.get(getValue().toString()));
	}

	protected void drawString(GuiGraphics pGuiGraphics, Screen screen, String label) {
		int color = 0xFFFFFF;
		pGuiGraphics.drawCenteredString(screen.getMinecraft().font, label, (int) (width / 2.0f), (int) ((float) (height - 8) / 2.0f), color);
	}
}