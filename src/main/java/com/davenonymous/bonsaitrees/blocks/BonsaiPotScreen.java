package com.davenonymous.bonsaitrees.blocks;

import com.davenonymous.bonsaitrees.lib.gui.GUI;
import com.davenonymous.bonsaitrees.lib.gui.WidgetContainerScreen;
import com.davenonymous.bonsaitrees.lib.gui.event.GuiDataUpdatedEvent;
import com.davenonymous.bonsaitrees.lib.gui.event.UpdateScreenEvent;
import com.davenonymous.bonsaitrees.lib.gui.event.ValueChangedEvent;
import com.davenonymous.bonsaitrees.lib.gui.event.WidgetEventResult;
import com.davenonymous.bonsaitrees.lib.gui.tooltip.IngredientBoxTooltipComponent;
import com.davenonymous.bonsaitrees.lib.gui.tooltip.TranslatableTooltipComponent;
import com.davenonymous.bonsaitrees.lib.gui.widgets.WidgetItemStack;
import com.davenonymous.bonsaitrees.lib.gui.widgets.WidgetProgressArrow;
import com.davenonymous.bonsaitrees.lib.gui.widgets.WidgetRedstoneMode;
import com.davenonymous.bonsaitrees.networking.SetRedstoneMode;
import com.davenonymous.bonsaitrees.setup.cache.BonsaiCache;
import com.davenonymous.bonsaitrees.setup.cache.SoilCache;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Collections;

public class BonsaiPotScreen extends WidgetContainerScreen<BonsaiPotContainer> {
	public BonsaiPotScreen(BonsaiPotContainer container, Inventory inv, Component name) {
		super(container, inv, name);
	}

	@Override
	protected GUI createGUI() {
		GUI gui = new GUI(0, 0, BonsaiPotContainer.WIDTH, BonsaiPotContainer.HEIGHT);
		gui.setContainer(this.menu);

		BonsaiPotBlockEntity pot = this.menu.getBlockEntity();

		if(pot != null) {
			BlockState potState = pot.getBlockState();
			Block potBlock = potState.getBlock();
			ItemEnchantments potEnchantments = pot.inventories.enchantments;
			StringBuilder titleKey = new StringBuilder(potBlock.getDescriptionId());
			if(!potEnchantments.isEmpty()) {
				titleKey.append(".enchanted");
			}

			this.setCustomTitle(Component.translatable(titleKey.toString()));
		}

		var progressArrow = new WidgetProgressArrow();
		progressArrow.setValue(0D);
		progressArrow.setPosition(32, 40);
		gui.add(progressArrow);

		var canProduceArrow = new WidgetProgressArrow();
		canProduceArrow.setValue(0D);
		canProduceArrow.setPosition(86, 40);
		gui.add(canProduceArrow);

		var fakeSapling = new WidgetItemStack(new ItemStack(Items.OAK_SAPLING), true);
		fakeSapling.setPosition(8, 20);
		fakeSapling.setVisible(false);
		fakeSapling.setGrayOut(true);
		fakeSapling.setDrawTooltip(false);
		fakeSapling.setTooltipElements(
			new TranslatableTooltipComponent("bonsaitrees4.tooltip.sapling_required"),
			new IngredientBoxTooltipComponent(BonsaiCache.BONSAI_BY_ITEM.keySet())
		);
		gui.add(fakeSapling);

		var fakeSoil = new WidgetItemStack(new ItemStack(Items.GRASS_BLOCK), true);
		fakeSoil.setPosition(8, 40);
		fakeSoil.setVisible(false);
		fakeSoil.setGrayOut(true);
		fakeSoil.setDrawTooltip(false);
		fakeSoil.setTooltipElements(
			new TranslatableTooltipComponent("bonsaitrees4.tooltip.soil_required"),
			new IngredientBoxTooltipComponent(SoilCache.SOILS.keySet().stream().map(ItemStack::getItem).toList())
		);
		gui.add(fakeSoil);


		var fakeTool = new WidgetItemStack(new ItemStack(Items.IRON_AXE), true);
		fakeTool.setPosition(62, 40);
		fakeTool.setVisible(false);
		fakeTool.setGrayOut(true);
		fakeTool.setDrawTooltip(false);
		fakeTool.setTooltipElements(
			new TranslatableTooltipComponent("bonsaitrees4.tooltip.tool_required")
		);
		gui.add(fakeTool);

		var fakeCamo = new WidgetItemStack(new ItemStack(Blocks.EMERALD_BLOCK), true);
		fakeCamo.setPosition(8, 60);
		fakeCamo.setVisible(false);
		fakeCamo.setGrayOut(true);
		fakeCamo.setDrawTooltip(false);
		fakeCamo.setTooltipElements(
			new TranslatableTooltipComponent("bonsaitrees4.tooltip.camouflage_optional")
		);
		gui.add(fakeCamo);

		var enchantmentsDisplay = new WidgetItemStack(new ItemStack(Items.ENCHANTED_BOOK), false);
		enchantmentsDisplay.setPosition(62, 60);
		enchantmentsDisplay.setVisible(false);
		enchantmentsDisplay.setDrawTooltip(false);
		gui.add(enchantmentsDisplay);

		var redstoneModeToggle = new WidgetRedstoneMode(pot.getRedstoneMode());
		redstoneModeToggle.setPosition(BonsaiPotContainer.WIDTH - 20, 2);
		redstoneModeToggle.addListener(ValueChangedEvent.class, (event, widget) -> {
			PacketDistributor.sendToServer(new SetRedstoneMode(pot.getBlockPos(), redstoneModeToggle.getValue()));
			return WidgetEventResult.HANDLED;
		});
		gui.add(redstoneModeToggle);


		gui.addListener(GuiDataUpdatedEvent.class, (event, widget) -> {
			boolean isActive = pot.getRedstoneMode().resolve(pot.getLevel(), pot.getBlockPos());
			redstoneModeToggle.updateToolTips();
			redstoneModeToggle.addTooltipLine(
				isActive
				? Component.translatable("bonsaitrees4.tooltip.redstone_active").withStyle(ChatFormatting.DARK_GREEN)
				: Component.translatable("bonsaitrees4.tooltip.redstone_inactive").withStyle(ChatFormatting.RED)
			);

			if(pot.inventories.enchantments.isEmpty()) {
				enchantmentsDisplay.setVisible(false);
			} else {
				enchantmentsDisplay.setVisible(true);
				enchantmentsDisplay.setTooltipLines(Component.translatable("bonsaitrees4.tooltip.enchantments"));
				for(var enchantmentHolder : pot.inventories.enchantments.keySet()) {
					int level = pot.inventories.enchantments.getLevel(enchantmentHolder);
					MutableComponent description = enchantmentHolder.value().description().copy()
						.append(" ")
						.append(Component.translatable("enchantment.level." + level));

					enchantmentsDisplay.addTooltipLine(description.withStyle(ChatFormatting.GRAY));
				}
			}

			if(pot.inventories.getSaplingStack().isEmpty()) {
				fakeSapling.setVisible(true);
			} else {
				fakeSapling.setVisible(false);
			}

			if(pot.inventories.getSoilStack().isEmpty()) {
				fakeSoil.setVisible(true);
			} else {
				fakeSoil.setVisible(false);
			}

			if(pot.inventories.getCamouflageStack().isEmpty()) {
				fakeCamo.setVisible(true);
			} else {
				fakeCamo.setVisible(false);
			}

			if(pot.inventories.getToolStack().isEmpty()) {
				canProduceArrow.setValue(0D);
				fakeTool.setVisible(true);
			} else {
				canProduceArrow.setValue(100D);
				fakeTool.setVisible(false);
			}

			return WidgetEventResult.CONTINUE_PROCESSING;
		});
		this.fireDataUpdateEvent();

		gui.addListener(UpdateScreenEvent.class, (event, widget) -> {
			progressArrow.setValue((double) pot.getTreeGrowthProgress(0.0f) * 100);
			if(progressArrow.getValue() > 0) {
				progressArrow.setTooltipLines(Component.literal(String.format("%.0f%%", progressArrow.getValue())));
			} else {
				progressArrow.setTooltipLines(Collections.emptyList());
			}

			return WidgetEventResult.CONTINUE_PROCESSING;
		});

		return gui;
	}
}
