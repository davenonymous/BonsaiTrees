package com.davenonymous.bonsaitrees.command.place;

import com.davenonymous.bonsaitrees.blocks.BonsaiPotBlockEntity;
import com.davenonymous.bonsaitrees.setup.ModBlocks;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class PlaceCamouflageTest implements Command<CommandSourceStack> {
	public static final PlaceCamouflageTest INSTANCE = new PlaceCamouflageTest();

	private PlaceCamouflageTest() {
	}

	public static ArgumentBuilder<CommandSourceStack, ?> register() {
		return Commands.argument("pos", BlockPosArgument.blockPos()).executes(INSTANCE);
	}

	@Override
	public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "pos");
		ServerLevel world = context.getSource().getLevel();
		BlockState state = ModBlocks.BONSAI_POT.get().defaultBlockState();

		world.setBlock(pos.above(), state, Block.UPDATE_ALL);
		BonsaiPotBlockEntity fakeBlockEntity = (BonsaiPotBlockEntity) world.getBlockEntity(pos.above());
		List<Item> allBlocks = BuiltInRegistries.BLOCK.stream()
			.map(Block::asItem)
			.filter(item -> {
				return fakeBlockEntity.inventories.camouflageInventory.isItemValid(0, new ItemStack(item));
			})
			.toList();

		int total = allBlocks.size();
		PlacementHelper<BonsaiPotBlockEntity> helper = new PlacementHelper<>(pos, context.getSource().getLevel(), total);
		helper.placeArena();

		for(var item : allBlocks) {
			helper.placeNextBlock(
				state, pot -> {
					pot.inventories.camouflageInventory.setStackInSlot(0, new ItemStack(item));

					pot.setChanged();
					pot.notifyClients(false);
				}
			);
		}

		return 0;
	}
}
