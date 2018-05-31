package org.dave.bonsaitrees.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import org.dave.bonsaitrees.base.CommandBaseExt;
import org.dave.bonsaitrees.trees.TreeShape;

public class CommandSaveShape extends CommandBaseExt {
    @Override
    public String getName() {
        return "saveTreeShape";
    }

    @Override
    public boolean isAllowed(EntityPlayer player, boolean creative, boolean isOp) {
        return isOp;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (!(sender.getCommandSenderEntity() instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();

        if (args.length != 1) {
            player.sendMessage(new TextComponentTranslation("commands.bonsaitrees.saveTreeShape.usage"));
            return;
        }

        // Get the block set we are looking at
        RayTraceResult result = player.rayTrace(16.0d, 0.0f);
        if (result.typeOfHit != RayTraceResult.Type.BLOCK) {
            player.sendMessage(new TextComponentTranslation("commands.bonsaitrees.saveTreeShape.exception.not_looking_at_block"));
            return;
        }

        BlockPos targetPos = result.getBlockPos();

        TreeShape treeShape = new TreeShape(args[0]);
        treeShape.setBlocksByFloodFill(player.getEntityWorld(), targetPos);
        if (treeShape.getBlocks().size() == 0) {
            player.sendMessage(new TextComponentTranslation("commands.bonsaitrees.saveTreeShape.exception.can_not_determine_shape"));
            return;
        }

        String filename = treeShape.saveToFile();
        if(filename != null) {
            player.sendMessage(new TextComponentTranslation("commands.bonsaitrees.saveTreeShape.wrote_shape_to_file", filename));
        } else {
            player.sendMessage(new TextComponentTranslation("commands.bonsaitrees.saveTreeShape.error_while_saving_shape", filename));
        }

    }
}
