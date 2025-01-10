package com.davenonymous.bonsaitrees.command.list;

import com.davenonymous.bonsaitrees.setup.cache.BonsaiCache;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.fml.ModList;

public class ListSaplings implements Command<CommandSourceStack> {
	private static final ListSaplings INSTANCE = new ListSaplings();

	private ListSaplings() {
	}

	public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
		return Commands.literal("sapling").requires(cs -> cs.hasPermission(0)).executes(INSTANCE);
	}

	@Override
	public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		context.getSource().sendSuccess(() -> Component.literal("Registered saplings:"), false);

		BonsaiCache.BONSAI_BY_ITEM.keySet().stream()
			.sorted((itemA, itemB) -> itemA.getCreatorModId(itemA.getDefaultInstance()).compareTo(itemB.getCreatorModId(itemB.getDefaultInstance()))).forEach((item) -> {
				MutableComponent mutablecomponent = Component.empty();
				mutablecomponent.append("- ");
				var modId = item.builtInRegistryHolder().getKey().location().getNamespace();
				ModList.get().getMods().stream().filter(iModInfo -> iModInfo.getModId().equals(modId)).findFirst().ifPresent(iModInfo -> {
					mutablecomponent.append(" (");
					mutablecomponent.append(iModInfo.getDisplayName());
					mutablecomponent.append(") ");
				});
				mutablecomponent.append(item.getDefaultInstance().getHoverName());
				context.getSource().sendSuccess(() -> mutablecomponent, false);
			});
		return 0;
	}
}
