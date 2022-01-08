package com.davenonymous.bonsaitrees3.libnonymous.helper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public class SpawnHelper {
	public static void spawnItemStack(ItemStack stack, Level level, BlockPos pos) {
		if(level.isClientSide()) {
			return;
		}

		var entity = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), stack);
		entity.setDeltaMovement(0d, 0.1d, 0.0d);
		entity.setPickUpDelay(10);
		entity.setExtendedLifetime();
		level.addFreshEntity(entity);
	}

	public static void dropItemHandlerContents(IItemHandlerModifiable handler, Level level, BlockPos pos, boolean removeFromHandler) {
		if(level.isClientSide()) {
			return;
		}

		for(int slot = 0; slot < handler.getSlots(); slot++) {
			var stack = handler.getStackInSlot(slot);
			if(stack.isEmpty()) {
				continue;
			}

			spawnItemStack(stack, level, pos);
			if(removeFromHandler) {
				handler.setStackInSlot(slot, ItemStack.EMPTY);
			}
		}
	}

	public static void dropItemHandlerContents(IItemHandler handler, Level level, BlockPos pos) {
		if(level.isClientSide()) {
			return;
		}

		for(int slot = 0; slot < handler.getSlots(); slot++) {
			var stack = handler.getStackInSlot(slot);
			if(stack.isEmpty()) {
				continue;
			}

			spawnItemStack(stack, level, pos);
		}
	}

}
