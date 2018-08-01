package org.dave.bonsaitrees.soils;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import org.dave.bonsaitrees.api.IBonsaiSoil;

import javax.annotation.Nullable;

public class SoilBlockAccess implements IBlockAccess {
    private IBonsaiSoil soil;

    public SoilBlockAccess(IBonsaiSoil soil) {
        this.soil = soil;
    }

    @Nullable
    @Override
    public TileEntity getTileEntity(BlockPos pos) {
        return null;
    }

    @Override
    public int getCombinedLight(BlockPos pos, int lightValue) {
        return 255;
    }

    @Override
    public IBlockState getBlockState(BlockPos pos) {
        if(soil.getSoilStack().getItem() instanceof ItemBlock) {
            return ((ItemBlock) soil.getSoilStack().getItem()).getBlock().getStateFromMeta(soil.getSoilStack().getMetadata());
        }

        return Blocks.AIR.getDefaultState();
    }

    @Override
    public boolean isAirBlock(BlockPos pos) {
        return getBlockState(pos).getBlock() == Blocks.AIR;
    }

    @Override
    public Biome getBiome(BlockPos pos) {
        return Biomes.FOREST;
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
