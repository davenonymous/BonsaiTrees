package com.davenonymous.bonsaitrees.command.list;

import com.davenonymous.bonsaitrees.command.arguments.SaplingArgument;
import com.davenonymous.bonsaitrees.lib.util.LootHelper;
import com.davenonymous.bonsaitrees.setup.cache.BonsaiCache;
import com.davenonymous.bonsaitrees.setup.data.BonsaiInfo;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Optional;

public class ListDrops implements Command<CommandSourceStack> {
	private static final ListDrops INSTANCE = new ListDrops();

	private ListDrops() {
	}

	public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
		return Commands.literal("drops").then(Commands.argument("sapling", SaplingArgument.saplingArgument()).requires(cs -> cs.hasPermission(0)).executes(INSTANCE));
	}

	@Override
	public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		ResourceLocation inputSapling = context.getArgument("sapling", ResourceLocation.class);

		BonsaiInfo bonsai = BonsaiCache.BONSAI_BY_RESOURCE.get(inputSapling);
		if(bonsai == null) {
			context.getSource().sendFailure(Component.literal("No bonsai found for: " + inputSapling));
			return 0;
		}

		ResourceKey<LootTable> lootTableId = bonsai.lootTable();
		LootTable lootTable = context.getSource().getLevel().getServer().reloadableRegistries().getLootTable(lootTableId);
		if(lootTable == null) {
			context.getSource().sendFailure(Component.literal("No loot table found for: " + lootTableId));
			return 0;
		}

		LootParams lootParams = new LootParams.Builder(context.getSource().getLevel()).create(LootContextParamSets.EMPTY);

		List<LootHelper.LootTableDrop> drops = LootHelper.getLootTableDrops(
			lootTableId, context.getSource().getLevel(), new LootContext.Builder(lootParams).create(Optional.empty()));
		context.getSource().sendSuccess(() -> Component.literal(drops.size() + " drops for: " + inputSapling), false);
		for(var lootDrop : drops) {
			var drop = lootDrop.stack();
			context.getSource().sendSuccess(drop::getHoverName, false);
		}

		return 0;
	}
}
