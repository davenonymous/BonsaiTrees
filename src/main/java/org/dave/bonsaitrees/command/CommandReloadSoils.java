package org.dave.bonsaitrees.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import org.dave.bonsaitrees.BonsaiTrees;
import org.dave.bonsaitrees.base.CommandBaseExt;
import org.dave.bonsaitrees.integration.IntegrationRegistry;

public class CommandReloadSoils extends CommandBaseExt {
    @Override
    public String getName() {
        return "reloadSoils";
    }

    @Override
    public boolean isAllowed(EntityPlayer player, boolean creative, boolean isOp) {
        return isOp;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        BonsaiTrees.instance.soilRegistry.clear();
        IntegrationRegistry.registerSoilIntegrations();

        BonsaiTrees.instance.soilCompatibility.updateCompatibility(BonsaiTrees.instance.soilRegistry, BonsaiTrees.instance.typeRegistry);
    }
}
