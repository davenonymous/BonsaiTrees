package org.dave.bonsaitrees.trees;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.dave.bonsaitrees.base.BaseTreeType;

public class TreeTypeSimple extends BaseTreeType {
    private ItemStack sapling = ItemStack.EMPTY;

    public TreeTypeSimple(String typeName, String blockName, int meta) {
        super(typeName);
        Block saplingBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockName));
        this.sapling = new ItemStack(saplingBlock, 1, meta);
    }

    public TreeTypeSimple(String typeName, ItemStack sapling) {
        super(typeName);

        this.sapling = sapling;
    }

    @Override
    public double growRate(World world, BlockPos pos, IBlockState state, double progress) {
        return 1.0d;
    }

    @Override
    public boolean worksWith(ItemStack stack) {
        boolean sameItem = sapling.getItem() == stack.getItem();
        boolean sameMeta = sapling.getMetadata() == stack.getMetadata();
        //boolean sameNbt = ItemStack.areItemStackTagsEqual(type.sapling, stack);

        if(sameItem && sameMeta) {
            return true;
        }

        return false;
    }

    @Override
    public ItemStack getExampleStack() {
        return this.sapling.copy();
    }
}
