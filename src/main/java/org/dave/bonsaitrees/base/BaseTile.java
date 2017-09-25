package org.dave.bonsaitrees.base;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.UUID;

public class BaseTile extends TileEntity {
    protected String customName = "";
    protected UUID owner;

    public boolean retainNbtData() {
        return true;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        customName = compound.getString("CustomName");
        owner = compound.getUniqueId("owner");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setString("CustomName", customName);

        if(hasOwner()) {
            compound.setUniqueId("owner", this.owner);
        }

        return compound;
    }

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public UUID getOwner() {
        return owner;
    }

    public String getOwnerName() {
        return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerProfileCache().getProfileByUUID(getOwner()).getName();
    }

    public boolean hasOwner() {
        return owner != null;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public void setOwner(EntityPlayer player) {
        if(player == null) {
            return;
        }

        setOwner(player.getUniqueID());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 1, getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        readFromNBT(packet.getNbtCompound());
    }

    public void dropTile() {
        BaseTile tile = world.getTileEntity(pos) instanceof BaseTile ? (BaseTile) world.getTileEntity(pos) : null;

        if(tile != null) {
            IBlockState state = world.getBlockState(pos);
            ItemStack stack = new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state));

            if(retainNbtData()) {
                NBTTagCompound tileData = tile.writeToNBT(new NBTTagCompound());
                stack.setTagCompound(tileData);
            }

            EntityItem entityItem = new EntityItem(world, getPos().getX()+0.5f, getPos().getY(), getPos().getZ()+0.5f, stack);
            entityItem.lifespan = 600;
            entityItem.setPickupDelay(5);

            entityItem.motionX = 0.0f;
            entityItem.motionY = 0.10f;
            entityItem.motionZ = 0.0f;

            world.spawnEntity(entityItem);
        }
    }
}
