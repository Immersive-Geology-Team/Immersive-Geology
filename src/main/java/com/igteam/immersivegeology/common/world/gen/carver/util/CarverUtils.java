package com.igteam.immersivegeology.common.world.gen.carver.util;

import java.util.BitSet;
import java.util.Random;

import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.api.util.IGRegistryGrabber;
import com.igteam.immersivegeology.common.blocks.IGMaterialBlock;
import com.igteam.immersivegeology.common.blocks.property.IGProperties;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

public class CarverUtils {
    public static int getLocal(int coordinate) {
        return coordinate & 0xF; // This is same as modulo 16, but quicker
    }
    
    private static final BlockState CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();
    private static final BlockState WATER = Blocks.WATER.getDefaultState();
    private static final BlockState SAND = Blocks.SAND.getDefaultState();
    private static final BlockState RED_SAND = Blocks.RED_SAND.getDefaultState();
    private static final BlockState SANDSTONE = Blocks.SANDSTONE.getDefaultState();
    private static final BlockState RED_SANDSTONE = Blocks.RED_SANDSTONE.getDefaultState();

    public static boolean isPosInWorld(ColPos pos, IWorld world) {
        return world.chunkExists(pos.getX() >> 4, pos.getZ() >> 4);
   }
    
    public static void carveBlock(IChunk chunkIn, BlockPos blockPos, BlockState airBlockState, BlockState liquidBlockState, int liquidAltitude, boolean replaceGravel, BitSet carvingMask) {
        // Mark block as processed - for use by features
        int bitIndex = (blockPos.getX() & 0xF) | ((blockPos.getZ() & 0xF) << 4) | (blockPos.getY() << 8);
        carvingMask.set(bitIndex);

        BlockPos blockPosAbove = blockPos.up();
        BlockPos blockPosBelow = blockPos.down();

        Biome biome = chunkIn.getBiome(blockPos);
        BlockState biomeTopBlockState = biome.getSurfaceBuilderConfig().getTop();
        BlockState biomeFillerBlockState = biome.getSurfaceBuilderConfig().getUnder();
        BlockState blockState = chunkIn.getBlockState(blockPos);
        BlockState blockStateAbove = chunkIn.getBlockState(blockPosAbove);
        BlockState blockStateBelow = chunkIn.getBlockState(blockPosBelow);

        // Only continue if the block is replaceable
        if (!canReplaceBlock(blockState, blockStateAbove) && blockState != biomeTopBlockState && blockState != biomeFillerBlockState) {
            return;
        }

        if (airBlockState == CAVE_AIR && blockPos.getY() <= liquidAltitude) { // Replace any block below the liquid altitude with the liquid block passed in
            if (liquidBlockState != null) {
                chunkIn.setBlockState(blockPos, liquidBlockState, false);
            }
        } else {
            // Check for adjacent water blocks to avoid breaking into lakes or oceans
            if (airBlockState == CAVE_AIR && isWaterAdjacent(chunkIn, blockPos)) return;

            // Adjust block below if block removed is biome top block
            if (isTopBlock(chunkIn, blockPos) && canReplaceBlock(blockStateBelow, CAVE_AIR))
                chunkIn.setBlockState(blockPosBelow, biomeTopBlockState, false);

            // If we caused floating sand to form, replace it with sandstone
            if (blockStateAbove == SAND)
                chunkIn.setBlockState(blockPosAbove, SANDSTONE, false);
            else if (blockStateAbove == RED_SAND)
                chunkIn.setBlockState(blockPosAbove, RED_SANDSTONE, false);

            // Replace floating gravel with rock
            if (replaceGravel && blockStateAbove.getBlock() instanceof IGMaterialBlock) {
            	IGMaterialBlock matBlock = (IGMaterialBlock) blockStateAbove.getBlock();
            	if(matBlock.getUseType().equals(MaterialUseType.GRAVEL)) {
            		chunkIn.setBlockState(blockPos.up(), IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, matBlock.getMaterial()).getDefaultState().with(IGProperties.NATURAL, true), false);
            	}
            }
            
            // Replace this block with air, effectively "digging" it out
            chunkIn.setBlockState(blockPos, airBlockState, false);
        }
    }
    
    public static void carveBlock(IChunk chunkIn, BlockPos blockPos, BlockState liquidBlockState, int liquidAltitude, boolean replaceGravel, BitSet carvingMask) {
        carveBlock(chunkIn, blockPos, Blocks.CAVE_AIR.getDefaultState(), liquidBlockState, liquidAltitude, replaceGravel, carvingMask);
    }

    public static void carveBlock(IChunk chunkIn, int x, int y, int z, BlockState liquidBlockState, int liquidAltitude, boolean replaceGravel, BitSet carvingMask) {
        carveBlock(chunkIn, new BlockPos(x, y, z), Blocks.CAVE_AIR.getDefaultState(), liquidBlockState, liquidAltitude, replaceGravel, carvingMask);
    }

    public static void carveBlock(IChunk chunkIn, int x, int y, int z, BlockState airBlockState, BlockState liquidBlockState, int liquidAltitude, boolean replaceGravel, BitSet carvingMask) {
        carveBlock(chunkIn, new BlockPos(x, y, z), airBlockState, liquidBlockState, liquidAltitude, replaceGravel, carvingMask);
    }
    
    /**
     * Counterpart to carveBlock() for flooded caves.
     * Places magma and obsidian randomly 1 block above liquidAltitude.
     *
     * @param chunkIn          the chunk containing the block
     * @param rand             Random used to place magma and obsidian.
     * @param blockPos         The block's position - can be with real (absolute) or chunk-local coordinates
     * @param liquidBlockState the BlockState to use for liquids. May be null if in buffer zone between liquid regions
     * @param liquidAltitude   altitude at and below which air is replaced with liquidBlockState
     * @param carvingMask      BitSet that keeps track of which blocks have already been dug.
     */
    public static void carveFloodedBlock(IChunk chunkIn, Random rand, BlockPos.MutableBlockPos blockPos, BlockState liquidBlockState, int liquidAltitude, boolean replaceGravel, BitSet carvingMask) {
        // Mark block as processed - for use by features
        int bitIndex = (blockPos.getX() & 0xF) | ((blockPos.getZ() & 0xF) << 4) | (blockPos.getY() << 8);
        carvingMask.set(bitIndex);

        // Dig flooded block
        Biome biome = chunkIn.getBiome(blockPos);
        BlockState biomeTopBlockState = biome.getSurfaceBuilderConfig().getTop();
        BlockState biomeFillerBlockState = biome.getSurfaceBuilderConfig().getUnder();
        BlockState blockState = chunkIn.getBlockState(blockPos);
        BlockState blockStateAbove = chunkIn.getBlockState(blockPos.up());
        if (!canReplaceLiquidBlock(blockState, blockStateAbove) && blockState != biomeTopBlockState && blockState != biomeFillerBlockState) {
            return;
        }

        // Add magma and obsidian right above liquid altitude
        if (blockPos.getY() == liquidAltitude + 1) {
            float f = rand.nextFloat();
            if (f < 0.25f) {
                chunkIn.setBlockState(blockPos, Blocks.MAGMA_BLOCK.getDefaultState(), false);
                chunkIn.getBlocksToBeTicked().scheduleTick(blockPos, Blocks.MAGMA_BLOCK, 0);
            } else {
                chunkIn.setBlockState(blockPos, Blocks.OBSIDIAN.getDefaultState(), false);
            }
        }
        // Replace any block below the liquid altitude with the liquid block passed in
        else if (blockPos.getY() <= liquidAltitude) {
            if (liquidBlockState != null) {
                chunkIn.setBlockState(blockPos, liquidBlockState, false);
            }
        }
        // Else, normal carving
        else {
            chunkIn.setBlockState(blockPos, WATER.getBlockState(), false);

            // Replace floating gravel with andesite, if enabled
            if (replaceGravel && blockStateAbove.getBlock() instanceof IGMaterialBlock) {
            	IGMaterialBlock matBlock = (IGMaterialBlock) blockStateAbove.getBlock();
            	if(matBlock.getUseType().equals(MaterialUseType.GRAVEL)) {
            		chunkIn.setBlockState(blockPos.up(), IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, matBlock.getMaterial()).getDefaultState().with(IGProperties.NATURAL, true), false);
            	}
            }
        }
    }
    
    public static void carveFloodedBlock(IChunk chunkIn, Random rand, BlockPos.MutableBlockPos blockPos, BlockState liquidBlockState, int liquidAltitude, BitSet carvingMask) {
        carveFloodedBlock(chunkIn, rand, blockPos, liquidBlockState, liquidAltitude, false, carvingMask);
    }
    
    public static boolean isTopBlock(IChunk chunkIn, BlockPos blockPos) {
        Biome biome = chunkIn.getBiome(blockPos);
        BlockState blockState = chunkIn.getBlockState(blockPos);
        return blockState == biome.getSurfaceBuilderConfig().getTop();
    }
    /**
     * Determines if the Block of a given BlockState is suitable to be replaced during cave generation.
     * Basically returns true for most common worldgen blocks (e.g. stone, dirt, sand), false if the block is air.
     * @param blockState the block's IBlockState
     * @param blockStateAbove the IBlockState of the block above this one
     * @return true if the blockState can be replaced
     */
    public static boolean canReplaceBlock(BlockState blockState, BlockState blockStateAbove) {
        Block block = blockState.getBlock();

        // Avoid damaging trees
        if (blockState.getMaterial() == Material.LEAVES
            || blockState.getMaterial() == Material.WOOD)
            return false;

        // Avoid digging out under trees
        if (blockStateAbove.getMaterial() == Material.WOOD)
            return false;

        // This should hopefully avoid damaging villages
        if (block == Blocks.FARMLAND
            || block == Blocks.GRASS_PATH) {
            return false;
        }

        // Accept stone-like blocks added from other mods
        if (blockState.getMaterial() == Material.ROCK)
            return true;

        //check for our stone types and only accept gravel if water is not directly above it
        if (blockState.getBlock() instanceof IGMaterialBlock) {
            IGMaterialBlock matBlock = (IGMaterialBlock) blockState.getBlock();
            if(matBlock.getUseType().equals(MaterialUseType.GRAVEL) && blockStateAbove.getMaterial() != Material.WATER) {
            	return false;
            } else {
            	return true;
            }
        }
        
        //last catch, check for normal sand! (this should stop Air Caves types from generating under water sources
        return block == Blocks.SAND && blockStateAbove.getMaterial() != Material.WATER;
    }
    
    public static boolean canReplaceLiquidBlock(BlockState blockState, BlockState blockStateAbove) {
        Block block = blockState.getBlock();

        // Avoid damaging trees
        if (blockState.getMaterial() == Material.LEAVES
            || blockState.getMaterial() == Material.WOOD)
            return false;

        // Avoid digging out under trees
        if (blockStateAbove.getMaterial() == Material.WOOD)
            return false;

        // This should hopefully avoid damaging villages
        if (block == Blocks.FARMLAND
            || block == Blocks.GRASS_PATH) {
            return false;
        }

        // Accept stone-like blocks added from other mods
        if (blockState.getMaterial() == Material.ROCK)
            return true;

        // List of carvable blocks
        if (blockState.getBlock() instanceof IGMaterialBlock)
            return true;

        return false;
    }
    
    private static boolean isWaterAdjacent(IChunk chunkIn, BlockPos blockPos) {
        int localX = getLocal(blockPos.getX());
        int localZ = getLocal(blockPos.getZ());
        int y = blockPos.getY();

        return (y < 255 && chunkIn.getBlockState(blockPos.up()).getMaterial() == Material.WATER)
            || localZ > 0 && chunkIn.getBlockState(blockPos.north()).getMaterial() == Material.WATER
            || localX < 15 && chunkIn.getBlockState(blockPos.east()).getMaterial() == Material.WATER
            || localZ < 15 && chunkIn.getBlockState(blockPos.south()).getMaterial() == Material.WATER
            || localX > 0 && chunkIn.getBlockState(blockPos.west()).getMaterial() == Material.WATER;
    }
    
}
