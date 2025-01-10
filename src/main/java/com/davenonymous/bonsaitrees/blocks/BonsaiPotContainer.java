package com.davenonymous.bonsaitrees.blocks;

import com.davenonymous.bonsaitrees.BonsaiTrees;
import com.davenonymous.bonsaitrees.lib.gui.WidgetBlockEntityContainer;
import com.davenonymous.bonsaitrees.setup.ModContainers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class BonsaiPotContainer extends WidgetBlockEntityContainer<BonsaiPotBlockEntity> {
	public static int WIDTH = 176;
	public static int HEIGHT = 185;

	public static ResourceLocation SLOTGROUP_SOIL = BonsaiTrees.resource("input_soil");
	public static ResourceLocation SLOTGROUP_SAPLING = BonsaiTrees.resource("input_sapling");
	public static ResourceLocation SLOTGROUP_CAMOUFLAGE = BonsaiTrees.resource("camouflage");
	public static ResourceLocation SLOTGROUP_TOOL = BonsaiTrees.resource("output");
	public static ResourceLocation SLOTGROUP_OUTPUT = BonsaiTrees.resource("output");

	private final BonsaiPotBlockEntity pot;

	public BonsaiPotContainer(int id, BlockPos pos, Inventory inv, @NotNull Player player) {
		super(ModContainers.BONSAI_POT.get(), id, pos, inv, player);

		this.layoutPlayerInventorySlots(8, HEIGHT - 84);

		int yOffset = 20;
		pot = (BonsaiPotBlockEntity) player.getCommandSenderWorld().getBlockEntity(pos);
		if(pot != null) {
			int x = WIDTH - 8 - (18 * 3) + 2;

			this.addSlotRange(SLOTGROUP_SAPLING, pot.inventories.saplingInventory, 0, 8, yOffset + 0, 1, 0);
			this.addSlotRange(SLOTGROUP_SOIL, pot.inventories.soilInventory, 0, 8, yOffset + 20, 1, 0);
			this.addSlotRange(SLOTGROUP_CAMOUFLAGE, pot.inventories.camouflageInventory, 0, 8, yOffset + 40, 1, 0);
			this.addSlotRange(SLOTGROUP_TOOL, pot.inventories.toolInventory, 0, 62, yOffset + 20, 1, 0);

			this.addSlotBox(SLOTGROUP_OUTPUT, pot.inventories.outputInventory, 0, x, yOffset + 11, 3, 18, 2, 18);
		}

		this.allowSlotGroupMovement(SLOTGROUP_PLAYER, SLOTGROUP_SOIL, true);
		this.allowSlotGroupMovement(SLOTGROUP_PLAYER, SLOTGROUP_SAPLING, true);
		this.allowSlotGroupMovement(SLOTGROUP_PLAYER, SLOTGROUP_CAMOUFLAGE, true);
		this.allowSlotGroupMovement(SLOTGROUP_PLAYER, SLOTGROUP_TOOL, true);
		this.allowSlotGroupMovement(SLOTGROUP_OUTPUT, SLOTGROUP_PLAYER, false);

	}
}
