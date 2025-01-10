package com.davenonymous.bonsaitrees.command.place;

import com.davenonymous.bonsaitrees.blocks.BonsaiPotBlockEntity;
import com.davenonymous.bonsaitrees.lib.util.Sorting;
import com.davenonymous.bonsaitrees.setup.ModBlocks;
import com.davenonymous.bonsaitrees.setup.cache.SoilCache;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class PlaceSoilTest implements Command<CommandSourceStack> {
	public static final PlaceSoilTest INSTANCE = new PlaceSoilTest();

	private PlaceSoilTest() {
	}

	public static ArgumentBuilder<CommandSourceStack, ?> register() {
		return Commands.argument("pos", BlockPosArgument.blockPos()).executes(INSTANCE);
	}

	@Override
	public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "pos");
		int total = SoilCache.SOILS.keySet().size();
		BlockState potState = ModBlocks.BONSAI_POT.get().defaultBlockState();

		PlacementHelper<BonsaiPotBlockEntity> helper = new PlacementHelper<>(pos, context.getSource().getLevel(), total);
		helper.placeArena();

		for(var entry : Sorting.stacksToSortedList(SoilCache.SOILS.keySet())) {
			helper.placeNextBlock(
				potState, pot -> {
					pot.inventories.soilInventory.setStackInSlot(0, entry.copy());
					pot.setChanged();
					pot.notifyClients(false);
				}
			);
		}

		return 0;
	}
}
