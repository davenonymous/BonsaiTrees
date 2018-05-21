package org.dave.bonsaitrees.integration.mods;


import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.ForestryAPI;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.dave.bonsaitrees.api.BonsaiIntegration;
import org.dave.bonsaitrees.api.IBonsaiIntegration;
import org.dave.bonsaitrees.api.IBonsaiTreeType;
import org.dave.bonsaitrees.api.ITreeTypeRegistry;
import org.dave.bonsaitrees.integration.TreeTypeForestry;
import org.dave.bonsaitrees.utility.Logz;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.dave.bonsaitrees.api.BonsaiDropChances.*;

@BonsaiIntegration(mod = "forestry")
public class Forestry implements IBonsaiIntegration {
    private List<String> blockedTrees = Arrays.asList(
            "for.trees.species.giantSequoia",
            "for.trees.species.coastSequoia"
    );

    @Override
    public void registerTrees(ITreeTypeRegistry registry) {
        if(!ForestryAPI.moduleManager.isModuleEnabled("forestry", "arboriculture")) {
            Logz.info("Arboriculture module is disabled. Not loading forestry trees!");
            return;
        }

        for(ITree tree : TreeManager.treeRoot.getIndividualTemplates()) {
            String id = tree.getGenome().getPrimary().getUnlocalizedName();
            if(blockedTrees.contains(id)) {
                continue;
            }

            TreeTypeForestry treeType = new TreeTypeForestry(tree);

            // Logs
            ItemStack woodStack = tree.getGenome().getPrimary().getWoodProvider().getWoodStack().copy();
            woodStack.setCount(logAmount);
            treeType.addDrop(woodStack, logChance);

            // Leaves
            ItemStack leafStack = tree.getGenome().getPrimary().getLeafProvider().getDecorativeLeaves().copy();
            leafStack.setCount(leafAmount);
            treeType.addDrop(leafStack, leafChance);

            // Fruits
            for(Map.Entry<ItemStack, Float> entry : tree.getProducts().entrySet()) {
                ItemStack fruitStack = entry.getKey().copy();
                fruitStack.setCount(fruitAmount);
                Float chance = entry.getValue() * fruitChance;

                treeType.addDrop(fruitStack, chance);
            }

            // Sticks
            treeType.addDrop(new ItemStack(Items.STICK, stickAmount), stickChance);

            // Sapling
            ItemStack saplingStack = treeType.getExampleStack();
            saplingStack.setCount(saplingAmount);
            treeType.addDrop(saplingStack, saplingChance);

            registry.registerTreeType(this, treeType);
        }
    }

    @Override
    public void generateTree(IBonsaiTreeType type, World world, BlockPos pos, Random rand) {
        ITree tree = ((TreeTypeForestry) type).getForestryTreeType();
        for (int x = 0; x < tree.getGirth(); x++) {
            for (int z = 0; z < tree.getGirth(); z++) {
                TreeManager.treeRoot.plantSapling(world, tree, null, pos.add(x, 0.0, z));
            }
        }

        WorldGenerator treeGen = tree.getTreeGenerator(world, pos, true);
        treeGen.generate(world, rand, pos);
    }

    @Override
    public void modifyTreeShape(IBonsaiTreeType type, Map<BlockPos, IBlockState> blocks) {
        ITree tree = ((TreeTypeForestry) type).getForestryTreeType();

        for(Map.Entry<BlockPos, IBlockState> entry : blocks.entrySet()) {
            if (entry.getValue().getBlock().getRegistryName().toString().equals("forestry:leaves")) {
                ItemStack leafStack = tree.getGenome().getPrimary().getLeafProvider().getDecorativeLeaves();
                Item leafItem = leafStack.getItem();
                if (leafItem instanceof ItemBlock) {
                    blocks.put(entry.getKey(), ((ItemBlock)leafItem).getBlock().getStateFromMeta(leafStack.getMetadata()));
                }
            }
        }
    }
}
