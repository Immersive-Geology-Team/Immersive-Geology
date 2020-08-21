package com.igteam.immersivegeology.common.world.gen.carver.controller;

import java.util.Random;

import com.igteam.immersivegeology.common.world.gen.carver.util.ColPos;
import com.igteam.immersivegeology.common.world.noise.FastNoise;
import com.igteam.immersivegeology.common.world.noise.NoiseUtil;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.IWorld;
import net.minecraft.world.dimension.DimensionType;

public class WaterRegionController {
    private FastNoise waterRegionController;
    private IWorld world;
    private int dimensionId;
    private String dimensionName;
    private Random rand;

    // Vars determined from config
    private BlockState lavaBlock;
    private BlockState waterBlock;
    private float waterRegionThreshold;

    // Constants
    private static final float SMOOTH_RANGE = .04f;
    private static final float SMOOTH_DELTA = .01f;
    
    public WaterRegionController(IWorld worldIn) {
        this.world = worldIn;
        this.dimensionId = worldIn.getDimension().getType().getId();
        this.dimensionName = DimensionType.getKey(worldIn.getDimension().getType()).toString();
        this.rand = new Random();

        lavaBlock = Blocks.LAVA.getDefaultState();
        waterBlock = Blocks.WATER.getDefaultState();
        waterRegionThreshold = NoiseUtil.simplexNoiseOffsetByPercent(-1f, 40f / 100f);

        // Water region controller
        float waterRegionSize = .004f;
        waterRegionController = new FastNoise();
        waterRegionController.SetSeed((int)world.getSeed() + 444);
        waterRegionController.SetFrequency(waterRegionSize);
    }
    
    public BlockState[][] getLiquidBlocksForChunk(int chunkX, int chunkZ) {
        rand.setSeed(world.getSeed() ^ chunkX ^ chunkZ);
        BlockState[][] blocks = new BlockState[16][16];
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int realX = chunkX * 16 + x;
                int realZ = chunkZ * 16 + z;
                ColPos pos = new ColPos(realX, realZ);
                blocks[x][z] = getLiquidBlockAtPos(rand, pos);
            }
        }
        return blocks;
    }

    private BlockState getLiquidBlockAtPos(Random rand, ColPos colPos) {
        BlockState liquidBlock = lavaBlock;
        if (waterRegionThreshold > -1f) { // Don't bother calculating noise if water regions are disabled
            float waterRegionNoise = waterRegionController.GetNoise(colPos.getX(), colPos.getZ());

            // If water region threshold check is passed, change liquid block to water
            float randOffset = rand.nextFloat() * SMOOTH_DELTA + SMOOTH_RANGE;
            if (waterRegionNoise < waterRegionThreshold - randOffset)
                liquidBlock = waterBlock;
            else if (waterRegionNoise < waterRegionThreshold + randOffset)
                liquidBlock = null;
        }
        return liquidBlock;
    }

	public void setWorld(IWorld worldIn) {
		this.world = worldIn;
	}
}
