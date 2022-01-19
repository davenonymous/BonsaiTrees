package com.davenonymous.bonsaitrees3.libnonymous.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class SimpleCommandReply implements Command<CommandSourceStack> {
	private final Component reply;
	private final boolean isError;

	public SimpleCommandReply(Component reply, boolean isError) {
		this.reply = reply;
		this.isError = isError;
	}

	public static SimpleCommandReply info(String input) {
		return new SimpleCommandReply(new TextComponent(input), false);
	}

	public static SimpleCommandReply info(String fmt, Object... data) {
		return new SimpleCommandReply(new TextComponent(String.format(fmt, data)), false);
	}

	public static SimpleCommandReply error(String input) {
		return new SimpleCommandReply(new TextComponent(input), true);
	}

	public static SimpleCommandReply error(String fmt, Object... data) {
		return new SimpleCommandReply(new TextComponent(String.format(fmt, data)), true);
	}

	@Override
	public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		var source = context.getSource();
		if(isError) {
			source.sendFailure(reply);
		} else {
			source.sendSuccess(reply, false);
		}
		return 0;
	}
}