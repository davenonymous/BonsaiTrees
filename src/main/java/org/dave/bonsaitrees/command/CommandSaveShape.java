package org.dave.bonsaitrees.command;

import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import org.dave.bonsaitrees.base.CommandBaseExt;
import org.dave.bonsaitrees.misc.ConfigurationHandler;
import org.dave.bonsaitrees.misc.FloodFill;
import org.dave.bonsaitrees.trees.TreeShape;
import org.dave.bonsaitrees.utility.Logz;
import org.dave.bonsaitrees.utility.SerializationHelper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        if(!(sender.getCommandSenderEntity() instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer)sender.getCommandSenderEntity();
        ItemStack holdStack = player.getHeldItemMainhand();

        if(holdStack.isEmpty()) {
            player.sendMessage(new TextComponentTranslation("commands.bonsaitrees.saveTreeShape.exception.no_itemstack_in_mainhand"));
            return;
        }

        // Get the block set we are looking at
        RayTraceResult result = player.rayTrace(16.0d, 0.0f);
        if(result.typeOfHit != RayTraceResult.Type.BLOCK) {
            player.sendMessage(new TextComponentTranslation("commands.bonsaitrees.saveTreeShape.exception.not_looking_at_block"));
            return;
        }

        BlockPos targetPos = result.getBlockPos();
        IBlockState state = player.getEntityWorld().getBlockState(targetPos);

        FloodFill floodFill = new FloodFill(player.getEntityWorld(), targetPos);
        Map<BlockPos, IBlockState> connectedBlocks = floodFill.getConnectedBlocks();
        if(connectedBlocks.size() == 0) {
            player.sendMessage(new TextComponentTranslation("commands.bonsaitrees.saveTreeShape.exception.can_not_determine_shape"));
            return;
        }

        TreeShape treeShape = new TreeShape(holdStack);
        treeShape.setBlocks(connectedBlocks);

        String json = SerializationHelper.GSON.toJson(treeShape);
        if(args.length == 0) {
            Logz.info("JSON for shape:\n%s", json);
        } else if(args.length == 1) {
            String sane = args[0].replaceAll("[^a-zA-Z0-9\\._]+", "_");
            File dstFile = getNextFile(sane.replace(".json", ""));

            Logz.info("Writing shape to file: %s", dstFile.getName());

            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(dstFile));
                writer.write(json);
                writer.close();
            } catch (IOException e) {
                Logz.warn("Could not save shape!");
                e.printStackTrace();
            }

            player.sendMessage(new TextComponentTranslation("commands.bonsaitrees.saveTreeShape.wrote_shape_to_file", dstFile.getName()));
        }
    }

    private static final Pattern p = Pattern.compile("0*([0-9]+)\\.json$");
    private static File getNextFile(String prefix) {
        int highestNum = 0;
        for(File file : ConfigurationHandler.treeShapesDir.listFiles()) {
            String fileName = file.getName();
            if (!fileName.endsWith(".json") || !fileName.startsWith(prefix)) {
                continue;
            }

            Matcher m = p.matcher(fileName);
            if(!m.find()) {
                continue;
            }

            int num = Integer.parseInt(m.group(1));
            if(num > highestNum) {
                highestNum = num;
            }
        }

        int nextNum = highestNum+1;
        String fileName = String.format("%s%03d.json", prefix, nextNum);
        return new File(ConfigurationHandler.treeShapesDir, fileName);
    }

}
