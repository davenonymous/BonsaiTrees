package org.dave.bonsaitrees.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import org.dave.bonsaitrees.BonsaiTrees;
import org.dave.bonsaitrees.api.IBonsaiTreeType;
import org.dave.bonsaitrees.base.CommandBaseExt;

import java.util.LinkedList;
import java.util.List;

public class CommandListTrees extends CommandBaseExt {
    @Override
    public String getName() {
        return "listTrees";
    }

    @Override
    public boolean isAllowed(EntityPlayer player, boolean creative, boolean isOp) {
        return true;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        sender.sendMessage(new TextComponentTranslation("commands.bonsaitrees.listTrees.result_title"));
        List<IBonsaiTreeType> types = new LinkedList<>(BonsaiTrees.instance.typeRegistry.getAllTypes());
        types.sort((a, b) -> a.getName().compareTo(b.getName()));
        for(IBonsaiTreeType type : types) {
            sender.sendMessage(new TextComponentString(" - " + type.getName()));
        }
    }
}
