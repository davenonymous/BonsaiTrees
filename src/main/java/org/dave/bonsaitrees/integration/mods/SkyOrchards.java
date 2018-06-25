package org.dave.bonsaitrees.integration.mods;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.dave.bonsaitrees.api.*;
import org.dave.bonsaitrees.utility.Logz;

import sky_orchards.blocks.EnumWood;

import java.util.Random;

@BonsaiIntegration(mod = "sky_orchards")
public class SkyOrchards implements IBonsaiIntegration {
    @Override
    public void registerTrees(ITreeTypeRegistry registry) {
        for(EnumWood wood : EnumWood.values()) {
            TreeTypeSimple treeType = new TreeTypeSimple("sky_orchard:" + wood.getName(), new ItemStack(wood.getSapling()));

            treeType.addDrop(new ItemStack(wood.getSapling(), 1, 0), BonsaiDropChances.saplingChance);
            treeType.addDrop(new ItemStack(Items.STICK, BonsaiDropChances.stickAmount), BonsaiDropChances.stickChance);
            treeType.addDrop(new ItemStack(wood.getResin(), 1, 0), BonsaiDropChances.fruitChance);
            treeType.addDrop(new ItemStack(Blocks.LOG, 1, 0), BonsaiDropChances.logChance);
            treeType.addDrop(new ItemStack(wood.getLeaves(), 1, 0), BonsaiDropChances.leafChance);
            treeType.addDrop(new ItemStack(wood.getAcorn(), 1, 0), BonsaiDropChances.leafChance);

            if (!wood.getLeafDrop().equalsIgnoreCase("unused") && !wood.getLeafDrop().equalsIgnoreCase("null")) {
                String[] finalEntry = wood.getLeafDrop().trim().split("#");
                if (finalEntry.length != 4) {
                    // This should not be happening since it is already checked by SkyOrchards in the first place.
                    continue;
                }

                ItemStack stack = new ItemStack((Item)Item.REGISTRY.getObject(new ResourceLocation(finalEntry[0])), Integer.valueOf(finalEntry[1]), Integer.valueOf(finalEntry[2]));
                int chanceInv = Integer.valueOf(finalEntry[3]);
                float chance = chanceInv == 0 ? 0.0f : 1.0f / chanceInv;

                treeType.addDrop(stack, chance);
            }

            registry.registerTreeType(this, treeType);
        }
    }

    @Override
    public void generateTree(IBonsaiTreeType type, World world, BlockPos pos, Random rand) {
        if(!(type.getExampleStack().getItem() instanceof ItemBlock)) {
            Logz.warn("Sapling is no ItemBlock! This should not be happening!");
            return;
        }

        Block sapling = ((ItemBlock) type.getExampleStack().getItem()).getBlock();
        if(!(sapling instanceof BlockSapling)) {
            Logz.info("Not an ore sapling");
            return;
        }

        world.setBlockState(pos, sapling.getDefaultState());

        BlockSapling oreSapling = (BlockSapling)sapling;
        oreSapling.generateTree(world, pos, null, rand);
    }
}
