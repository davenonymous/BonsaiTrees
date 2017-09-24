package org.dave.bonsaitrees.command;

import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.TreeManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.dave.bonsaitrees.base.BaseTreeType;
import org.dave.bonsaitrees.base.CommandBaseExt;
import org.dave.bonsaitrees.trees.TreeShape;
import org.dave.bonsaitrees.trees.TreeShapeRegistry;
import org.dave.bonsaitrees.trees.TreeTypeForestry;
import org.dave.bonsaitrees.trees.TreeTypeRegistry;
import org.dave.bonsaitrees.utility.Logz;

import java.util.Map;
import java.util.Random;


public class CommandGenerateMissingForestryShapes extends CommandBaseExt {
    @Override
    public String getName() {
        return "generateMissingForestryShapes";
    }

    @Override
    public boolean isAllowed(EntityPlayer player, boolean creative, boolean isOp) {
        return super.isAllowed(player, creative, isOp);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(!(sender.getCommandSenderEntity() instanceof EntityPlayer)) {
            return;
        }

        int numShapes = 3;
        EntityPlayer player = (EntityPlayer)sender.getCommandSenderEntity();

        if(args.length < 1 || !args[0].equals("yes")) {
            player.sendMessage(new TextComponentTranslation("commands.bonsaitrees.generateMissingForestryShapes.exception.confirm_with_yes"));
            return;
        }

        RayTraceResult result = player.rayTrace(32.0d, 0.0f);
        if(result.typeOfHit != RayTraceResult.Type.BLOCK) {
            player.sendMessage(new TextComponentTranslation("commands.bonsaitrees.saveTreeShape.exception.not_looking_at_block"));
            return;
        }
        BlockPos targetPos = result.getBlockPos().up();
        sender.getEntityWorld().setBlockToAir(targetPos);

        for(BaseTreeType type : TreeTypeRegistry.getAllTypes()) {
            if(!(type instanceof TreeTypeForestry)) {
                continue;
            }

            int shapes = TreeShapeRegistry.getShapeCountForType(type);
            if (shapes == 0) {
                ITree tree = ((TreeTypeForestry) type).getForestryTreeType();

                for(int i = 0; i < numShapes; i++) {
                    clearArea(sender.getEntityWorld(), targetPos, 32);

                    for (int x = 0; x < tree.getGirth(); x++) {
                        for (int z = 0; z < tree.getGirth(); z++) {
                            TreeManager.treeRoot.plantSapling(player.getEntityWorld(), tree, player.getGameProfile(), targetPos.add(x, 0, z));
                        }
                    }

                    WorldGenerator treeGen = tree.getTreeGenerator(player.getEntityWorld(), player.getPosition(), true);
                    treeGen.generate(sender.getEntityWorld(), new Random(), targetPos);

                    TreeShape treeShape = new TreeShape(TreeTypeForestry.getCleanIdForTree(tree));
                    treeShape.setBlocksByFloodFill(player.getEntityWorld(), targetPos);
                    if (treeShape.getBlocks().size() == 0) {
                        player.sendMessage(new TextComponentTranslation("commands.bonsaitrees.saveTreeShape.exception.can_not_determine_shape"));
                        return;
                    }


                    for (Map.Entry<BlockPos, IBlockState> entry : treeShape.getBlocks().entrySet()) {
                        if (entry.getValue().getBlock().getRegistryName().toString().equals("forestry:leaves")) {
                            ItemStack leafStack = tree.getGenome().getPrimary().getLeafProvider().getDecorativeLeaves();
                            Item leafItem = leafStack.getItem();
                            if (leafItem instanceof ItemBlock) {
                                treeShape.getBlocks().put(entry.getKey(), ((ItemBlock) leafItem).getBlock().getStateFromMeta(leafStack.getMetadata()));
                            }
                        }

                    }

                    String filename = treeShape.saveToFile();
                    Logz.info("Created shape file: %s", filename);
                }

                clearArea(sender.getEntityWorld(), targetPos, 32);
            }
        }
    }

    private void clearArea(World world, BlockPos pos, int areaSize) {
        for(int deltaX = 0; deltaX < areaSize; deltaX++) {
            for(int deltaY = 0; deltaY < areaSize; deltaY++) {
                for(int deltaZ = 0; deltaZ < areaSize; deltaZ++) {
                    int x = pos.getX() - areaSize / 2;
                    int y = pos.getY();
                    int z = pos.getZ() - areaSize / 2;

                    x += deltaX;
                    y += deltaY;
                    z += deltaZ;

                    world.setBlockToAir(new BlockPos(x, y, z));
                }
            }
        }
    }
}
