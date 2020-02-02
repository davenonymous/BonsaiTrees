package com.davenonymous.bonsaitrees2.command;

import com.davenonymous.bonsaitrees2.block.ModObjects;
import com.davenonymous.bonsaitrees2.registry.SoilCompatibility;
import com.davenonymous.bonsaitrees2.registry.soil.SoilInfo;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

import java.util.Set;
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

        ModObjects.saplingRecipeHelper.getRecipeStream(context.getSource().getWorld().getRecipeManager()).forEach(sapling -> {
            Set<SoilInfo> soilInfo = SoilCompatibility.INSTANCE.getValidSoilsForSapling(sapling);
            String soils = String.join(", ", soilInfo.stream().map(s -> s.getId().toString()).collect(Collectors.toList()));
            StringTextComponent message = new StringTextComponent(
                    String.format("%s <- %s [soils: %s]", sapling.getId(), sapling.ingredient.serialize(), soils)
            );
            context.getSource().sendFeedback(message, false);
        });

        return 0;
    }
}
