package com.davenonymous.bonsaitrees3.client;

import com.davenonymous.bonsaitrees3.BonsaiTrees3;
import com.davenonymous.bonsaitrees3.blocks.BonsaiPotContainer;
import com.davenonymous.libnonymous.gui.framework.GUI;
import com.davenonymous.libnonymous.gui.framework.WidgetContainerScreen;
import com.davenonymous.libnonymous.gui.framework.WidgetSlot;
import com.davenonymous.libnonymous.gui.framework.event.MouseClickEvent;
import com.davenonymous.libnonymous.gui.framework.event.UpdateScreenEvent;
import com.davenonymous.libnonymous.gui.framework.event.ValueChangedEvent;
import com.davenonymous.libnonymous.gui.framework.event.WidgetEventResult;
import com.davenonymous.libnonymous.gui.framework.widgets.WidgetItemStack;
import com.davenonymous.libnonymous.gui.framework.widgets.WidgetProgressBar;
import com.davenonymous.libnonymous.gui.framework.widgets.WidgetRedstoneMode;
import com.davenonymous.libnonymous.helper.Translatable;
import com.davenonymous.bonsaitrees3.network.Networking;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class BonsaiPotScreen extends WidgetContainerScreen<BonsaiPotContainer> {
	public static final Translatable CUT_BUTTON_TOOLTIP_OK = new Translatable(BonsaiTrees3.MODID, "button.cut_tree.tooltip.ok");
	public static final Translatable CUT_BUTTON_TOOLTIP_WAIT = new Translatable(BonsaiTrees3.MODID, "button.cut_tree.tooltip.wait");

	public BonsaiPotScreen(BonsaiPotContainer container, Inventory inv, Component name) {
		super(container, inv, name);

	}

	@Override
	protected GUI createGUI() {
		GUI gui = new GUI(0, 0, BonsaiPotContainer.WIDTH, BonsaiPotContainer.HEIGHT);
		gui.setContainer(this.menu);

		WidgetItemStack cutButton = new WidgetItemStack(new ItemStack(Items.DIAMOND_AXE), false);
		cutButton.setPosition(BonsaiPotContainer.WIDTH - (34 + 12 + 2 * 18), 20);
		cutButton.setEnabled(false);
		cutButton.addListener(MouseClickEvent.class, (event, widget) -> {
			Networking.sendCutTreeToServer(this.menu.getPot().getBlockPos());
			return WidgetEventResult.HANDLED;
		});
		gui.add(cutButton);

		WidgetProgressBar progressBar = new WidgetProgressBar();
		progressBar.setDimensions(29, 19, BonsaiPotContainer.WIDTH - (20 + 34 + 8 + 3 * 18), 18);
		progressBar.setValue(this.menu.getPot().getProgress() * 100);
		gui.add(progressBar);

		WidgetRedstoneMode redstoneToggle = new WidgetRedstoneMode();
		redstoneToggle.setDimensions(BonsaiPotContainer.WIDTH - 16, 5, 10, 10);
		redstoneToggle.addListener(ValueChangedEvent.class, (event, widget) -> {
			Networking.sendRedstoneModeToServer(this.menu.getPot().getBlockPos(), redstoneToggle.getValue());
			return WidgetEventResult.HANDLED;
		});
		gui.add(redstoneToggle);

		gui.addListener(UpdateScreenEvent.class, (event, widget) -> {
			var progress = this.menu.getPot().getProgress();
			progressBar.setValue(progress * 100);
			cutButton.setEnabled(progress >= 1.0f);
			cutButton.setTooltipLines(progress >= 1.0f ? CUT_BUTTON_TOOLTIP_OK : CUT_BUTTON_TOOLTIP_WAIT);
			redstoneToggle.setValue(this.menu.getPot().redstoneMode, false);
			redstoneToggle.updateToolTips();

			return WidgetEventResult.CONTINUE_PROCESSING;
		});

		for(Slot slot : this.menu.slots) {
			if(slot instanceof WidgetSlot ws) {
				ws.bindToWidget(gui);
			}
		}

		return gui;
	}
}