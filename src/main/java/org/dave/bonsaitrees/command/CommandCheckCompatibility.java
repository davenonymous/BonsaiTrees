package org.dave.bonsaitrees.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import org.dave.bonsaitrees.BonsaiTrees;
import org.dave.bonsaitrees.api.IBonsaiSoil;
import org.dave.bonsaitrees.api.IBonsaiTreeType;
import org.dave.bonsaitrees.base.CommandBaseExt;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class CommandCheckCompatibility extends CommandBaseExt {
    @Override
    public String getName() {
        return "checkCompatibility";
    }

    @Override
    public boolean isAllowed(EntityPlayer player, boolean creative, boolean isOp) {
        return true;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length != 1) {
            sender.sendMessage(new TextComponentTranslation("commands.bonsaitrees.checkCompatibility.exception.specify_tree_or_soil"));
            return;
        }

        String searchTerm = args[0];
        IBonsaiTreeType treeType = BonsaiTrees.instance.typeRegistry.getTypeByName(searchTerm);
        if(treeType != null) {
            List<IBonsaiSoil> supportedSoils = new LinkedList<>(BonsaiTrees.instance.soilCompatibility.getValidSoilsForTree(treeType));
            supportedSoils.sort(Comparator.comparing(IBonsaiSoil::getName));
            sender.sendMessage(new TextComponentTranslation("commands.bonsaitrees.checkCompatibility.result_title_soils", supportedSoils.size()));
            for(IBonsaiSoil soil : supportedSoils) {
                sender.sendMessage(new TextComponentString(" - " + soil.getName()));
            }
            return;
        }

        IBonsaiSoil soil = BonsaiTrees.instance.soilRegistry.getTypeByName(searchTerm);
        if(soil != null) {
            List<IBonsaiTreeType> supportedTrees = new LinkedList<>(BonsaiTrees.instance.soilCompatibility.getValidTreesForSoil(soil));
            supportedTrees.sort(Comparator.comparing(IBonsaiTreeType::getName));
            sender.sendMessage(new TextComponentTranslation("commands.bonsaitrees.checkCompatibility.result_title_trees", supportedTrees.size()));
            for(IBonsaiTreeType tree : supportedTrees) {
                sender.sendMessage(new TextComponentString(" - " + tree.getName()));
            }
            return;
        }

        sender.sendMessage(new TextComponentTranslation("commands.bonsaitrees.checkCompatibility.exception.no_valid_soil_or_tree_found"));
    }
}
