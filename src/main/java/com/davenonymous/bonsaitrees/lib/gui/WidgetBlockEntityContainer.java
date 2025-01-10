package com.davenonymous.bonsaitrees.lib.gui;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class WidgetBlockEntityContainer<T extends BlockEntity> extends WidgetContainer {
	protected T blockEntity;

	protected WidgetBlockEntityContainer(@Nullable MenuType<?> type, int id, BlockPos pos, Inventory inv, @NotNull Player player) {
		super(type, id, inv);

		var uncastEntity = player.getCommandSenderWorld().getBlockEntity(pos);
		if(uncastEntity != null) {
			this.blockEntity = (T) uncastEntity;
		}
	}

	@Override
	public boolean stillValid(Player playerEntity) {
		if(blockEntity != null) {
			Level level = blockEntity.getLevel();
			if(level != null) {
				ContainerLevelAccess callable = ContainerLevelAccess.create(level, blockEntity.getBlockPos());
				return stillValid(callable, playerEntity, blockEntity.getBlockState().getBlock());
			}
		}

		return false;
	}

	public T getBlockEntity() {
		return blockEntity;
	}
}