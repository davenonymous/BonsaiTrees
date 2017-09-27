package org.dave.bonsaitrees.integration.mods;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.dave.bonsaitrees.api.*;

import java.util.Random;

import static org.dave.bonsaitrees.api.BonsaiDropChances.*;

@BonsaiIntegration(mod = "ic2")
public class IndustrialCraft2 implements IBonsaiIntegration {
    @Override
    public void registerTrees(ITreeTypeRegistry registry) {
        TreeTypeSimple treeType = new TreeTypeSimple("ic2:rubber", "ic2:sapling", 0);
        treeType.addDrop(new ItemStack(Items.STICK, stickAmount), stickChance);
        treeType.addDrop("ic2:rubber_wood", logAmount, 0, logChance);
        treeType.addDrop("ic2:sapling", saplingAmount, 0, saplingChance);
        treeType.addDrop("ic2:leaves", leafAmount, 0, leafChance);

        // resin
        treeType.addDrop("ic2:misc_resource", fruitAmount, 4, fruitChance);

        treeType.setGrowTime((int)(treeType.getGrowTime() * 1.5));

        registry.registerTreeType(this, treeType);
    }

    @Override
    public void generateTree(IBonsaiTreeType type, World world, BlockPos pos, Random rand) {

    }
}
