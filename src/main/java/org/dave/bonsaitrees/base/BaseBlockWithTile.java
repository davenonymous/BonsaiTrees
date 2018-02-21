package org.dave.bonsaitrees.base;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
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
