package com.davenonymous.bonsaitrees2.command;

import com.davenonymous.bonsaitrees2.block.ModObjects;
import com.davenonymous.bonsaitrees2.registry.sapling.SaplingDrop;
import com.davenonymous.bonsaitrees2.registry.sapling.SaplingInfo;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.Optional;
import java.util.stream.Stream;

public class CommandListSaplingDrops implements Command<CommandSource> {
    private static final CommandListSaplingDrops CMD = new CommandListSaplingDrops();

    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        return Commands.literal("drops")
                .then(
                    Commands.argument("type", StringArgumentType.string())
                        .suggests((context, builder) -> {
                            Stream<String> saplingIds = ModObjects.saplingRecipeHelper.getRecipeStream(context.getSource().getWorld().getRecipeManager()).map(r -> r.getId().toString());
                            return ISuggestionProvider.suggest(saplingIds, builder);
                        })
                        .executes(CMD)
                )
                .requires(cs -> cs.hasPermissionLevel(0))
                .executes(CMD);
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        if(context.getSource().getWorld() == null) {
            return 0;
        }

        String type = StringArgumentType.getString(context, "type");
        Optional<SaplingInfo> optSaplingInfo = ModObjects.saplingRecipeHelper.getRecipeStream(context.getSource().getWorld().getRecipeManager()).filter(s -> s.getId().toString().equals(type)).findFirst();
        if(!optSaplingInfo.isPresent()) {
            context.getSource().sendFeedback(new StringTextComponent("Unknown bonsai tree: " + type), false);
            return 0;
        }

        SaplingInfo saplingInfo = optSaplingInfo.get();
        context.getSource().sendFeedback(new StringTextComponent("Registered drops for bonsai tree: " + type), false);
        for(SaplingDrop drop : saplingInfo.drops) {
            ITextComponent stackName = drop.resultStack.getDisplayName();
            stackName = stackName.appendSibling(new StringTextComponent(String.format(" [chance=%.2f, rolls=%d]", drop.chance, drop.rolls)));
            context.getSource().sendFeedback(stackName, false);
        }

        return 1;
    }
}
