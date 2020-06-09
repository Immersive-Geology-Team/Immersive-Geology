package com.igteam.immersivegeology.common.world.gen.carver;

import java.util.Arrays;
import java.util.Random;

import com.igteam.immersivegeology.common.world.biome.IGBiome;
import com.igteam.immersivegeology.common.world.biome.IGBiomes;
import com.igteam.immersivegeology.common.world.noise.INoise3D;
import com.igteam.immersivegeology.common.world.noise.NoiseUtil;
import com.igteam.immersivegeology.common.world.noise.OpenSimplexNoise;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

public class WorleyCaveCarver {
	// number of vertical samples to take, noise sampled every 4 blocks, then interpolated
	private static final int SAMPLE_HEIGHT = 28;
	// depth to fill the lower levers with a liquid
	private static int LIQUID_DEPTH_MAX = 11;
	private static int LIQUID_DEPTH_MIN = 0;
	private static final float NOISE_THRESHOLD = 0.4f;
	private static final float HEIGHT_FADE_THRESHOLD = 96;
	
	private static final BlockState LAVA = Blocks.LAVA.getDefaultState();
	private static final BlockState AIR = Blocks.AIR.getDefaultState();
	private static final BlockState WATER = Blocks.WATER.getDefaultState();
	private static final BlockState BEDROCK = Blocks.BEDROCK.getDefaultState();	
	
	//biomes to check!
	private static final IGBiome OCEAN = IGBiomes.OCEAN;
	private static final IGBiome OCEAN_DEEP = IGBiomes.DEEP_OCEAN;
	private static final IGBiome OCEAN_EDGE = IGBiomes.OCEAN_EDGE;
	//private static final IGBiome DEEP_OCEAN_VOLCANIC = IGBiomes.DEEP_OCEAN_VOLCANIC;
	
	// size of the cave intrest points!
	private static final float FEATURE_SIZE = 24;
	private final INoise3D caveNoise;
	
	public WorleyCaveCarver(Random seedGenerator) {
		OpenSimplexNoise caveNoiseWorley = new OpenSimplexNoise(seedGenerator.nextLong());
		
		this.caveNoise = (x, y, z) -> {
            return caveNoiseWorley.eval(x / FEATURE_SIZE, y / FEATURE_SIZE, z / FEATURE_SIZE);
		};
	}
	
	@SuppressWarnings("PointlessArithmeticExpression")
    public void carve(IChunk chunkIn, int chunkX, int chunkZ)
    {
        float[] noiseValues = new float[5 * 5 * SAMPLE_HEIGHT];

        // Sample initial noise values
        for (int x = 0; x < 5; x++)
        {
            for (int z = 0; z < 5; z++)
            {
                for (int y = 0; y < SAMPLE_HEIGHT; y++)
                {
                    noiseValues[x + (z * 5) + (y * 25)] = caveNoise.noise(chunkX + x * 4, y * 7.3f, chunkZ + z * 4);
                }
            }
        }

        float[] section = new float[16 * 16];
        float[] prevSection = null;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        // Create caves, layer by layer
        for (int y = SAMPLE_HEIGHT - 1; y >= 0; y--)
        {
            for (int x = 0; x < 4; x++)
            {
                for (int z = 0; z < 4; z++)
                {
                    float noiseUNW = noiseValues[(x + 0) + ((z + 0) * 5) + ((y + 0) * 25)];
                    float noiseUNE = noiseValues[(x + 1) + ((z + 0) * 5) + ((y + 0) * 25)];
                    float noiseUSW = noiseValues[(x + 0) + ((z + 1) * 5) + ((y + 0) * 25)];
                    float noiseUSE = noiseValues[(x + 1) + ((z + 1) * 5) + ((y + 0) * 25)];

                    float noiseMidN, noiseMidS;

                    // Lerp east-west edges
                    for (int sx = 0; sx < 4; sx++)
                    {
                        // Increasing x -> moving east
                        noiseMidN = NoiseUtil.lerp(noiseUNW, noiseUNE, 0.25f * sx);
                        noiseMidS = NoiseUtil.lerp(noiseUSW, noiseUSE, 0.25f * sx);

                        // Lerp faces
                        for (int sz = 0; sz < 4; sz++)
                        {
                            // Increasing z -> moving south
                            section[(x * 4 + sx) + (z * 4 + sz) * 16] = NoiseUtil.lerp(noiseMidN, noiseMidS, 0.25f * sz);
                        }
                    }
                    BlockState replacementLiquid = WATER;
                    BlockState replacementAir = AIR;
                    
                    if (prevSection != null)
                    {
                        // We aren't on the first section, so we need to interpolate between sections, and assign blocks from the previous section up until this one

                        for (int y0 = 4 - 1; y0 >= 0; y0--)
                        {
                            int yPos = y * 4 + y0;
                            float heightFadeValue = yPos > HEIGHT_FADE_THRESHOLD ? 1 - 0.02f * (yPos - HEIGHT_FADE_THRESHOLD) : 1;

                            // Replacement state for cave interior based on height
                            
                            
                            for (int x0 = x * 4; x0 < (x + 1) * 4; x0++)
                            {
                                for (int z0 = z * 4; z0 < (z + 1) * 4; z0++)
                                {
                                    // set the current position
                                    pos.setPos(chunkX + x0, yPos, chunkZ + z0);
                                    
                                   

                                    replacementAir = AIR;
                                    if(chunkIn.getBiome(pos) == OCEAN) {
                                    	replacementLiquid = WATER;
                                    	LIQUID_DEPTH_MAX = 90;
                                    	replacementAir = WATER;
                                    }
                                    
                                    if(chunkIn.getBiome(pos) == OCEAN_EDGE) {
                                    	replacementLiquid = WATER;
                                    	LIQUID_DEPTH_MAX = 78;
                                    	replacementAir = WATER;
                                    }
                                    
                                    if(chunkIn.getBiome(pos) == OCEAN_DEEP) {
                                    	LIQUID_DEPTH_MAX = 14; // from 0 - 14 lava will fill
                                    	replacementLiquid = LAVA;
                                    	replacementAir = WATER; // from 14 to NOISE_THRESHOLD water will cut
                                    	//when the cave meets open water/air, 
                                    	if(pos.getY() == (LIQUID_DEPTH_MAX)) { // at lava 14 set magma
                                    		replacementLiquid = Blocks.MAGMA_BLOCK.getDefaultState();                        
                                    	}
                                    }
                                    
                                    
                                    BlockState replacementState = (yPos <= LIQUID_DEPTH_MAX) ? (yPos >= LIQUID_DEPTH_MIN ? replacementLiquid : replacementAir) : replacementAir;
                         
                                    float finalNoise = NoiseUtil.lerp(section[x0 + 16 * z0], prevSection[x0 + 16 * z0], 0.25f * y0);
                                    finalNoise *= heightFadeValue;

                                    if (finalNoise > NOISE_THRESHOLD)
                                    {
                                        // Create cave if possible
                                        BlockState originalState = chunkIn.getBlockState(pos);
                                        if (!originalState.isAir(chunkIn, pos) && originalState != BEDROCK && !originalState.getMaterial().isLiquid())
                                        {
                                            chunkIn.setBlockState(pos, replacementState, false);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    // End of x/z iteration
                }
            }
            // End of x/z loop, so move section to previous
            prevSection = Arrays.copyOf(section, section.length);
        }
    }
}
