package com.davenonymous.bonsaitrees2.command;

import com.davenonymous.bonsaitrees2.registry.RecipeTypes;
import com.davenonymous.bonsaitrees2.registry.soil.SoilInfo;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;
import java.util.stream.Collectors;

public class CommandListSoils implements Command<CommandSource> {
    private static final CommandListSoils CMD = new CommandListSoils();

    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        return Commands.literal("soil")
                .requires(cs -> cs.hasPermissionLevel(0))
                .executes(CMD);
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        if(context.getSource().getWorld() == null) {
            return 0;
        }

        context.getSource().sendFeedback(new StringTextComponent("Registered soils:"), false);
        List<SoilInfo> soils = context.getSource().getWorld().getRecipeManager().getRecipes().stream().filter(r -> r.getType() == RecipeTypes.soilRecipeType).map(r -> (SoilInfo)r).collect(Collectors.toList());
        for(SoilInfo soil : soils) {
            ResourceLocation soilId = new ResourceLocation(soil.getId().toString().substring(18).replaceFirst("/", ":"));
            context.getSource().sendFeedback(new StringTextComponent(soilId.toString() + " => " + soil.ingredient.serialize().toString()), false);
        }

        return 0;
    }
}
