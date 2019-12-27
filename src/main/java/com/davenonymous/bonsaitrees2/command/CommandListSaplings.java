package com.davenonymous.bonsaitrees2.command;

import com.davenonymous.bonsaitrees2.registry.RecipeTypes;
import com.davenonymous.bonsaitrees2.registry.sapling.SaplingInfo;
import com.davenonymous.bonsaitrees2.util.Logz;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;
import java.util.stream.Collectors;

public class CommandListSaplings implements Command<CommandSource> {
    private static final CommandListSaplings CMD = new CommandListSaplings();

    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        return Commands.literal("sapling")
                .requires(cs -> cs.hasPermissionLevel(0))
                .executes(CMD);
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        if(context.getSource().getWorld() == null) {
            return 0;
        }

        context.getSource().sendFeedback(new StringTextComponent("Registered saplings:"), false);

        List<SaplingInfo> saplings = context.getSource().getWorld().getRecipeManager().getRecipes().stream().filter(r -> r.getType() == RecipeTypes.saplingRecipeType).map(r -> (SaplingInfo)r).collect(Collectors.toList());
        for(SaplingInfo sapling : saplings) {
            context.getSource().sendFeedback(new StringTextComponent(sapling.getTreeId().toString() + " => " + sapling.ingredient.serialize().toString()), false);
        }

        return 0;
    }
}
