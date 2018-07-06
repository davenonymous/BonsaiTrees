package org.dave.bonsaitrees.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import org.dave.bonsaitrees.base.CommandBaseExt;
import org.dave.bonsaitrees.trees.ShapeGenerator;

public class CommandGenerateMissingShapes extends CommandBaseExt {
    @Override
    public String getName() {
        return "generateMissingShapes";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(!(sender.getCommandSenderEntity() instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer)sender.getCommandSenderEntity();

        if(args.length < 1 || !args[0].equals("yes")) {
            player.sendMessage(new TextComponentTranslation("commands.bonsaitrees.generateMissingShapes.exception.confirm_with_yes"));
            return;
        }

        RayTraceResult result = player.rayTrace(32.0d, 0.0f);
        if(result.typeOfHit != RayTraceResult.Type.BLOCK) {
            player.sendMessage(new TextComponentTranslation("commands.bonsaitrees.saveTreeShape.exception.not_looking_at_block"));
            return;
        }

        BlockPos targetPos = result.getBlockPos().up();
        ShapeGenerator.generateMissingShapes(sender, sender.getEntityWorld(), targetPos);
    }
}
