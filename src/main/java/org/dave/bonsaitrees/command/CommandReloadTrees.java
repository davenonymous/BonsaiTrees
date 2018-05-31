package org.dave.bonsaitrees.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import org.dave.bonsaitrees.BonsaiTrees;
import org.dave.bonsaitrees.base.CommandBaseExt;
import org.dave.bonsaitrees.integration.IntegrationRegistry;
import org.dave.bonsaitrees.trees.TreeShapeRegistry;

public class CommandReloadTrees extends CommandBaseExt {
    @Override
    public String getName() {
        return "reloadTrees";
    }

    @Override
    public boolean isAllowed(EntityPlayer player, boolean creative, boolean isOp) {
        return isOp;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        BonsaiTrees.instance.typeRegistry.clear();
        IntegrationRegistry.registerBonsaiIntegrations();
        TreeShapeRegistry.init();

        // We can not call this here as this call can only exist on the client
        //TESRBonsaiPot.clearGlLists();
    }
}
