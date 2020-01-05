package com.davenonymous.bonsaitrees2.command;

import com.davenonymous.bonsaitrees2.gui.TreeCreatorContainer;
import com.davenonymous.libnonymous.utils.RaytraceHelper;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class CommandTreeCreator implements Command<CommandSource> {
    private static final CommandTreeCreator CMD = new CommandTreeCreator();

    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        return Commands.literal("maker")
                .requires(cs -> cs.hasPermissionLevel(0))
                .executes(CMD);
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().asPlayer();

        BlockRayTraceResult trace = RaytraceHelper.rayTrace(context.getSource().getWorld(), context.getSource().asPlayer());
        BlockPos tracePos = trace.getPos();

        NetworkHooks.openGui(player, new ContainerProvider(tracePos), tracePos);
        return 0;
    }


    private static class ContainerProvider implements INamedContainerProvider {
        BlockPos pos;

        public ContainerProvider(BlockPos pos) {
            this.pos = pos;
        }

        @Override
        public ITextComponent getDisplayName() {
            return new StringTextComponent("tree_creator");
        }

        @Nullable
        @Override
        public Container createMenu(int i, PlayerInventory inv, PlayerEntity player) {
            return new TreeCreatorContainer(i, inv, pos);
        }
    }
}
