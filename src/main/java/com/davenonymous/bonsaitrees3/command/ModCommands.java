package com.davenonymous.bonsaitrees3.command;


import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class ModCommands {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext pContext) {

		dispatcher.register(
				Commands.literal("bonsai")
						.then(CommandTreeCreator.register(dispatcher))
						.then(CommandTreeGenerator.register(dispatcher, pContext))
						.then(Commands.literal("list")
								.then(CommandListSoils.register(dispatcher))
								.then(CommandListSaplings.register(dispatcher))
								.then(CommandListSaplingDrops.register(dispatcher))));
	}
}