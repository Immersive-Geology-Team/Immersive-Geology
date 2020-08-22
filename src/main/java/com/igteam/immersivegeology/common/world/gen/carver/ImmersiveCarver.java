package com.igteam.immersivegeology.common.world.gen.carver;

import com.igteam.immersivegeology.common.util.IGLogger;
import com.igteam.immersivegeology.common.world.biome.IGBiome;
import com.igteam.immersivegeology.common.world.gen.carver.controller.CaveCarverController;
import com.igteam.immersivegeology.common.world.gen.carver.controller.WaterRegionController;
import com.igteam.immersivegeology.common.world.gen.carver.util.ColPos;
import net.minecraft.block.BlockState;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/*
 * This carver is a modified version of YungsBetterCaves BetterCavesCarver.java
 * I've done my best to adhere to the GNU General Public License Version 3
 */

public class ImmersiveCarver {
    private IWorld world;
    public long seed = 0;

    private CaveCarverController caveCarver;
    private WaterRegionController waterCarver;
    private WorleyCaveCarver worleyCaveCarver;
   // private RavineController ravineController;
    
    public void initialize(IWorld worldIn) {
    	this.world = worldIn;
    	this.seed = worldIn.getSeed();
    	Random seedGenerator = new Random();
    	seedGenerator.setSeed(seed);
    	int dimensionID = worldIn.getDimension().getType().getId();
    	String dimensionName = "";
    	
    	try {
    		
    	} catch (NullPointerException e) {
			IGLogger.info("ERROR, failed to find the name of the dimension with the ID "+dimensionID);
    	}
    	
    	this.caveCarver = new CaveCarverController(worldIn);
        this.waterCarver = new WaterRegionController(worldIn);
        this.worleyCaveCarver = new WorleyCaveCarver(seedGenerator);
    }
    
    public void carve(IChunk chunkIn, int chunkX, int chunkZ) {
        BitSet airCarvingMask = chunkIn.getCarvingMask(GenerationStage.Carving.AIR);
        BitSet liquidCarvingMask = chunkIn.getCarvingMask(GenerationStage.Carving.LIQUID);
        // Determine surface altitudes in this chunk
        int[][] surfaceAltitudes = new int[16][16];
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
				// Don't bother doing unnecessary calculations
				surfaceAltitudes[x][z] = Math.min(
						chunkIn.getTopBlockY(Heightmap.Type.WORLD_SURFACE_WG, x, z),
						chunkIn.getTopBlockY(Heightmap.Type.OCEAN_FLOOR_WG, x, z));
            }
        }
        
        // Determine biomes in this chunk - used for flooded cave checking
        Map<Long, IGBiome> biomeMap = new HashMap<>();
        for (int x = chunkX * 16 - 2; x <= chunkX * 16 + 17; x++) {
            for (int z = chunkZ * 16 - 2; z <= chunkZ * 16 + 17; z++) {
                ColPos pos = new ColPos(x, z);
                if(chunkIn.getBiome(pos.toBlockPos()) instanceof IGBiome) {
                	biomeMap.put(pos.toLong(), (IGBiome) chunkIn.getBiome(pos.toBlockPos()));
                }
            }
        }
        
        // Determine liquid blocks for this chunk
        BlockState[][] liquidBlocks = waterCarver.getLiquidBlocksForChunk(chunkX, chunkZ);
 
        // Carve chunk
        caveCarver.carveChunk(chunkIn, chunkX, chunkZ, surfaceAltitudes, liquidBlocks, biomeMap, airCarvingMask, liquidCarvingMask);
        worleyCaveCarver.carve(chunkIn, chunkX << 4, chunkZ << 4, liquidBlocks, biomeMap, airCarvingMask, liquidCarvingMask);
       // ravineController.carveChunk(chunkIn, chunkX, chunkZ, liquidBlocks, biomeMap, airCarvingMask, liquidCarvingMask);

        // Set carving masks for features to use 
        ((ChunkPrimer) chunkIn).setCarvingMask(GenerationStage.Carving.AIR, airCarvingMask);
        ((ChunkPrimer) chunkIn).setCarvingMask(GenerationStage.Carving.LIQUID, liquidCarvingMask);
    }
    
    public void setWorld(IWorld worldIn) {
        this.world = worldIn;
        this.caveCarver.setWorld(worldIn);
        this.waterCarver.setWorld(worldIn);
        this.worleyCaveCarver.setWorld(worldIn);
       // this.ravineController.setWorld(worldIn);
    }

    public long getSeed() {
        return this.seed;
    }
}
