package com.davenonymous.bonsaitrees2.command;

import com.davenonymous.libnonymous.network.Networking;
import com.davenonymous.libnonymous.render.MultiblockBlockModel;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ILocationArgument;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;


public class CommandModelSave implements Command<CommandSource> {
    private static final CommandModelSave CMD = new CommandModelSave();

    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        return Commands.literal("save")
            .requires(cs -> cs.hasPermissionLevel(0))
            .then(Commands.argument("type", StringArgumentType.string())
                .then(Commands.argument("location", Vec3Argument.vec3())
                    .executes(CMD)
                )
            )
            .executes(context -> {
                context.getSource().sendFeedback(new StringTextComponent("You need to specify a type id and location!"), false);
                return 0;
            });
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().asPlayer();

        String type = StringArgumentType.getString(context, "type");
        ILocationArgument location = Vec3Argument.getLocation(context, "location");

        MultiblockBlockModel model = new MultiblockBlockModel(new ResourceLocation(type));
        model.setBlocksByFloodFill(context.getSource().getWorld(), location.getBlockPos(context.getSource()));
        if(model.getBlockCount() == 0) {
            context.getSource().sendFeedback(new StringTextComponent("Unable to determine model. No blocks have been selected."), false);
            return 0;
        }

        if(model.getBlockCount() > 16*16*16) {
            context.getSource().sendFeedback(new StringTextComponent("There are many blocks in your model! You will probably run into issues!"), false);
        }

        String modelJson = model.serializePretty();
        Networking.sendClipboardMessage(player, modelJson);
        context.getSource().sendFeedback(new StringTextComponent("Model copied to your clipboard"), false);
        return 0;
    }
}
