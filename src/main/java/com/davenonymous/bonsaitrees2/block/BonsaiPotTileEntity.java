package com.davenonymous.bonsaitrees2.block;

import com.davenonymous.bonsaitrees2.registry.sapling.SaplingHelper;
import com.davenonymous.bonsaitrees2.registry.sapling.SaplingInfo;
import com.davenonymous.bonsaitrees2.registry.soil.SoilHelper;
import com.davenonymous.bonsaitrees2.registry.soil.SoilInfo;
import com.davenonymous.libnonymous.base.BaseTileEntity;
import com.davenonymous.libnonymous.serialization.Store;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

import java.util.List;

public class BonsaiPotTileEntity extends BaseTileEntity {
    @Store(sendInUpdatePackage = true)
    protected ItemStack soilStack = ItemStack.EMPTY;

    @Store(sendInUpdatePackage = true)
    protected ItemStack saplingStack = ItemStack.EMPTY;

    @Store(sendInUpdatePackage = true)
    protected int modelRotation = -1;

    @Store(sendInUpdatePackage = true)
    protected int growTicks;

    @Store(sendInUpdatePackage = true)
    protected int requiredTicks;

    @Store(sendInUpdatePackage = true)
    protected ResourceLocation treeId;


    public SaplingInfo saplingInfo;
    public SoilInfo soilInfo;

    public BonsaiPotTileEntity() {
        super(ModObjects.BONSAIPOT_TILE);
    }

    public BonsaiPotTileEntity(TileEntityType type) {
        super(type);
    }


    protected void updateInfoObjects() {
        this.saplingInfo = null;
        if(this.saplingStack != null && !this.saplingStack.isEmpty()) {
            this.saplingInfo = SaplingHelper.getSaplingInfoForItem(world, this.saplingStack);
            this.treeId = this.saplingInfo.getId();
        }

        this.soilInfo = null;
        if(this.soilStack != null && !this.soilStack.isEmpty()) {
            this.soilInfo = SoilHelper.getSoilForItem(world, this.soilStack);
        }

        if(this.soilInfo != null && this.saplingInfo != null) {
            int ticks = this.saplingInfo.getRequiredTicks();
            float soilModifier = this.soilInfo.getTickModifier();

            this.requiredTicks = (int)Math.ceil(ticks * soilModifier);
        } else {
            this.requiredTicks = Integer.MAX_VALUE;
        }
    }




    public boolean hasSapling() {
        return saplingStack != null && !saplingStack.isEmpty();
    }

    public ItemStack getSaplingStack() {
        return saplingStack.copy();
    }

    public ResourceLocation getTreeId() {
        return treeId;
    }

    public void setSapling(ItemStack saplingStack) {
        this.saplingStack = saplingStack.copy();
        this.growTicks = 0;
        this.modelRotation = world.rand.nextInt(4);
        this.updateInfoObjects();
        this.markDirty();
        this.notifyClients();
    }

    public void dropSapling() {
        if(!this.hasSapling()) {
            return;
        }

        this.spawnItem(this.getSaplingStack());
        this.setSapling(ItemStack.EMPTY);
    }




    public boolean hasSoil() {
        return soilStack != null && !soilStack.isEmpty() && soilStack.getItem() instanceof BlockItem;
    }

    public ItemStack getSoilStack() {
        return soilStack.copy();
    }

    public BlockState getSoilBlockState() {
        if(!hasSoil()) {
            return null;
        }

        BlockItem soilBlock = (BlockItem)soilStack.getItem();
        return soilBlock.getBlock().getDefaultState();
    }

    public void setSoil(ItemStack soilStack) {
        this.soilStack = soilStack.copy();
        this.updateInfoObjects();
        this.markDirty();
        this.notifyClients();
    }

    public void dropSoil() {
        if(!this.hasSoil()) {
            return;
        }

        this.spawnItem(this.getSoilStack());
        this.setSoil(ItemStack.EMPTY);
    }


    public boolean isGrowing() {
        return hasSapling() && this.growTicks < this.requiredTicks;
    }

    public double getProgress() {
        if(this.getSaplingStack().isEmpty() || this.getSoilStack().isEmpty() || this.requiredTicks == 0) {
            return 0;
        }

        return (double)this.growTicks / (double)this.requiredTicks;
    }

    public double getProgress(float partialTicks) {
        if(this.getSaplingStack().isEmpty() || this.getSoilStack().isEmpty() || this.requiredTicks == 0) {
            return 0;
        }

        double result = ((double)this.growTicks+partialTicks) / (double)this.requiredTicks;
        if(result >= 0.999) {
            result = 1.0d;
        }
        return result;
    }

    public void updateGrowth() {
        if(this.getSaplingStack().isEmpty() || this.getSoilStack().isEmpty()) {
            this.setGrowTicks(0);
            return;
        }

        if(canGrowIntoBlockAbove()) {
            if(getProgress() < 1.0f) {
                this.setGrowTicks(growTicks+1);
            }
        } else {
            if(getProgress() > 0.3f) {
                this.setGrowTicks((int)Math.ceil(this.requiredTicks * 0.3f));
            }
        }
    }

    private boolean canGrowIntoBlockAbove() {
        if(world == null) {
            return false;
        }

        BlockPos upPos = pos.up();
        if(world.isAirBlock(upPos)) {
            return true;
        }

        BlockState blockState = world.getBlockState(upPos);
        VoxelShape collisionShape = blockState.getCollisionShape(world, upPos);
        if (collisionShape == null || collisionShape.equals(VoxelShapes.empty())) {
            return true;
        }

        return false;
    }

    public void setGrowTicks(int growTicks) {
        this.growTicks = growTicks;
        this.markDirty();
    }

    public void boostProgress() {
        if(!isGrowing()) {
            return;
        }

        this.growTicks += this.requiredTicks / 4;
        if(this.growTicks >= this.requiredTicks) {
            this.growTicks = this.requiredTicks;
        }

        notifyClients();
    }

    @Override
    protected void initialize() {
        super.initialize();

        if(world == null || this.world.isRemote) {
            return;
        }

        if(this.modelRotation == -1) {
            this.modelRotation = this.world.rand.nextInt(4);
        }
    }

    @Override
    public void tick() {
        super.tick();

        this.updateGrowth();
    }

    public void dropLoot() {
        if(this.saplingInfo == null || this.soilInfo == null) {
            this.updateInfoObjects();
        }

        List<ItemStack> drops = this.saplingInfo.getRandomizedDrops(this.world.rand);
        for(ItemStack stack : drops) {
            this.spawnItem(stack);
        }
    }

}
