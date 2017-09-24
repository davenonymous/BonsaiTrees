package org.dave.bonsaitrees.trees;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.TreeManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.dave.bonsaitrees.base.BaseTreeType;
import org.dave.bonsaitrees.utility.Logz;

import java.util.ArrayList;
import java.util.List;

public class TreeTypeForestry extends BaseTreeType {
    ItemStack exampleStack;
    int saplingChance;
    int saplingAmount;

    public TreeTypeForestry(String typeName, int saplingAmount, int saplingChance) {
        super(typeName);
        this.saplingAmount = saplingAmount;
        this.saplingChance = saplingChance;
    }

    @Override
    public List<TreeTypeDrop> getDrops() {
        List<TreeTypeDrop> result = new ArrayList<>(super.getDrops());

        if(!getExampleStack().isEmpty()) {
            ItemStack toDropStack = getExampleStack();
            toDropStack.setCount(saplingAmount);
            result.add(new TreeTypeDrop(toDropStack, saplingChance));
        }

        return result;
    }

    @Override
    public double growRate(World world, BlockPos pos, IBlockState state, double progress) {
        return 1.0d;
    }

    @Override
    public boolean worksWith(ItemStack stack) {
        if(!TreeManager.treeRoot.isMember(stack)) {
            return false;
        }

        ITree forestryTree = TreeManager.treeRoot.getMember(stack);
        String modId = forestryTree.getGenome().getPrimary().getModID();
        String treeId = forestryTree.getGenome().getPrimary().getUnlocalizedName();
        if(modId.equals("forestry")) {
            treeId = treeId.replace("for.trees.species.", "");
        } else if(modId.equals("extratrees")) {
            treeId = treeId.replace("extratrees.species.", "");
            treeId = treeId.replace(".name", "");
        }
        treeId = forestryTree.getGenome().getPrimary().getModID() + ":" + treeId;

        return typeName.equals(treeId);
    }

    @Override
    public ItemStack getExampleStack() {
        if(exampleStack == null || exampleStack.isEmpty()) {
            for(ITree forestryTree : TreeManager.treeRoot.getIndividualTemplates()) {
                String modId = forestryTree.getGenome().getPrimary().getModID();

                String treeId = forestryTree.getGenome().getPrimary().getUnlocalizedName();
                if(modId.equals("forestry")) {
                    treeId = treeId.replace("for.trees.species.", "");
                } else if(modId.equals("extratrees")) {
                    treeId = treeId.replace("extratrees.species.", "");
                    treeId = treeId.replace(".name", "");
                }
                treeId = forestryTree.getGenome().getPrimary().getModID() + ":" + treeId;

                if(treeId.equals(typeName)) {
                    exampleStack = forestryTree.getGenome().getSpeciesRoot().getMemberStack(forestryTree, EnumGermlingType.SAPLING);
                }
            }
        }

        if(exampleStack == null) {
            Logz.info("Could not find example stack for tree: %s", this.typeName);
            exampleStack = new ItemStack(Blocks.SAPLING, 1, 0);
        }

        return exampleStack;
    }
}
