package com.davenonymous.bonsaitrees3.command;


import com.davenonymous.bonsaitrees3.setup.Registration;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;

public class CommandListSoils implements Command<CommandSourceStack> {
	private static final CommandListSoils CMD = new CommandListSoils();

	public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
		return Commands.literal("soil").requires(cs -> cs.hasPermission(0)).executes(CMD);
	}

	@Override
	public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

		context.getSource().sendSuccess(new TextComponent("Registered soils:"), false);
		Registration.RECIPE_HELPER_SOIL.getRecipeStream(context.getSource().getLevel().getRecipeManager()).forEach(soil -> {
			context.getSource().sendSuccess(new TextComponent(soil.getId().toString()), false);
		});

		return 0;
	}
}