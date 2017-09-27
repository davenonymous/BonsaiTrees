package org.dave.bonsaitrees.integration.mods;

import com.pam.harvestcraft.blocks.FruitRegistry;
import com.pam.harvestcraft.blocks.growables.BlockPamSapling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.dave.bonsaitrees.api.*;

import java.util.Random;

@BonsaiIntegration(mod = "harvestcraft")
public class PamsHarvestcraft implements IBonsaiIntegration {
    @Override
    public void registerTrees(ITreeTypeRegistry registry) {
        for(BlockPamSapling sapling : FruitRegistry.getSaplings()) {
            String name = sapling.getRegistryName().toString().replace("_sapling", "");

            TreeTypeSimple pamTreeType = new TreeTypeSimple(name, new ItemStack(sapling, 1, 0));

            pamTreeType.addDrop(new ItemStack(sapling, BonsaiDropChances.saplingAmount), BonsaiDropChances.saplingChance);
            pamTreeType.addDrop(new ItemStack(Items.STICK, BonsaiDropChances.stickAmount), BonsaiDropChances.stickChance);
            pamTreeType.addDrop(new ItemStack(sapling.getFruitItem(), BonsaiDropChances.fruitAmount), BonsaiDropChances.fruitChance);

            IBlockState logState = ReflectionHelper.getPrivateValue(BlockPamSapling.class, sapling, "logState");
            pamTreeType.addDrop(new ItemStack(logState.getBlock(), BonsaiDropChances.logAmount), BonsaiDropChances.logChance);

            IBlockState leafState = ReflectionHelper.getPrivateValue(BlockPamSapling.class, sapling, "leavesState");
            pamTreeType.addDrop(new ItemStack(leafState.getBlock(), BonsaiDropChances.leafAmount), BonsaiDropChances.leafChance);

            registry.registerTreeType(this, pamTreeType);
        }
    }

    @Override
    public void generateTree(IBonsaiTreeType type, World world, BlockPos pos, Random rand) {
        for(BlockPamSapling sapling : FruitRegistry.getSaplings()) {
            String name = sapling.getRegistryName().toString().replace("_sapling", "");
            if(!name.equals(type.getName())) {
                continue;
            }

            world.setBlockState(pos, sapling.getDefaultState());
            sapling.grow(world, rand, pos, sapling.getDefaultState());
        }
    }
}
