package com.davenonymous.bonsaitrees3.libnonymous.gui.framework.widgets;

import com.davenonymous.bonsaitrees3.libnonymous.gui.framework.GUI;
import com.davenonymous.bonsaitrees3.libnonymous.gui.framework.GUIHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.client.gui.GuiUtils;

import java.util.Collections;

public class WidgetItemStack extends WidgetWithValue<ItemStack> {
	boolean drawSlot = false;

	public WidgetItemStack(ItemStack stack) {
		this.setSize(16, 16);
		this.setValue(stack);
	}

	public WidgetItemStack(ItemStack stack, boolean drawSlot) {
		this(stack);
		this.drawSlot = drawSlot;
	}

	public void setValue(ItemStack stack) {
		if(!stack.isEmpty()) {

			var tooltipFlag = Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL;
			this.setTooltipLines(stack.getTooltipLines(Minecraft.getInstance().player, tooltipFlag));
		} else {
			this.setTooltipLines(Collections.emptyList());
		}

		super.setValue(stack);
	}

	@Override
	public void draw(PoseStack pPoseStack, Screen screen) {
		super.draw(pPoseStack, screen);

		if(drawSlot) {
			this.drawSlot(pPoseStack, screen);
		}

		if(this.value == null || this.value.isEmpty()) {
			return;
		}

		GUIHelper.renderGuiItem(this.value, getActualX(), getActualY(), !this.enabled);
	}

	private void drawSlot(PoseStack pPoseStack, Screen screen) {
		RenderSystem.setShaderTexture(0, GUI.tabIcons);

		int texOffsetY = 84;
		int texOffsetX = 84;

		GuiUtils.drawTexturedModalRect(pPoseStack, -1, -1, texOffsetX, texOffsetY, 18, 18, 0.0f);
	}
}
