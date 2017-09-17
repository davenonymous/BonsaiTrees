package org.dave.bonsaitrees.base;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public abstract class BaseBlockWithTile<T extends BaseTile> extends BaseBlock {

    public BaseBlockWithTile(Material material) {
        super(material);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public abstract TileEntity createTileEntity(World world, IBlockState state);

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        //super.getDrops(drops, world, pos, state, fortune);
        T tile = world.getTileEntity(pos) instanceof BaseTile ? (T) world.getTileEntity(pos) : null;
        if(tile != null) {
            NBTTagCompound tileData = tile.writeToNBT(new NBTTagCompound());
            ItemStack result = new ItemStack(this, 1, 0);
            result.setTagCompound(tileData);
            drops.add(result);
        }
    }

    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);

        if (!(world.getTileEntity(pos) instanceof BaseTile)) {
            return;
        }

        BaseTile tile = (BaseTile)world.getTileEntity(pos);
        if(stack.hasTagCompound()) {
            if(stack.hasDisplayName()) {
                tile.setCustomName(stack.getDisplayName());
            }

            if(stack.getTagCompound().hasKey("owner")) {
                tile.setOwner(stack.getTagCompound().getUniqueId("owner"));
            }
        }

        if(!tile.hasOwner() && placer instanceof EntityPlayer) {
            tile.setOwner((EntityPlayer)placer);
        }

        tile.markDirty();
    }
}
