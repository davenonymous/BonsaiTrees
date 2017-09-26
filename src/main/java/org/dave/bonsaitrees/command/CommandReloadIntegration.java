package org.dave.bonsaitrees.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import org.dave.bonsaitrees.base.CommandBaseExt;
import org.dave.bonsaitrees.trees.TreeShapeRegistry;

public class CommandReloadIntegration extends CommandBaseExt {
    @Override
    public String getName() {
        return "reloadIntegration";
    }

    @Override
    public boolean isAllowed(EntityPlayer player, boolean creative, boolean isOp) {
        return isOp;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        TreeShapeRegistry.reload();
        /*
        if(args.length == 1) {
            // reload a particular one
            TreeTypeRegistry.reload(args[0]);
        } else {
            // reload all
            TreeTypeRegistry.reload();
        }
        */
    }
}
