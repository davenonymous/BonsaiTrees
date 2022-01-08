package com.davenonymous.bonsaitrees3.command;

import com.davenonymous.bonsaitrees3.BonsaiTrees3;
import com.davenonymous.bonsaitrees3.libnonymous.serialization.MultiblockBlockModel;
import com.davenonymous.bonsaitrees3.network.Networking;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class CommandTreeCreator implements Command<CommandSourceStack> {
	private static final CommandTreeCreator CMD = new CommandTreeCreator();

	public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
		return Commands.literal("maker").then(Commands.argument("pos", BlockPosArgument.blockPos()).requires(cs -> cs.hasPermission(0)).executes(CMD));
	}

	@Override
	public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		MultiblockBlockModel model = new MultiblockBlockModel(new ResourceLocation(BonsaiTrees3.MODID, "temporary_tree_model"));

		BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "pos");
		model.setBlocksByFloodFill(context.getSource().getLevel(), pos);

		context.getSource().sendSuccess(new TextComponent(String.format("Model consists of %d blocks", model.getBlockCount())), false);

		for(var state : model.reverseBlocks.keySet()) {
			int count = model.reverseBlocks.get(state).size();
			context.getSource().sendSuccess(new TextComponent(String.format("%d %s", count, state)), false);
		}

		ServerPlayer player = context.getSource().getPlayerOrException();
		Networking.sendModelToClipboard(player.connection.connection, model);

		context.getSource().sendSuccess(new TextComponent("Model json copied to the clipboard!"), false);

		return 0;
	}
}