package org.dave.bonsaitrees.misc;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FloodFill {
    private List<Block> ignoredBlocks = Arrays.asList(new Block[] {
            Blocks.DIRT,
            Blocks.STONE,
            Blocks.COBBLESTONE,
            Blocks.GRASS,
            Blocks.GRASS_PATH,
            Blocks.GRAVEL,
            Blocks.SAND,
            Blocks.SANDSTONE,
            Blocks.WATER
    });
    private int MAX_SEARCH_DEPTH = 2048;
    private int MAX_BLOCKS = 4196;

    private World world;
    private BlockPos startingPosition;
    private Map<BlockPos, IBlockState> result;
    private boolean normalized = true;

    public FloodFill(World world, BlockPos startingPosition) {
        this.world = world;
        this.startingPosition = startingPosition;
    }

    private static Map<BlockPos, IBlockState> normalizeBlockPosMap(Map<BlockPos, IBlockState> input) {
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int minX = Integer.MAX_VALUE;
        for(BlockPos pos : input.keySet()) {
            if(pos.getY() < minY) {
                minY = pos.getY();
            }
            if(pos.getZ() < minZ) {
                minZ = pos.getZ();
            }
            if(pos.getX() < minX) {
                minX = pos.getX();
            }
        }

        Map<BlockPos, IBlockState> result = new HashMap<>();
        for(Map.Entry<BlockPos, IBlockState> blockInfo : input.entrySet()) {
            result.put(blockInfo.getKey().add(-minX, -minY, -minZ), blockInfo.getValue());
        }

        return result;
    }

    public Map<BlockPos, IBlockState> getConnectedBlocks() {
        result = new HashMap<>();
        floodFill(world, startingPosition, 0);
        if(normalized) {
            return normalizeBlockPosMap(result);
        }

        return result;
    }

    private void floodFill(World world, BlockPos pos, int depth) {
        if(depth > MAX_SEARCH_DEPTH) {
            return;
        }

        if(result.size() > MAX_BLOCKS) {
            return;
        }

        if(result.containsKey(pos)) {
            return;
        }

        IBlockState state = world.getBlockState(pos);
        if(state == null || state.getBlock() == null || state.getBlock().isAir(state, world, pos)) {
            return;
        }

        if(ignoredBlocks.contains(state.getBlock())) {
            return;
        }

        if(state.getBlock().getRegistryName().toString().equals("tconstruct:slime_grass")) {
            return;
        }

        result.put(pos, state);

        // The 6 main directions
        for(EnumFacing direction : EnumFacing.values()) {
            floodFill(world, pos.offset(direction), depth++);
        }

        // The 8 diagonals
        floodFill(world, pos.add(1, 1, 1), depth+1);
        floodFill(world, pos.add(-1, 1, -1), depth+1);
        floodFill(world, pos.add(1, 1, -1), depth+1);
        floodFill(world, pos.add(-1, 1, 1), depth+1);
        floodFill(world, pos.add(1, -1, 1), depth+1);
        floodFill(world, pos.add(-1, -1, -1), depth+1);
        floodFill(world, pos.add(1, -1, -1), depth+1);
        floodFill(world, pos.add(-1, -1, 1), depth+1);
    }
}
