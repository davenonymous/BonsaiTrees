package org.dave.bonsaitrees.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import org.dave.bonsaitrees.base.CommandBaseExt;
import org.dave.bonsaitrees.misc.ConfigurationHandler;
import org.dave.bonsaitrees.trees.TreeShape;
import org.dave.bonsaitrees.trees.TreeShapeRegistry;
import org.dave.bonsaitrees.trees.TreeShapeSerializer;
import org.dave.bonsaitrees.utility.Logz;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.stream.Collectors;

public class CommandUpgradeJsonFiles extends CommandBaseExt {

    @Override
    public String getName() {
        return "upgradeJsonFiles";
    }

    @Override
    public boolean isAllowed(EntityPlayer player, boolean creative, boolean isOp) {
        return isOp;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        File upgradeDir = new File(ConfigurationHandler.configDir, "upgradedShapes.d");
        if(!upgradeDir.exists()) {
            upgradeDir.mkdir();
        }

        for(TreeShape shape : TreeShapeRegistry.getTreeShapes().stream().sorted(Comparator.comparing(TreeShape::getTreeTypeName)).collect(Collectors.toList())) {
            Logz.info("Dumping shape in latest version: %s", shape.getFileName());

            String output = TreeShapeSerializer.serializePretty(shape);
            if(output == null) {
                continue;
            }

            File dstFile = new File(upgradeDir, shape.getFileName() +  "json");
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(dstFile));
                writer.write(output);
                writer.close();
            } catch (IOException e) {
                sender.sendMessage(new TextComponentTranslation("commands.bonsaitrees.saveTreeShape.error_while_saving_shape"));
                e.printStackTrace();
            }
        }
    }
}
