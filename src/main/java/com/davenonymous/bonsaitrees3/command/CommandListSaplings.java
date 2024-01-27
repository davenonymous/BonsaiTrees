package com.davenonymous.bonsaitrees3.command;


import com.davenonymous.bonsaitrees3.registry.SoilCompatibility;
import com.davenonymous.bonsaitrees3.registry.soil.SoilInfo;
import com.davenonymous.bonsaitrees3.setup.Registration;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.util.Set;
import java.util.stream.Collectors;

public class CommandListSaplings implements Command<CommandSourceStack> {
	private static final CommandListSaplings CMD = new CommandListSaplings();

	public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
		return Commands.literal("sapling").requires(cs -> cs.hasPermission(0)).executes(CMD);
	}

	@Override
	public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		context.getSource().sendSuccess(Component.literal("Registered saplings:"), false);

		Registration.RECIPE_HELPER_SAPLING.get().getRecipeStream(context.getSource().getLevel().getRecipeManager()).forEach(sapling -> {
			Set<SoilInfo> soilInfo = SoilCompatibility.INSTANCE.getValidSoilsForSapling(sapling);
			String soils = String.join(", ", soilInfo.stream().map(s -> s.getId().toString()).collect(Collectors.toList()));
			Component message = Component.literal(String.format("%s <- %s [soils: %s]", sapling.getId(), sapling.ingredient.toJson(), soils));
			context.getSource().sendSuccess(message, false);
		});

		return 0;
	}
}