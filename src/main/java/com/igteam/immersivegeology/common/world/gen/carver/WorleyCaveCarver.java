package com.igteam.immersivegeology.common.world.gen.carver;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Map;
import java.util.Random;

import com.igteam.immersivegeology.common.world.IGLib;
import com.igteam.immersivegeology.common.world.biome.IGBiome;
import com.igteam.immersivegeology.common.world.gen.carver.util.CarverUtils;
import com.igteam.immersivegeology.common.world.gen.carver.util.ColPos;
import com.igteam.immersivegeology.common.world.noise.INoise3D;
import com.igteam.immersivegeology.common.world.noise.NoiseUtil;
import com.igteam.immersivegeology.common.world.noise.OpenSimplexNoise;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

public class WorleyCaveCarver {
	// number of vertical samples to take, noise sampled every 4 blocks, then
	// interpolated
	private static final int SAMPLE_HEIGHT = 26;

	private static final float NOISE_THRESHOLD = 0.4f;
	public static float HEIGHT_FADE_THRESHOLD = 72;

	// size of the cave intrest points!
	private static final float FEATURE_SIZE = 24;
	private final INoise3D caveNoise;

	public WorleyCaveCarver(Random seedGenerator) {
		OpenSimplexNoise caveNoiseWorley = new OpenSimplexNoise(seedGenerator.nextLong());

		this.caveNoise = (x, y, z) -> {
			return caveNoiseWorley.eval(x / FEATURE_SIZE, y / FEATURE_SIZE, z / FEATURE_SIZE);
		};
	}

	public void carve(IChunk chunkIn, int chunkX, int chunkZ, BlockState[][] liquidBlocks, Map<Long, IGBiome> biomeMap, BitSet airBitmask, BitSet liquidBitmask) {
		float[] noiseValues = new float[5 * 5 * SAMPLE_HEIGHT];
		
		// Sample initial noise values
		for (int x = 0; x < 5; x++) {
			for (int z = 0; z < 5; z++) {
				for (int y = 0; y < SAMPLE_HEIGHT; y++) {
					noiseValues[x + (z * 5) + (y * 25)] = caveNoise.noise(chunkX + x * 4, y * 7.3f, chunkZ + z * 4);
				}
			}
		}
		
		ColPos.MutableColPos mutablePos = new ColPos.MutableColPos();
		
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
			                        
			                        ColPos colPos = new ColPos(pos.getX(), pos.getZ());
			                        boolean flooded = biomeMap.get(colPos.toLong()).getCategory() == Biome.Category.OCEAN;
			                        
			                        if (flooded) {
			                        	//TODO Error found with the isPosInWorld, suspect the colPos
			                        	boolean east  = true;//isPosInWorld(mutablePos.setPos(colPos).move(Direction.EAST), chunkIn.getWorldForge());
			                        	boolean west  = true;//isPosInWorld(mutablePos.setPos(colPos).move(Direction.WEST), world);
			                        	boolean north = true; //isPosInWorld(mutablePos.setPos(colPos).move(Direction.NORTH), world);
			                        	boolean south = true; //isPosInWorld(mutablePos.setPos(colPos).move(Direction.SOUTH), world);
			                        	
			                            if (
			                                (east && biomeMap.get(mutablePos.setPos(colPos).move(Direction.EAST).toLong()).getCategory() != Biome.Category.OCEAN) ||
			                                (west && biomeMap.get(mutablePos.setPos(colPos).move(Direction.WEST).toLong()).getCategory() != Biome.Category.OCEAN) ||
			                                (north && biomeMap.get(mutablePos.setPos(colPos).move(Direction.NORTH).toLong()).getCategory() != Biome.Category.OCEAN) ||
			                                (south && biomeMap.get(mutablePos.setPos(colPos).move(Direction.SOUTH).toLong()).getCategory() != Biome.Category.OCEAN)
			                            ) {
			                                continue;
			                            }
			                        }
			                        
			                        BlockState liquidBlock = liquidBlocks[localX][localZ];

									float finalNoise = NoiseUtil.lerp(section[x0 + 16 * z0], prevSection[x0 + 16 * z0],
											0.25f * y0);
									finalNoise *= heightFadeValue;

									BitSet bitmask = flooded ? liquidBitmask : airBitmask;
									
									if (finalNoise > NOISE_THRESHOLD) {
										// Create cave if possible
										BlockState originalState = chunkIn.getBlockState(pos);
										BlockState blockStateAbove = chunkIn.getBlockState(pos.up());
										if (CarverUtils.canReplaceBlock(originalState,chunkIn.getBlockState(pos.up())) && originalState.getBlock() != Blocks.BEDROCK) {
											 if (flooded) {
												 CarverUtils.carveFloodedBlock(chunkIn, new Random(), pos, liquidBlock, 12, true, bitmask);
											 } else {
												 if (blockStateAbove.getMaterial() != Material.WATER && blockStateAbove.getMaterial() != Material.LAVA){
												 	CarverUtils.carveBlock(chunkIn, pos, liquidBlock, 12, true, bitmask);
												 }
											 }
										}
									}
								}
							}
						}
					} // End of x/z iteration
				}
			}
			// End of x/z loop, so move section to previous
			prevSection = Arrays.copyOf(section, section.length);
		}
	}
	
	@SuppressWarnings("unused")
	private IWorld world;
	
	public void setWorld(IWorld worldIn) {
        this.world = worldIn;
    }
}
