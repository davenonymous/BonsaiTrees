package com.davenonymous.bonsaitrees2.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class ModCommands {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralCommandNode<CommandSource> cmdBonsai = dispatcher.register(
            Commands.literal("bonsai")
                .then(CommandTreeCreator.register(dispatcher))
                .then(Commands.literal("list")
                    .then(CommandListSoils.register(dispatcher))
                    .then(CommandListSaplings.register(dispatcher))
                    .then(CommandListSaplingDrops.register(dispatcher))
                )
        );
    }
}
