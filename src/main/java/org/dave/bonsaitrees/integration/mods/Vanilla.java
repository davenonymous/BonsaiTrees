package org.dave.bonsaitrees.integration.mods;

import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.dave.bonsaitrees.api.*;

import java.util.Random;

import static org.dave.bonsaitrees.api.BonsaiDropChances.*;

@BonsaiIntegration()
public class Vanilla implements IBonsaiIntegration {
    @Override
    public void registerTrees(ITreeTypeRegistry registry) {
        for(BlockPlanks.EnumType woodType : BlockPlanks.EnumType.values()) {
            int meta = woodType.getMetadata();
            String type = woodType.getName();

            TreeTypeSimple vanillaType = new TreeTypeSimple("minecraft:" + type, new ItemStack(Blocks.SAPLING, 1, meta));
            vanillaType.addDrop(new ItemStack(Blocks.SAPLING, saplingAmount, meta), saplingChance);
            vanillaType.addDrop(new ItemStack(Items.STICK, stickAmount), stickChance);

            if(type.equals("acacia")) {
                vanillaType.addDrop(new ItemStack(Blocks.LOG2, logAmount, 0), logChance);
                vanillaType.addDrop(new ItemStack(Blocks.LEAVES2, leafAmount, 0), leafChance);
            } else if(type.equals("dark_oak")) {
                vanillaType.addDrop(new ItemStack(Blocks.LOG2, logAmount, 1), logChance);
                vanillaType.addDrop(new ItemStack(Blocks.LEAVES2, leafAmount, 1), leafChance);
            } else {
                vanillaType.addDrop(new ItemStack(Blocks.LOG, logAmount, meta), logChance);
                vanillaType.addDrop(new ItemStack(Blocks.LEAVES, leafAmount, meta), leafChance);
            }

            if(type.equals("oak")) {
                vanillaType.addDrop(new ItemStack(Items.APPLE, fruitAmount, 0), fruitChance);
            }

            if(type.equals("jungle")) {
                vanillaType.addDrop(new ItemStack(Items.DYE, fruitAmount, 3), fruitChance);
            }

            registry.registerTreeType(this, vanillaType);
        }
    }

    @Override
    public void generateTree(IBonsaiTreeType type, World world, BlockPos pos, Random rand) {
        int meta = type.getExampleStack().getMetadata();
        IBlockState saplingState = Blocks.SAPLING.getStateFromMeta(meta);
        if(type.getName().equals("minecraft:dark_oak")) {
            world.setBlockState(pos.west(), saplingState);
            world.setBlockState(pos.south(), saplingState);
            world.setBlockState(pos.west().south(), saplingState);
        }
        world.setBlockState(pos, saplingState);

        BlockSapling saplingBlock = (BlockSapling) Blocks.SAPLING;
        saplingBlock.generateTree(world, pos, saplingState, rand);
    }
}
