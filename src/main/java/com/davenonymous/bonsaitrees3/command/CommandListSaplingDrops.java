package com.davenonymous.bonsaitrees3.command;


import com.davenonymous.bonsaitrees3.registry.sapling.SaplingDrop;
import com.davenonymous.bonsaitrees3.registry.sapling.SaplingInfo;
import com.davenonymous.bonsaitrees3.setup.Registration;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.util.Optional;
import java.util.stream.Stream;

public class CommandListSaplingDrops implements Command<CommandSourceStack> {
	private static final CommandListSaplingDrops CMD = new CommandListSaplingDrops();

	public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
		return Commands.literal("drops").then(Commands.argument("type", StringArgumentType.string()).suggests((context, builder) -> {
			Stream<String> saplingIds = Registration.RECIPE_HELPER_SAPLING.get().getRecipeStream(context.getSource().getLevel().getRecipeManager()).map(r -> '"' + r.getId().toString() + '"');
			return SharedSuggestionProvider.suggest(saplingIds, builder);
		}).executes(CMD)).requires(cs -> cs.hasPermission(0)).executes(CMD);
	}

	@Override
	public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		String type = StringArgumentType.getString(context, "type");
		Optional<SaplingInfo> optSaplingInfo = Registration.RECIPE_HELPER_SAPLING.get().getRecipeStream(context.getSource().getLevel().getRecipeManager()).filter(s -> s.getId().toString().equals(type)).findFirst();
		if(!optSaplingInfo.isPresent()) {
			context.getSource().sendSuccess(Component.literal("Unknown bonsai tree: " + type), false);
			return 0;
		}

		SaplingInfo saplingInfo = optSaplingInfo.get();
		context.getSource().sendSuccess(Component.literal("Registered drops for bonsai tree: " + type), false);
		for(SaplingDrop drop : saplingInfo.drops) {
			Component stackName = drop.resultStack.getDisplayName();
			stackName.getSiblings().add(Component.literal(String.format(" [chance=%.2f, rolls=%d, silky=%s, pollinated=%s]", drop.chance, drop.rolls, drop.requiresSilkTouch ? "yes" : "false", drop.requiresBees ? "yes" : "false")));
			context.getSource().sendSuccess(stackName, false);
		}

		return 1;
	}
}