package org.dave.bonsaitrees.tile;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.management.PlayerList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.EmptyHandler;

import org.dave.bonsaitrees.BonsaiTrees;
import org.dave.bonsaitrees.api.IBonsaiTreeType;
import org.dave.bonsaitrees.api.TreeTypeDrop;
import org.dave.bonsaitrees.base.BaseTileTicking;
import org.dave.bonsaitrees.init.Triggerss;
import org.dave.bonsaitrees.misc.ConfigurationHandler;
import org.dave.bonsaitrees.trees.TreeDropModificationsRegistry;
import org.dave.bonsaitrees.trees.TreeShape;
import org.dave.bonsaitrees.trees.TreeShapeRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TileBonsaiPot extends BaseTileTicking {
    protected ItemStack sapling = ItemStack.EMPTY;
    protected String shapeFilename = null;
    protected double progress = 0;
    protected IBonsaiTreeType treeType = null;
    private final static EmptyHandler handler = new EmptyHandler();

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(this.getPos()).grow(1.0d).expand(0.0d, 1.0d, 0.0d);
    }

    @Override
    public boolean retainNbtData() {
        return false;
    }

    public boolean hasSapling() {
        return sapling != ItemStack.EMPTY && getTreeType() != null;
    }

    public boolean isHarvestable() {
        return hasSapling() && progress >= BonsaiTrees.instance.typeRegistry.getGrowTime(getTreeType());
    }

    public boolean isGrowing() {
        return hasSapling() && progress < BonsaiTrees.instance.typeRegistry.getGrowTime(getTreeType());
    }

    public ItemStack getSapling() {
        return sapling;
    }

    public void boostProgress() {
        if(!isGrowing()) {
            return;
        }

        this.progress += BonsaiTrees.instance.typeRegistry.getGrowTime(getTreeType()) / 4;
        this.markDirty();
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
    }

    public void dropSapling() {
        if(sapling == ItemStack.EMPTY) {
            return;
        }

        spawnItem(sapling);

        setSapling(ItemStack.EMPTY);
    }

    private void spawnItem(ItemStack stack) {
        EntityItem entityItem = new EntityItem(world, getPos().getX()+0.5f, getPos().getY(), getPos().getZ()+0.5f, stack);
        entityItem.lifespan = 600;
        entityItem.setPickupDelay(5);

        entityItem.motionX = 0.0f;
        entityItem.motionY = 0.10f;
        entityItem.motionZ = 0.0f;

        world.spawnEntity(entityItem);
    }

    private List<ItemStack> getRandomizedDrops() {
        List<ItemStack> result = new ArrayList<>();
        List<TreeTypeDrop> drops = TreeDropModificationsRegistry.getModifiedDropList(treeType);
        for(TreeTypeDrop drop : drops) {
            int tries = drop.stack.getCount();

            int count = 0;
            for (int tryNum = 0; tryNum < tries; tryNum++) {
                if (world.rand.nextFloat() >= drop.chance) {
                    continue;
                }
                count++;
            }

            if (count == 0) {
                continue;
            }

            if (count > drop.stack.getMaxStackSize()) {
                count = drop.stack.getMaxStackSize();
            }

            ItemStack dropCopy = drop.stack.copy();
            dropCopy.setCount(count);
            result.add(dropCopy);
        }

        return result;
    }

    public int dropLoot() {
        List<ItemStack> drops = getRandomizedDrops();
        drops.forEach(this::spawnItem);

        return drops.size();
    }

    public String getBonsaiShapeName() {
        return shapeFilename;
    }

    public TreeShape getShapeFilename() {
        return TreeShapeRegistry.getTreeShapeByFilename(shapeFilename);
    }

    public IBonsaiTreeType getTreeType() {
        return treeType;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if(compound.hasKey("shapeFilename")) {
            shapeFilename = compound.getString("shapeFilename");
        } else {
            shapeFilename = null;
        }

        if(compound.hasKey("sapling")) {
            sapling = new ItemStack(compound.getCompoundTag("sapling"));
            treeType = BonsaiTrees.instance.typeRegistry.getTypeByStack(sapling);
        } else {
            sapling = ItemStack.EMPTY;
            treeType = null;
        }

        if(treeType == null || sapling.isEmpty()) {
            sapling = ItemStack.EMPTY;
            shapeFilename = null;
        }

        progress = compound.getDouble("progress");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        if(shapeFilename != null) {
            compound.setString("shapeFilename", shapeFilename);
        }
        if(sapling != ItemStack.EMPTY) {
            compound.setTag("sapling", sapling.writeToNBT(new NBTTagCompound()));
        }
        compound.setDouble("progress", progress);

        return compound;
    }

    @Override
    public void update() {
        super.update();

        if(!sapling.isEmpty() && getTreeType() != null) {
            progress = BonsaiTrees.instance.typeRegistry.growTick(getTreeType(), getWorld(), getPos(), getWorld().getBlockState(getPos()), progress);
        }

        if(!world.isRemote && treeType != null && progress >= BonsaiTrees.instance.typeRegistry.getGrowTime(getTreeType())) {
            if(hasOwner()) {
                PlayerList list = world.getMinecraftServer().getPlayerList();
                EntityPlayerMP player = list.getPlayerByUUID(getOwner());
                if (player != null) {
                    Triggerss.GROW_TREE.trigger(player);
                }
            }

            boolean isHoppingPot = getBlockMetadata() == 1;
            boolean hoppingIsEnabled = !ConfigurationHandler.GeneralSettings.disableHoppingBonsaiPot;
            boolean hasTileEntityBelow = getWorld().getTileEntity(getPos().down()) != null;
            boolean hasNoRedstoneSignal = !getWorld().isBlockPowered(getPos());

            if(isHoppingPot && hoppingIsEnabled && hasTileEntityBelow && hasNoRedstoneSignal) {
                TileEntity below = getWorld().getTileEntity(getPos().down());
                if(below.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP)) {
                    IItemHandler itemHandler = below.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
                    List<ItemStack> drops = getRandomizedDrops();
                    boolean canDrop = true;
                    for(ItemStack drop : drops) {
                        if(!ItemHandlerHelper.insertItemStacked(itemHandler, drop, true).isEmpty()) {
                            canDrop = false;
                            break;
                        }
                    }

                    if(canDrop) {
                        for(ItemStack drop : drops) {
                            ItemHandlerHelper.insertItemStacked(itemHandler, drop, false);
                        }

                        progress = 0;
                        this.markDirty();
                        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
                    }
                }
            }
        }
    }

    public double getProgressPercent() {
        return getProgress() / (double)BonsaiTrees.instance.typeRegistry.getGrowTime(getTreeType());
    }

    public double getProgress() {
        return progress;
    }

    public void setSapling(ItemStack sapling) {
        this.sapling = sapling;
        if(sapling.isEmpty()) {
            treeType = null;
            shapeFilename = null;
        } else {
            treeType = BonsaiTrees.instance.typeRegistry.getTypeByStack(sapling);
            TreeShape shape = TreeShapeRegistry.getRandomShapeForStack(sapling);
            if(shape != null) {
                shapeFilename = shape.getFileName();
            } else {
                shapeFilename = null;
            }
        }

        progress = 0;
        this.markDirty();
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
    }
    
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		boolean result = capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing == EnumFacing.DOWN;
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
    	return (T)handler;
    }
}
