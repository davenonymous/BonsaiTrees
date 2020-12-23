package com.davenonymous.bonsaitrees2.block;

import com.davenonymous.bonsaitrees2.config.Config;
import com.davenonymous.bonsaitrees2.config.WaterLogEffect;
import com.davenonymous.bonsaitrees2.registry.sapling.SaplingRecipeHelper;
import com.davenonymous.bonsaitrees2.registry.sapling.SaplingInfo;
import com.davenonymous.bonsaitrees2.registry.soil.SoilRecipeHelper;
import com.davenonymous.bonsaitrees2.registry.soil.SoilInfo;
import com.davenonymous.bonsaitrees2.setup.Registration;
import com.davenonymous.libnonymous.base.BaseTileEntity;
import com.davenonymous.libnonymous.serialization.Store;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
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
        super(Registration.BONSAIPOT_TILE.get());
    }

    public BonsaiPotTileEntity(TileEntityType type) {
        super(type);
    }


    protected void updateInfoObjects() {
        this.saplingInfo = null;
        if(this.saplingStack != null && !this.saplingStack.isEmpty()) {
            this.saplingInfo = ModObjects.saplingRecipeHelper.getSaplingInfoForItem(world, this.saplingStack);
            if(this.saplingInfo != null) { // This shouldn't happen, but does in case of Immersive Portals?!
                this.treeId = this.saplingInfo.getId();
            }
        }

        this.soilInfo = null;
        if(this.soilStack != null && !this.soilStack.isEmpty()) {
            this.soilInfo = ModObjects.soilRecipeHelper.getSoilForItem(world, this.soilStack);
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
                this.setGrowTicks(growTicks + 1);
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

    private void checkWaterlogged() {
        if(this.isWaterlogged()) {
            WaterLogEffect mode = Config.WATERLOG_EFFECT.get();
            if(mode.shouldDropLoot() && getProgress() >= 1.0f) {
                // Drop loot
                this.dropLoot();
                this.setSapling(this.saplingStack);
            }

            if(mode == WaterLogEffect.DROP_SAPLING){
                // Drop sapling
                this.dropSapling();
            }
        }
    }

    @Override
    public void tick() {
        //TODO cant be accessed
        //super.tick();

        this.checkWaterlogged();
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

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(this.getPos()).grow(1.0d).expand(0.0d, 1.0d, 0.0d);
    }
}
