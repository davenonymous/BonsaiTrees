package org.dave.bonsaitrees.integration.mods;


import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.dave.bonsaitrees.api.*;

import java.util.Random;

import static org.dave.bonsaitrees.api.BonsaiDropChances.*;

@BonsaiIntegration(mod = "integrateddynamics")
public class IntegratedDynamics implements IBonsaiIntegration {
    @Override
    public void registerTrees(ITreeTypeRegistry registry) {
        TreeTypeSimple menrilTree = new TreeTypeSimple("integrateddynamics:menril", "integrateddynamics:menril_sapling", 0);

        menrilTree.addDrop(new ItemStack(Items.STICK, stickAmount), stickChance);

        menrilTree.addDrop("integrateddynamics:menril_log", logAmount, 0, logChance);
        menrilTree.addDrop("integrateddynamics:menril_sapling", saplingAmount, 0, saplingChance);
        menrilTree.addDrop("integrateddynamics:menril_leaves", leafAmount, 0, leafChance);

        menrilTree.addDrop("integrateddynamics:crystalized_menril_chunk", fruitAmount, 0, fruitChance);
        menrilTree.addDrop("integrateddynamics:menril_berries", fruitAmount, 0, fruitChance);

        registry.registerTreeType(this, menrilTree);
    }

    @Override
    public void generateTree(IBonsaiTreeType type, World world, BlockPos pos, Random rand) {

    }
}
