package com.davenonymous.bonsaitrees.command;

import com.davenonymous.bonsaitrees.command.list.ListDrops;
import com.davenonymous.bonsaitrees.command.list.ListSaplings;
import com.davenonymous.bonsaitrees.command.place.PlaceCamouflageTest;
import com.davenonymous.bonsaitrees.command.place.PlaceDemoDisplay;
import com.davenonymous.bonsaitrees.command.place.PlaceSoilTest;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class BonsaiCommand {

	public static boolean canDestroyWorld(CommandSourceStack source) {
		if(!source.hasPermission(source.getServer().getOperatorUserPermissionLevel())) {
			return false;
		}
		if(source.getServer().isDedicatedServer()) {
			return false;
		}

		if(!source.getLevel().isFlat()) {
			return false;
		}

		return true;
	}

	public static LiteralArgumentBuilder<CommandSourceStack> register(CommandDispatcher<CommandSourceStack> dispatcher) {
		return Commands.literal("bonsai")
			.then(Commands.literal("list")
				.then(ListSaplings.register(dispatcher))
				.then(ListDrops.register(dispatcher))

			).then(Commands.literal("place")
				.requires(BonsaiCommand::canDestroyWorld)
				.then(Commands.literal("saplings")
					.then(PlaceDemoDisplay.register())
				)
				.then(Commands.literal("camouflage")
					.then(PlaceCamouflageTest.register())
				)
				.then(Commands.literal("soil")
					.then(PlaceSoilTest.register())
				)
			)
			;
	}
}
