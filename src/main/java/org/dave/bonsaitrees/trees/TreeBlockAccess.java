package org.dave.bonsaitrees.trees;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;

import javax.annotation.Nullable;

public class TreeBlockAccess implements IBlockAccess {
    private TreeShape shape;

    private World bonsaiPotWorld;
    private BlockPos bonsaiPotPosition;

    public TreeBlockAccess(TreeShape shape) {
        this.shape = shape;
    }

    public TreeBlockAccess(TreeShape shape, World bonsaiPotWorld, BlockPos bonsaiPotPosition) {
        this.shape = shape;
        this.bonsaiPotWorld = bonsaiPotWorld;
        this.bonsaiPotPosition = bonsaiPotPosition;
    }

    @Nullable
    @Override
    public TileEntity getTileEntity(BlockPos pos) {
        return null;
    }

    @Override
    public int getCombinedLight(BlockPos pos, int lightValue) {
        return bonsaiPotWorld == null ? 255 : bonsaiPotWorld.getCombinedLight(bonsaiPotPosition, lightValue);
    }

    @Override
    public IBlockState getBlockState(BlockPos pos) {
        if(shape.getStateAtPos(pos) != null) {
            return shape.getStateAtPos(pos);
        }
        return Blocks.AIR.getDefaultState();
    }

    @Override
    public boolean isAirBlock(BlockPos pos) {
        IBlockState blockState = this.getBlockState(pos);
        return blockState.getBlock().isAir(blockState, this, pos);
    }

    @Override
    public Biome getBiome(BlockPos pos) {
        return bonsaiPotWorld == null ? Biomes.FOREST : bonsaiPotWorld.getBiome(bonsaiPotPosition);
    }

    @Override
    public int getStrongPower(BlockPos pos, EnumFacing direction) {
        return 0;
    }

    @Override
    public WorldType getWorldType() {
        return WorldType.FLAT;
    }

    @Override
    public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
        return this.getBlockState(pos).isSideSolid(this, pos, side);
    }
}
