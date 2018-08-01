package org.dave.bonsaitrees.integration;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.TreeManager;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import org.dave.bonsaitrees.api.IBonsaiTreeType;
import org.dave.bonsaitrees.api.TreeTypeDrop;
import org.dave.bonsaitrees.utility.Logz;

import java.util.*;

public class TreeTypeForestry implements IBonsaiTreeType {
    private String name;
    ITree treeType;

    ItemStack exampleStack;

    private List<TreeTypeDrop> drops = new ArrayList<>();

    public TreeTypeForestry(ITree treeType) {
        this.name = getCleanIdForTree(treeType);
        this.treeType = treeType;
    }

    public void addDrop(ItemStack stack, float chance) {
        this.drops.add(new TreeTypeDrop(stack.copy(), chance));
    }

    public ITree getForestryTreeType() {
        return treeType;
    }

    public static String getCleanIdForTree(ITree tree) {
        String modId = tree.getGenome().getPrimary().getModID();
        String treeId = tree.getGenome().getPrimary().getUnlocalizedName();
        if(modId.equals("forestry")) {
            treeId = treeId.replace("for.trees.species.", "");
        } else if(modId.equals("extratrees")) {
            treeId = treeId.replace("extratrees.species.", "");
            treeId = treeId.replace(".name", "");
        }
        return tree.getGenome().getPrimary().getModID() + ":" + treeId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<TreeTypeDrop> getDrops() {
        return drops;
    }

    @Override
    public Set<String> getCompatibleSoilTags() {
        return new HashSet<>(Arrays.asList("dirt", "grass"));
    }

    @Override
    public boolean worksWith(ItemStack stack) {
        if(!TreeManager.treeRoot.isMember(stack)) {
            return false;
        }

        ITree forestryTree = TreeManager.treeRoot.getMember(stack);
        return getName().equals(getCleanIdForTree(forestryTree));
    }

    @Override
    public ItemStack getExampleStack() {
        if(exampleStack == null || exampleStack.isEmpty()) {
            for(ITree forestryTree : TreeManager.treeRoot.getIndividualTemplates()) {
                if(getName().equals(getCleanIdForTree(forestryTree))) {
                    exampleStack = forestryTree.getGenome().getSpeciesRoot().getMemberStack(forestryTree, EnumGermlingType.SAPLING);
                }
            }
        }

        if(exampleStack == null) {
            Logz.info("Could not find example stack for tree: %s", getName());
            exampleStack = new ItemStack(Blocks.SAPLING, 1, 0);
        }

        return exampleStack;
    }
}
