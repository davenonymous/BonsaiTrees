package com.davenonymous.bonsaitrees3.libnonymous.gui.framework.widgets;


import com.davenonymous.bonsaitrees3.libnonymous.gui.framework.GUI;
import com.davenonymous.bonsaitrees3.libnonymous.gui.framework.event.MouseClickEvent;
import com.davenonymous.bonsaitrees3.libnonymous.gui.framework.event.MouseEnterEvent;
import com.davenonymous.bonsaitrees3.libnonymous.gui.framework.event.MouseExitEvent;
import com.davenonymous.bonsaitrees3.libnonymous.gui.framework.event.WidgetEventResult;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;


public class WidgetSelectButton<T> extends WidgetWithChoiceValue<T> {
	public boolean hovered = false;

	public ResourceLocation backgroundTexture;
	public TextureAtlasSprite atlasSprite;

	public SoundEvent clickSound;

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

	public WidgetSelectButton<T> setClickSound(SoundEvent clickSound) {
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
	public void draw(PoseStack pPoseStack, Screen screen) {
		//Logz.info("Width: %d, height: %d", width, height);
        /*

        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.translatef(0.0f, 0.0f, 2.0f);

        // Draw the background
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);


        if(atlasSprite != null) {
            screen.getMinecraft().getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);

            //screen.drawTexturedModalRect(0, 0, atlasSprite, 16, 16);
            WidgetButton.fillAreaWithIcon(atlasSprite, 0, 0, width, height);
            //Gui.drawModalRectWithCustomSizedTexture(0, 0, atlasSprite.getMinU(), atlasSprite.getMinV(), width, height, atlasSprite.getMaxU()-atlasSprite.getMinU(), atlasSprite.getMaxV()-atlasSprite.getMinU());
        } else {
            screen.getMinecraft().getTextureManager().bindTexture(backgroundTexture);
            GUIHelper.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, width, height, 16.0f, 16.0f);
        }

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, hovered ? 1.0F : 1.0F);
        screen.getMinecraft().getTextureManager().bindTexture(GUI.tabIcons);

        // Top Left corner
        int texOffsetX = 64;
        int texOffsetY = 84;
        int overlayWidth = 20;

        GuiUtils.drawTexturedModalRect(0, 0, texOffsetX, texOffsetY, 4, 4, 0.0f);


        // Top right corner
        GuiUtils.drawTexturedModalRect(0+width - 4, 0, texOffsetX + overlayWidth - 4, texOffsetY, 4, 4, 0.0f);

        // Bottom Left corner
        GuiUtils.drawTexturedModalRect(0, this.height - 4, texOffsetX, texOffsetY + overlayWidth - 4, 4, 4, 0.0f);

        // Bottom Right corner
        GuiUtils.drawTexturedModalRect(0+width - 4, this.height - 4, texOffsetX + overlayWidth - 4, texOffsetY + overlayWidth - 4, 4, 4, 0.0f);


        // Top edge
        GUIHelper.drawStretchedTexture(0+4, 0, width - 8, 4, texOffsetX + 4, texOffsetY, 12, 4);

        // Bottom edge
        GUIHelper.drawStretchedTexture(0+4, this.height - 4, width - 8, 4, texOffsetX + 4, texOffsetY + overlayWidth - 4, 12, 4);

        // Left edge
        GUIHelper.drawStretchedTexture(0, 4, 4, this.height - 8, texOffsetX, texOffsetY+4, 4, 12);

        // Right edge
        GUIHelper.drawStretchedTexture(0+width - 4, 4, 4, this.height - 8, texOffsetX + overlayWidth - 4, texOffsetY + 3, 4, 12);

        FontRenderer fontrenderer = screen.getMinecraft().fontRenderer;
        RenderSystem.translatef(0.0f, 0.0f, 10.0f);
        drawButtonContent(pPoseStack, screen, fontrenderer);
        RenderSystem.translatef(0.0f, 0.0f, -10.0f);

        if(!enabled) {
            GUIHelper.drawColoredRectangle(1, 1, width-2, height-2, 0x80000000);
        } else if(hovered) {
            GUIHelper.drawColoredRectangle(1, 1, width-2, height-2, 0x808090FF);
        }

        RenderSystem.popMatrix();
         */
	}

	protected void drawButtonContent(PoseStack pPoseStack, Screen screen, Font fontrenderer) {
		int color = 0xEEEEEE;
		//screen.drawCenteredString(fontrenderer, getValue().toString(), width / 2, (height - 8) / 2, color);
	}
}
