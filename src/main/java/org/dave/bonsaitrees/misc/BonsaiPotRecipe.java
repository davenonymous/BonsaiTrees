/*
package org.dave.bonsaitrees.misc;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.dave.bonsaitrees.BonsaiTrees;
import org.dave.bonsaitrees.init.Blockss;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class BonsaiPotRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
    public static final int[] emptySlots = new int[] {0, 1, 2};
    public static final int[] potSlots = new int[] {3, 5, 6, 7, 8};
    public static final int soilSlot = 4;

    public static final List<Block> validPotBlocks = Arrays.asList(Blocks.HARDENED_CLAY, Blocks.CLAY, Blocks.STAINED_HARDENED_CLAY);

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        if(Arrays.stream(emptySlots).anyMatch(slot -> !inv.getStackInSlot(slot).isEmpty())) {
            return false;
        }

        Block type = null;
        for(int potSlot : potSlots) {
            ItemStack invStack = inv.getStackInSlot(potSlot);
            if(invStack.isEmpty()) {
                return false;
            }

            Item invItem = invStack.getItem();
            if(!(invItem instanceof ItemBlock)) {
                return false;
            }

            ItemBlock invItemBlock = (ItemBlock)invItem;
            if(validPotBlocks.stream().noneMatch(valid -> invItemBlock.getBlock() == valid)) {
                return false;
            }

            if(type == null) {
                type = invItemBlock.getBlock();
            }

            if(type != invItemBlock.getBlock()) {
                return false;
            }
        }

        ItemStack soilStack = inv.getStackInSlot(soilSlot);
        if(soilStack.isEmpty() || BonsaiTrees.instance.soilCompatibility.isValidSoil(soilStack)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width == 3 && height == 3;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return new ItemStack(Blockss.bonsaiPot, 1, 0);
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
        ItemStack result = new ItemStack(Blockss.bonsaiPot, 1, 0);

        NBTTagCompound tag = new NBTTagCompound();

        ItemStack soilStack = inv.getStackInSlot(soilSlot);
        if(!soilStack.isEmpty()) {
            tag.setTag("soil", soilStack.writeToNBT(new NBTTagCompound()));
        }

        result.setTagCompound(tag);
        return result;
    }
}
*/