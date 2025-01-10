package com.davenonymous.bonsaitrees.command.place;

import com.davenonymous.bonsaitrees.blocks.BonsaiPotBlockEntity;
import com.davenonymous.bonsaitrees.lib.util.LootHelper;
import com.davenonymous.bonsaitrees.lib.util.Sorting;
import com.davenonymous.bonsaitrees.setup.ModBlocks;
import com.davenonymous.bonsaitrees.setup.cache.BonsaiCache;
import com.davenonymous.bonsaitrees.setup.data.BonsaiInfo;
import com.davenonymous.bonsaitrees.setup.data.SoilType;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Optional;

public class PlaceDemoDisplay implements Command<CommandSourceStack> {
	public static final PlaceDemoDisplay INSTANCE = new PlaceDemoDisplay();

	private PlaceDemoDisplay() {
	}

	public static ArgumentBuilder<CommandSourceStack, ?> register() {
		return Commands.argument("pos", BlockPosArgument.blockPos()).executes(INSTANCE);
	}

	@Override
	public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "pos");
		ServerLevel world = context.getSource().getLevel();
		BlockState state = ModBlocks.BONSAI_POT.get().defaultBlockState();

		int total = BonsaiCache.BONSAI_BY_ITEM.keySet().size();
		PlacementHelper<BonsaiPotBlockEntity> helper = new PlacementHelper<>(pos, context.getSource().getLevel(), total);
		helper.ascendingHeight = true;
		helper.placeArena();

		for(Item item : Sorting.toSortedList(BonsaiCache.BONSAI_BY_ITEM.keySet())) {
			BonsaiInfo bonsai = BonsaiCache.BONSAI_BY_ITEM.get(item);

			SoilType soilType = bonsai.validSoilTypes(world.registryAccess()).getFirst();
			ItemStack soilStack = soilType.defaultItem().copy();

			helper.placeNextBlock(
				state, pot -> {
					pot.inventories.saplingInventory.setStackInSlot(0, new ItemStack(item));
					pot.inventories.soilInventory.setStackInSlot(0, soilStack);
					pot.production.init();
					pot.production.growTicks = pot.production.getRequiredGrowTicks();

					LootParams lootParams = new LootParams.Builder(context.getSource().getLevel()).create(LootContextParamSets.EMPTY);
					LootContext lootContext = new LootContext.Builder(lootParams).create(Optional.empty());
					List<LootHelper.LootTableDrop> drops = LootHelper.getLootTableDrops(bonsai.lootTable(), world, lootContext);
					for(var drop : drops) {
						if(pot.inventories.camouflageInventory.isItemValid(0, drop.stack())) {
							pot.inventories.camouflageInventory.setStackInSlot(0, drop.stack());
							break;
						}
					}

					pot.setChanged();
					pot.notifyClients(false);
				}
			);
		}

		return 0;
	}
}
