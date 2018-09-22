package org.dave.bonsaitrees.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.server.management.PlayerList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.dave.bonsaitrees.BonsaiTrees;
import org.dave.bonsaitrees.api.IBonsaiSoil;
import org.dave.bonsaitrees.api.IBonsaiTreeType;
import org.dave.bonsaitrees.api.TreeTypeDrop;
import org.dave.bonsaitrees.base.BaseTileTicking;
import org.dave.bonsaitrees.compat.CraftTweaker2.registries.SoilStatsModificationsRegistry;
import org.dave.bonsaitrees.compat.CraftTweaker2.registries.TreeDropModificationsRegistry;
import org.dave.bonsaitrees.init.Triggerss;
import org.dave.bonsaitrees.misc.ConfigurationHandler;
import org.dave.bonsaitrees.render.PotColorizer;
import org.dave.bonsaitrees.trees.TreeGrowthHelper;
import org.dave.bonsaitrees.trees.TreeShape;
import org.dave.bonsaitrees.trees.TreeShapeRegistry;

import java.util.ArrayList;
import java.util.List;

public class TileBonsaiPot extends BaseTileTicking {
    protected ItemStack sapling = ItemStack.EMPTY;
    protected ItemStack soilStack = ItemStack.EMPTY;

    protected HoppingItemStackBufferHandler hoppingItemBuffer = new HoppingItemStackBufferHandler() {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            TileBonsaiPot.this.markDirty();
        }
    };

    protected String shapeFilename = null;
    protected double progress = 0;
    protected IBonsaiTreeType treeType = null;
    protected EnumDyeColor color = PotColorizer.DEFAULT_COLOR;

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        EnumDyeColor before = color;
        super.onDataPacket(net, packet);
        EnumDyeColor after = color;

        if(before != after) {
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        }
    }

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

    public boolean hasSoil() {
        return soilStack != null && !soilStack.isEmpty() && soilStack.getItem() instanceof ItemBlock;
    }

    public ItemStack getSoilStack() {
        return soilStack.copy();
    }

    public IBonsaiSoil getBonsaiSoil() {
        return BonsaiTrees.instance.soilRegistry.getTypeByStack(soilStack);
    }

    public IBlockState getSoilBlockState() {
        if(!hasSoil()) {
            return null;
        }

        ItemBlock soilBlock = (ItemBlock)soilStack.getItem();
        return soilBlock.getBlock().getStateFromMeta(soilStack.getMetadata());
    }

    public boolean isHarvestable() {
        return hasSapling() && progress >= BonsaiTrees.instance.typeRegistry.getFinalGrowTime(getTreeType(), getBonsaiSoil());
    }

    public boolean isGrowing() {
        return hasSapling() && progress < BonsaiTrees.instance.typeRegistry.getFinalGrowTime(getTreeType(), getBonsaiSoil());
    }

    public ItemStack getSapling() {
        return sapling;
    }

    public EnumDyeColor getColor() {
        return color;
    }

    public void setColor(EnumDyeColor color) {
        this.color = color;
        this.markDirty();
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
    }

    public void boostProgress() {
        if(!isGrowing()) {
            return;
        }

        this.progress += BonsaiTrees.instance.typeRegistry.getFinalGrowTime(getTreeType(), getBonsaiSoil()) / 4;
        if(this.progress >= BonsaiTrees.instance.typeRegistry.getFinalGrowTime(getTreeType(), getBonsaiSoil())) {
            this.progress = BonsaiTrees.instance.typeRegistry.getFinalGrowTime(getTreeType(), getBonsaiSoil());
        }

        this.markDirty();
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
    }

    public void dropSapling() {
        if (sapling.isEmpty()) {
            return;
        }

        spawnItem(this.sapling.copy());

        setSapling(ItemStack.EMPTY);
    }

    public void dropSoil() {
        this.spawnItem(this.soilStack.copy());
        this.soilStack = ItemStack.EMPTY;
        this.markDirty();
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
    }


    private void spawnItem(ItemStack stack) {
        EntityItem entityItem = new EntityItem(world, getPos().getX()+0.5f, getPos().getY()+0.7f, getPos().getZ()+0.5f, stack);
        entityItem.lifespan = 1200;
        entityItem.setPickupDelay(5);

        entityItem.motionX = 0.0f;
        entityItem.motionY = 0.10f;
        entityItem.motionZ = 0.0f;

        world.spawnEntity(entityItem);
    }

    public List<ItemStack> getRandomizedDrops() {
        List<ItemStack> result = new ArrayList<>();

        List<TreeTypeDrop> drops = TreeDropModificationsRegistry.getModifiedDropList(treeType);
        for(TreeTypeDrop drop : drops) {
            int tries = drop.stack.getCount();

            float dropChance = drop.chance * SoilStatsModificationsRegistry.getModifiedDropChanceModifier(this.getBonsaiSoil());
            int count = 0;
            for (int tryNum = 0; tryNum < tries; tryNum++) {
                if (world.rand.nextFloat() >= dropChance) {
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

        if(compound.hasKey("soil")) {
            soilStack = new ItemStack(compound.getCompoundTag("soil"));
        } else {
            soilStack = new ItemStack(Blocks.DIRT, 1, 0);
        }

        if(treeType == null || sapling.isEmpty()) {
            sapling = ItemStack.EMPTY;
            shapeFilename = null;
        }

        if(compound.hasKey("color")) {
            color = EnumDyeColor.byMetadata(compound.getInteger("color"));
        } else {
            color = PotColorizer.DEFAULT_COLOR;
        }

        if(compound.hasKey("hoppingBuffer")) {
            hoppingItemBuffer.deserializeNBT((NBTTagCompound) compound.getTag("hoppingBuffer"));
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
        compound.setInteger("color", color.getMetadata());
        compound.setTag("soil", soilStack.writeToNBT(new NBTTagCompound()));

        if(isHoppingPot()) {
            compound.setTag("hoppingBuffer", hoppingItemBuffer.serializeNBT());
        }

        return compound;
    }

    public boolean isHoppingPot() {
        return getBlockMetadata() == 1;
    }

    private void updateHoppingExport() {
        if(world.isRemote) {
            return;
        }

        if(!isHoppingPot()) {
            return;
        }

        if(hoppingItemBuffer.isEmpty()) {
            return;
        }

        if(getWorld().getTileEntity(getPos().down()) == null) {
            return;
        }

        TileEntity below = getWorld().getTileEntity(getPos().down());
        if(!below.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP)) {
            return;
        }

        IItemHandler targetHandler = below.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);

        for(int srcSlot = 0; srcSlot < hoppingItemBuffer.getSlots(); srcSlot++) {
            ItemStack stack = hoppingItemBuffer.getStackInSlot(srcSlot);
            if(stack.isEmpty()) {
                continue;
            }

            ItemStack simResult = ItemHandlerHelper.insertItemStacked(targetHandler, stack, true);
            if(simResult.isEmpty() || simResult.getCount() < stack.getCount()) {
                ItemStack insertResult = ItemHandlerHelper.insertItemStacked(targetHandler, stack, false);
                hoppingItemBuffer.setStackInSlotInternal(srcSlot, insertResult);
            }
        }
    }

    public HoppingItemStackBufferHandler getHoppingItemBuffer() {
        return hoppingItemBuffer;
    }

    private void updateProgress() {
        if(!hasSoil() || !hasSapling()) {
            progress = 0;
            return;
        }

        if(!BonsaiTrees.instance.soilCompatibility.canTreeGrowOnSoil(getTreeType(), getBonsaiSoil())) {
            return;
        }

        progress = TreeGrowthHelper.growTick(getTreeType(), getBonsaiSoil(), getWorld(), getPos(), getWorld().getBlockState(getPos()), progress);
        this.markDirty();
    }

    private void tryToCutAndExport() {
        if(ConfigurationHandler.GeneralSettings.disableHoppingBonsaiPot) {
            return;
        }

        if(!isHoppingPot()) {
            return;
        }

        if(getWorld().isBlockPowered(getPos())) {
            return;
        }

        if(!hoppingItemBuffer.isEmpty()) {
            // TODO: Add cooldown for check
            return;
        }

        List<ItemStack> drops = getRandomizedDrops();
        int slot = 0;
        for(ItemStack drop : drops) {
            hoppingItemBuffer.setStackInSlotInternal(slot, drop);
            slot++;
        }

        progress = 0;
        this.markDirty();
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
    }

    private void checkProgress() {
        if(world.isRemote) {
            return;
        }

        if(!hasSoil()) {
            return;
        }

        if(!hasSapling()) {
            return;
        }

        if(progress < BonsaiTrees.instance.typeRegistry.getFinalGrowTime(getTreeType(), getBonsaiSoil())) {
            return;
        }

        // Tree is fully grown

        // Trigger achievements
        if(hasOwner()) {
            PlayerList list = world.getMinecraftServer().getPlayerList();
            EntityPlayerMP player = list.getPlayerByUUID(getOwner());
            if (player != null) {
                Triggerss.GROW_TREE.trigger(player);
            }
        }

        // Try to cut the tree if its a hopping bonsai pot
        tryToCutAndExport();
    }


    @Override
    public void update() {
        super.update();

        updateHoppingExport();
        updateProgress();
        checkProgress();
    }

    public double getProgressPercent() {
        return getProgress() / (double)BonsaiTrees.instance.typeRegistry.getFinalGrowTime(getTreeType(), getBonsaiSoil());
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

    public void setSoil(ItemStack soilStack) {
        this.soilStack = soilStack;
        this.markDirty();
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing == EnumFacing.DOWN;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing == EnumFacing.DOWN) {
            return (T) hoppingItemBuffer;
        }

        return null;
    }
}
