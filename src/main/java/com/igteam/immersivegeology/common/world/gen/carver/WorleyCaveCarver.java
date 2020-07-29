package com.igteam.immersivegeology.common.world.gen.carver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.igteam.immersivegeology.common.world.IGLib;
import com.igteam.immersivegeology.common.world.biome.IGBiome;
import com.igteam.immersivegeology.common.world.biome.IGBiomes;
import com.igteam.immersivegeology.common.world.gen.carver.util.CarverUtils;
import com.igteam.immersivegeology.common.world.gen.carver.util.ColPos;
import com.igteam.immersivegeology.common.world.noise.INoise3D;
import com.igteam.immersivegeology.common.world.noise.NoiseColumn;
import com.igteam.immersivegeology.common.world.noise.NoiseUtil;
import com.igteam.immersivegeology.common.world.noise.OpenSimplexNoise;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.IChunk;

public class WorleyCaveCarver {
	// number of vertical samples to take, noise sampled every 4 blocks, then
	// interpolated
	private static final int SAMPLE_HEIGHT = 26;
	// depth to fill the lower levers with a liquid
	private static int LIQUID_DEPTH_MAX = 10;
	private static int LIQUID_DEPTH_MIN = 0;
	private static final float NOISE_THRESHOLD = 0.4f;
	private static float HEIGHT_FADE_THRESHOLD = 72;

	private static final BlockState LAVA = Blocks.LAVA.getDefaultState();
	private static final BlockState AIR = Blocks.CAVE_AIR.getDefaultState();
	private static final BlockState WATER = Blocks.WATER.getDefaultState();
	private static final BlockState BEDROCK = Blocks.BEDROCK.getDefaultState();

	// biomes to check!
	private static final IGBiome OCEAN = IGBiomes.OCEAN;
	private static final IGBiome FLOODED_MOUNTAINS = IGBiomes.FLOODED_MOUNTAINS;
	private static final IGBiome OCEAN_DEEP = IGBiomes.DEEP_OCEAN;
	private static final IGBiome OCEAN_EDGE = IGBiomes.OCEAN_EDGE;
	private static final IGBiome OCEAN_DEEP_VOLCANIC = IGBiomes.DEEP_OCEAN_VOLCANIC;

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
	public void carve(IChunk chunkIn, int chunkX, int chunkZ, BlockState[][] liquidBlocks, BitSet bitmask) {
		float[] noiseValues = new float[5 * 5 * SAMPLE_HEIGHT];

		// Sample initial noise values
		for (int x = 0; x < 5; x++) {
			for (int z = 0; z < 5; z++) {
				for (int y = 0; y < SAMPLE_HEIGHT; y++) {
					noiseValues[x + (z * 5) + (y * 25)] = caveNoise.noise(chunkX + x * 4, y * 7.3f, chunkZ + z * 4);
				}
			}
		}

		float[] section = new float[16 * 16];
		float[] prevSection = null;
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

		// Create caves, layer by layer
		for (int y = SAMPLE_HEIGHT - 1; y >= 0; y--) {
			for (int x = 0; x < 4; x++) {
				for (int z = 0; z < 4; z++) {
					float noiseUNW = noiseValues[(x + 0) + ((z + 0) * 5) + ((y + 0) * 25)];
					float noiseUNE = noiseValues[(x + 1) + ((z + 0) * 5) + ((y + 0) * 25)];
					float noiseUSW = noiseValues[(x + 0) + ((z + 1) * 5) + ((y + 0) * 25)];
					float noiseUSE = noiseValues[(x + 1) + ((z + 1) * 5) + ((y + 0) * 25)];

					float noiseMidN, noiseMidS;
					int startX = x * IGLib.SUB_CHUNK_SIZE;
					int startZ = z * IGLib.SUB_CHUNK_SIZE;
					
	                // Lerp east-west edges
					for (int sx = 0; sx < 4; sx++) {
						// Increasing x -> moving east
						noiseMidN = NoiseUtil.lerp(noiseUNW, noiseUNE, 0.25f * sx);
						noiseMidS = NoiseUtil.lerp(noiseUSW, noiseUSE, 0.25f * sx);

						// Lerp faces
						for (int sz = 0; sz < 4; sz++) {
							// Increasing z -> moving south
							section[(x * 4 + sx) + (z * 4 + sz) * 16] = NoiseUtil.lerp(noiseMidN, noiseMidS,
									0.25f * sz);
						}
					}
					BlockState replacementAir = AIR;

					if (prevSection != null) {
						// We aren't on the first section, so we need to interpolate between sections,
						// and assign blocks from the previous section up until this one

						for (int y0 = 4 - 1; y0 >= 0; y0--) {
							int yPos = y * 4 + y0;
							float heightFadeValue = yPos > HEIGHT_FADE_THRESHOLD
									? 1 - 0.02f * (yPos - HEIGHT_FADE_THRESHOLD)
									: 1;

							// Replacement state for cave interior based on height
							for (int x0 = x * 4; x0 < (x + 1) * 4; x0++) {
								for (int z0 = z * 4; z0 < (z + 1) * 4; z0++) {
									// set the current position
									pos.setPos(chunkX + x0, yPos, chunkZ + z0);
									int localX = startX + (x0 / 4);
			                        int localZ = startZ + (z0 / 4);
			                        BlockState liquidBlock = liquidBlocks[localX][localZ];
			                        
									HEIGHT_FADE_THRESHOLD = 72;

									float finalNoise = NoiseUtil.lerp(section[x0 + 16 * z0], prevSection[x0 + 16 * z0],
											0.25f * y0);
									finalNoise *= heightFadeValue;

									if (finalNoise > NOISE_THRESHOLD) {
										// Create cave if possible
										BlockState originalState = chunkIn.getBlockState(pos);
										if (CarverUtils.canReplaceBlock(originalState,chunkIn.getBlockState(pos.up())) && originalState.getBlock() != Blocks.BEDROCK) {
											
												CarverUtils.carveBlock(chunkIn, pos, liquidBlock, 15, true, bitmask);
											
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
	
	private IWorld world;
	
	public void setWorld(IWorld worldIn) {
        this.world = worldIn;
    }
	
	public void carveChunk(IChunk chunk, int chunkX, int chunkZ, int[][] surfaceAltitudes, BlockState[][] liquidBlocks, Map<Long, IGBiome> biomeMap, BitSet airCarvingMask, BitSet liquidCarvingMask) {
		boolean flooded = false;
        float smoothAmpFloodFactor = 1;
        
        for (int subX = 0; subX < 16 / IGLib.SUB_CHUNK_SIZE; subX++) {
            for (int subZ = 0; subZ < 16 / IGLib.SUB_CHUNK_SIZE; subZ++) {
                int startX = subX * IGLib.SUB_CHUNK_SIZE;
                int startZ = subZ * IGLib.SUB_CHUNK_SIZE;
                int endX = startX + IGLib.SUB_CHUNK_SIZE - 1;
                int endZ = startZ + IGLib.SUB_CHUNK_SIZE - 1;

                for (int y = 80; y >= 8; y--) {
                for (int offsetX = 0; offsetX < IGLib.SUB_CHUNK_SIZE; offsetX++) {
                    for (int offsetZ = 0; offsetZ < IGLib.SUB_CHUNK_SIZE; offsetZ++) {
                        int localX = startX + offsetX;
                        int localZ = startZ + offsetZ;
                        ColPos colPos = new ColPos(chunkX * 16 + localX, chunkZ * 16 + localZ);
                        
                        flooded = biomeMap.get(colPos.toLong()).getCategory() == IGBiome.Category.OCEAN;
                        smoothAmpFloodFactor = CarverUtils.getDistFactor(world, biomeMap, colPos, 2, flooded ? CarverUtils.isNotOcean : CarverUtils.isOcean);
                        if (smoothAmpFloodFactor <= .25) { // Wall between flooded and normal caves.
                            continue; // Continue to prevent unnecessary noise calculation 
                        }

                        BlockState liquidBlock = liquidBlocks[localX][localZ];
                        
                        float worleyRegionNoise = caveNoise.noise(colPos.getX(), y, colPos.getZ());
                        	CarverNoiseRange range = new CarverNoiseRange(0,1, null);
                        	float smoothAmp = range.getSmoothAmp(worleyRegionNoise) * smoothAmpFloodFactor;

                        	BitSet carvingMask = flooded ? liquidCarvingMask : airCarvingMask;

                            if (localX < 0 || localX > 15)
                                return;
                            if (localZ < 0 || localZ > 15)
                                return;
                           
                            int topTransitionBoundary = 68 - 6;

                            // Set altitude at which caverns start closing off on the bottom
                            int bottomTransitionBoundary = 8 + 3;
                            bottomTransitionBoundary = 8 < 10 ? 18 : 8 + 7;
                            topTransitionBoundary = Math.max(topTransitionBoundary, 1);
                            bottomTransitionBoundary = Math.min(bottomTransitionBoundary, 255);
                            
                                if (y <= 10 && liquidBlock == null)
                                	break;
                                
                                List<Double> noiseBlock;
                                boolean digBlock = false;

                                // Compute a single noise value to represent all the noise values in the NoiseTuple
                                float noise = 1;
                                noiseBlock = new NoiseColumn().get(y).getNoiseValues();
                                for (double n : noiseBlock)
                                    noise *= n;

                                // Adjust threshold if we're in the transition range to provide smoother transition into ceiling
                                float noiseThreshold = 0.6f;
                                if (y >= topTransitionBoundary)
                                    noiseThreshold *= (float) (y - 70) / (topTransitionBoundary - 70);

                                // Close off caverns at the bottom to hide bedrock and give some walkable area
                                if (y < bottomTransitionBoundary)
                                    noiseThreshold *= (float) (y - 8) / (bottomTransitionBoundary - 8);

                                // Adjust threshold along region borders to create smooth transition
                                if (smoothAmp < 1)
                                    noiseThreshold *= smoothAmp;

                                // Mark block for removal if the noise passes the threshold check
                                if (noise < noiseThreshold) {
	                                BlockPos blockPos = new BlockPos(localX, y, localZ);
	                              
	                                if (flooded) {
	                                    CarverUtils.carveFloodedBlock(chunk, new Random(), new BlockPos.MutableBlockPos(blockPos), liquidBlock, 10, true, carvingMask);
	                                } else {
	                                    CarverUtils.carveBlock(chunk, blockPos, liquidBlock, 10, true, carvingMask);
	                                }
                                }
                                
                            
                        }
                    }
                }
            }
        }
	}
	//
	
}
