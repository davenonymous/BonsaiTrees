package com.davenonymous.bonsaitrees3.blocks;

import com.davenonymous.bonsaitrees3.BonsaiTrees3;
import com.davenonymous.bonsaitrees3.libnonymous.gui.framework.WidgetContainer;
import com.davenonymous.bonsaitrees3.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.items.CapabilityItemHandler;

public class BonsaiPotContainer extends WidgetContainer {
	public static int WIDTH = 176;
	public static int HEIGHT = 165;

	public static ResourceLocation SLOTGROUP_SOIL = new ResourceLocation(BonsaiTrees3.MODID, "input_soil");
	public static ResourceLocation SLOTGROUP_SAPLING = new ResourceLocation(BonsaiTrees3.MODID, "input_sapling");
	public static ResourceLocation SLOTGROUP_OUTPUT = new ResourceLocation(BonsaiTrees3.MODID, "output");
	public static ResourceLocation SLOTGROUP_UPGRADES = new ResourceLocation(BonsaiTrees3.MODID, "upgrades");

	private BonsaiPotBlockEntity pot;

	public BonsaiPotContainer(int windowId, BlockPos pos, Inventory playerInventory, Player player) {
		super(Registration.BONSAI_POT_CONTAINER.get(), windowId, playerInventory);

		this.layoutPlayerInventorySlots(8, HEIGHT - 84);

		int yOffset = 20;
		pot = (BonsaiPotBlockEntity) player.getLevel().getBlockEntity(pos);
		if(pot != null) {
			int x = WIDTH - 8 - (18 * 3) + 2;

			pot.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP).ifPresent(iItemHandler -> {
				this.addSlotRange(SLOTGROUP_SAPLING, iItemHandler, 0, 8, yOffset + 0, 1, 0);
			});

			pot.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.NORTH).ifPresent(iItemHandler -> {
				this.addSlotRange(SLOTGROUP_SOIL, iItemHandler, 0, 8, yOffset + 20, 1, 0);
			});

			pot.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.DOWN).ifPresent(iItemHandler -> {
				this.addSlotBox(SLOTGROUP_OUTPUT, iItemHandler, 0, x, yOffset + 0, 3, 18, 2, 20);
			});

			var upgradeItemStackHandler = pot.getUpgradeItemStacks();
			this.addSlotRange(SLOTGROUP_UPGRADES, upgradeItemStackHandler, 0, 35, yOffset + 20, 4, 18);
		}

		this.allowSlotGroupMovement(SLOTGROUP_PLAYER, SLOTGROUP_SOIL, true);
		this.allowSlotGroupMovement(SLOTGROUP_PLAYER, SLOTGROUP_SAPLING, true);
		this.allowSlotGroupMovement(SLOTGROUP_PLAYER, SLOTGROUP_UPGRADES, true);
		this.allowSlotGroupMovement(SLOTGROUP_OUTPUT, SLOTGROUP_PLAYER);
	}

	public BonsaiPotBlockEntity getPot() {
		return pot;
	}
}
