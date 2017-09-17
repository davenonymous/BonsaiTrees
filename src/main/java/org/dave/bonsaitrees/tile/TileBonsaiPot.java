package org.dave.bonsaitrees.tile;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.dave.bonsaitrees.base.BaseTileTicking;
import org.dave.bonsaitrees.trees.*;
import org.dave.bonsaitrees.utility.Logz;

import java.util.Random;

public class TileBonsaiPot extends BaseTileTicking {
    protected ItemStack sapling = ItemStack.EMPTY;
    protected String bonsaiShape = null;
    protected int progress = 0;
    protected TreeType treeType = null;

    public boolean hasSapling() {
        return sapling != ItemStack.EMPTY && treeType != null && bonsaiShape != null;
    }

    public boolean isHarvestable() {
        return hasSapling() && progress >= treeType.getGrowTime();
    }

    public boolean isGrowing() {
        return hasSapling() && progress < treeType.getGrowTime();
    }

    public ItemStack getSapling() {
        return sapling;
    }

    public void boostProgress() {
        if(!isGrowing()) {
            return;
        }

        this.progress += treeType.getGrowTime() / 4;
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

    public int dropLoot() {
        Random rand = new Random();
        int totalDrops = 0;
        for(TreeTypeDrop drop : treeType.getDrops()) {
            int tries = drop.stack.getCount();
            float chance = (float)drop.chance / 100.0f;

            int count = 0;
            for(int tryNum = 0; tryNum < tries; tryNum++) {
                if(rand.nextFloat() >= chance) {
                    continue;
                }
                count++;
            }

            if(count == 0) {
                continue;
            }

            if(count > drop.stack.getMaxStackSize()) {
                count = drop.stack.getMaxStackSize();
            }

            ItemStack dropCopy = drop.stack.copy();
            dropCopy.setCount(count);

            spawnItem(dropCopy);
            totalDrops += count;
        }

        return totalDrops;
    }

    public String getBonsaiShapeName() {
        return bonsaiShape;
    }

    public TreeShape getBonsaiShape() {
        return TreeShapeRegistry.treeShapes.get(bonsaiShape);
    }

    public TreeType getTreeType() {
        return treeType;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if(compound.hasKey("bonsaiShape")) {
            bonsaiShape = compound.getString("bonsaiShape");
        } else {
            bonsaiShape = null;
        }

        if(compound.hasKey("sapling")) {
            sapling = new ItemStack(compound.getCompoundTag("sapling"));
            treeType = TreeTypeRegistry.getTypeForStack(sapling);
        } else {
            sapling = ItemStack.EMPTY;
            treeType = null;
        }

        progress = compound.getInteger("progress");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        if(bonsaiShape != null) {
            compound.setString("bonsaiShape", bonsaiShape);
        }
        if(sapling != ItemStack.EMPTY) {
            compound.setTag("sapling", sapling.writeToNBT(new NBTTagCompound()));
        }
        compound.setInteger("progress", progress);

        return compound;
    }

    @Override
    public void update() {
        super.update();

        if(!sapling.isEmpty() && progress < treeType.getGrowTime()) {
            progress++;
            this.markDirty();
        }
    }

    public int getProgress() {
        return progress;
    }

    public void setSapling(ItemStack sapling) {
        this.sapling = sapling;
        if(sapling.isEmpty()) {
            treeType = null;
            bonsaiShape = null;
        } else {
            treeType = TreeTypeRegistry.getTypeForStack(sapling);
            bonsaiShape = TreeShapeRegistry.getRandomShapeForStack(sapling);
        }

        progress = 0;
        this.markDirty();
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        //Logz.info("Shape: %s", getBonsaiShape().getBlocks());
    }
}
