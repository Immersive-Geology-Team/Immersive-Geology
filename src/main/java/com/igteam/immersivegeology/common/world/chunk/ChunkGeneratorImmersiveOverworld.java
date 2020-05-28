package com.igteam.immersivegeology.common.world.chunk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.common.blocks.EnumMaterials;
import com.igteam.immersivegeology.common.blocks.IGBaseBlock;
import com.igteam.immersivegeology.common.util.IGBlockGrabber;
import com.igteam.immersivegeology.common.world.biome.BiomeLayerData;
import com.igteam.immersivegeology.common.world.biome.IGBiome;
import com.igteam.immersivegeology.common.world.biome.IGBiomes;
import com.igteam.immersivegeology.common.world.biome.WorldLayerData;
import com.igteam.immersivegeology.common.world.gen.config.ImmersiveGenerationSettings;
import com.igteam.immersivegeology.common.world.noise.INoise2D;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.INoiseGenerator;
import net.minecraft.world.gen.NoiseChunkGenerator;
import net.minecraft.world.gen.OctavesNoiseGenerator;
import net.minecraft.world.gen.OverworldChunkGenerator;
import net.minecraft.world.gen.OverworldGenSettings;
import net.minecraft.world.gen.PerlinNoiseGenerator;
import com.igteam.immersivegeology.common.world.ImmersiveBiomeProvider;

public class ChunkGeneratorImmersiveOverworld extends NoiseChunkGenerator<ImmersiveGenerationSettings> {

	public static WorldLayerData data;

	private static final double[] PARABOLIC_FIELD = Util.make(new double[9 * 9], array -> {
		for (int x = 0; x < 9; x++) {
			for (int z = 0; z < 9; z++) {
				array[x + 9 * z] = 0.0211640211641D * (1 - 0.03125D * ((z - 4) * (z - 4) + (x - 4) * (x - 4)));
			}
		}
	});
	private final ImmersiveBiomeProvider biomeProvider;
	public ChunkGeneratorImmersiveOverworld(IWorld world, BiomeProvider provider,
			ImmersiveGenerationSettings settings) {
		super(world, provider, 4, 8, 256, settings, false);
		boolean usePerlin = true;
		this.surfaceDepthNoise = (INoiseGenerator) (usePerlin ? new PerlinNoiseGenerator(this.randomSeed, 4)
				: new OctavesNoiseGenerator(this.randomSeed, 4));
		data = new WorldLayerData();
		Random seedGenerator = new Random(world.getSeed());
		final long biomeNoiseSeed = seedGenerator.nextLong();

		this.biomeNoiseMap = new HashMap<>();
		IGBiomes.getBiomes().forEach(biome -> biomeNoiseMap.put(biome, biome.createNoiseLayer(biomeNoiseSeed)));

		this.biomeProvider = (ImmersiveBiomeProvider) provider;
	}

	@Override
	public int getGroundHeight() {
		return getSeaLevel() + 1;
	}

	@Override
	public int getSeaLevel() {
		return SEA_LEVEL;
	}

	private final INoiseGenerator surfaceDepthNoise;

	private final Map<Biome, INoise2D> biomeNoiseMap;
	public static int SEA_LEVEL = 96;

	@Override
	public void makeBase(IWorld worldIn, IChunk chunk) {
		// Initialization
		ChunkPos chunkPos = chunk.getPos();
		SharedSeedRandom random = new SharedSeedRandom();
		int chunkX = chunkPos.getXStart(), chunkZ = chunkPos.getZStart();
		random.setBaseChunkSeed(chunkPos.x, chunkPos.z);

		// The spread biomes (for calculating terrain smoothing), and the 16x16 biome
		// grid (for height map creation)
		Biome[] spreadBiomes = biomeProvider.getBiomes(chunkX - 4, chunkZ - 4, 24, 24, false);
		Biome[] localBiomes = new Biome[16 * 16];

		// Build the base height map, and also assign surface types (different from
		// biomes because we need more control)
		double[] baseHeight = new double[16 * 16];
		double[] baseRockHeight = new double[16 * 16];
		Object2DoubleMap<Biome> heightBiomeMap = new Object2DoubleOpenHashMap<>(4);
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				// At each position, apply the parabolic field to a 9x9 square area around the
				// start position
				heightBiomeMap.clear();
				double totalHeight = 0, riverHeight = 0, shoreHeight = 0;
				double riverWeight = 0, shoreWeight = 0;
				for (int xOffset = 0; xOffset < 9; xOffset++) {
					for (int zOffset = 0; zOffset < 9; zOffset++) {
						// Get the biome at the position and add it to the height biome map
						Biome biomeAt = spreadBiomes[(x + xOffset) + 24 * (z + zOffset)];
						heightBiomeMap.mergeDouble(biomeAt, PARABOLIC_FIELD[xOffset + 9 * zOffset], Double::sum);

						// Sum the rock layer height
						baseRockHeight[z + 16 * x] += PARABOLIC_FIELD[zOffset + 9 * xOffset] * (SEA_LEVEL + 4);// TODO
																												// biomeAt.getDefaultRockHeight();
					}
				}

				// The biome to reference when building the initial surface
				Biome biomeAt = spreadBiomes[(x + 4) + 24 * (z + 4)];
				Biome shoreBiomeAt = biomeAt, standardBiomeAt = biomeAt;
				double maxShoreWeight = 0, maxStandardBiomeWeight = 0;

				// calculate the total height based on the biome noise map, using a custom
				// Noise2D for each biome
				for (Object2DoubleMap.Entry<Biome> entry : heightBiomeMap.object2DoubleEntrySet()) {
					double weight = entry.getDoubleValue();
					double height = weight * biomeNoiseMap.get(entry.getKey()).noise(chunkX + x, chunkZ + z);
					totalHeight += height;
					if (entry.getKey() == Biomes.RIVER) {
						riverHeight += height;
						riverWeight += weight;
					} else if (entry.getKey() == Biomes.BEACH || entry.getKey() == Biomes.STONE_SHORE) {
						shoreHeight += height;
						shoreWeight += weight;

						if (maxShoreWeight < weight) {
							shoreBiomeAt = entry.getKey();
							maxShoreWeight = weight;
						}
					} else if (maxStandardBiomeWeight < weight) {
						standardBiomeAt = entry.getKey();
						maxStandardBiomeWeight = weight;
					}
				}

				// Create river valleys - carve cliffs around river biomes, and smooth out the
				// edges
				double actualHeight = totalHeight;
				if (riverWeight > 0.6) {
					// River bottom / shore
					double aboveWaterDelta = actualHeight - riverHeight / riverWeight;
					if (aboveWaterDelta > 0) {
						if (aboveWaterDelta > 20) {
							aboveWaterDelta = 20;
						}
						double adjustedAboveWaterDelta = 0.02 * aboveWaterDelta * (40 - aboveWaterDelta) - 0.48;
						actualHeight = riverHeight / riverWeight + adjustedAboveWaterDelta;
					}
					biomeAt = Biomes.RIVER; // Use river surface for the bottom of the river + small shore beneath
											// cliffs
				} else if (riverWeight > 0) {
					double adjustedRiverWeight = 0.6 * riverWeight;
					actualHeight = (totalHeight - riverHeight) * ((1 - adjustedRiverWeight) / (1 - riverWeight))
							+ riverHeight * (adjustedRiverWeight / riverWeight);

					if (biomeAt == Biomes.RIVER) {
						biomeAt = standardBiomeAt;
					}
				}

				// Flatten shores, and create cliffs on the edges
				if (shoreWeight > 0.4) {
					if (actualHeight > SEA_LEVEL + 1) {
						actualHeight = SEA_LEVEL + 1;
					}
					biomeAt = shoreBiomeAt;
				} else if (shoreWeight > 0) {
					double adjustedShoreWeight = 0.4 * shoreWeight;
					actualHeight = (actualHeight - shoreHeight) * ((1 - adjustedShoreWeight) / (1 - shoreWeight))
							+ shoreHeight * (adjustedShoreWeight / shoreWeight);

					if (biomeAt == shoreBiomeAt) {
						biomeAt = standardBiomeAt;
					}
				}

				baseHeight[x + 16 * z] = actualHeight;
				localBiomes[x + 16 * z] = biomeAt;
			}
		}

		// Build Rough Terrain
		IGBaseBlock replaceBlock = IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Rhyolite.material);

		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				double totalHeight = baseHeight[x + 16 * z];
				boolean noGen = true;
				for (int y = 0; y <= (int) totalHeight; y++) {
					for (BiomeLayerData b : data.worldLayerData) {

						pos.setPos(chunkX + x, y, chunkZ + z);
						Biome biome = chunk.getBiome(new BlockPos(x, y, z));
						if (b.getLbiome() == biome) {
							noGen = false;
							int lc = b.getLayerCount();
							for (int l = lc; l > 0; l--) {
								int totHeight = 256;
								if ((y < (totHeight * l) / lc)
										&& (y >= (((totHeight * l) / lc) - ((totHeight * l) / lc) / l))) {
									replaceBlock = b.getLayerBlock(l);
									
									
									
									// setting the block in here isn't super efficient,
									double nh =  replaceBlock.getDefaultHardness();

									double max = Math.max(1, y);
									double log = Math.log(max / 512d);
									// TODO always >= 256? but it works CURRENTLY due to y level
									nh = -(Math.pow(Math.E, 3.6) * log - 25);
									chunk.setBlockState(pos, replaceBlock.getDefaultState()
											.with(IGBaseBlock.NATURAL, Boolean.TRUE).with(IGBaseBlock.HARDNESS,
													 Math.min(256, Math.max(1, (int)Math.ceil(nh)))),
											true);

								}
							}
						}
					}

					if (noGen) {
						BiomeLayerData b = data.worldLayerData.get(0);
						int lc = b.getLayerCount();
						for (int l = lc; l > 0; l--) {
							int totHeight = 256;
							if ((y < (totHeight * l) / lc)
									&& (y >= (((totHeight * l) / lc) - ((totHeight * l) / lc) / l))) {
								replaceBlock = b.getLayerBlock(l);



								// setting the block in here isn't super efficient,
								double nh =  replaceBlock.getDefaultHardness();

								double max = Math.max(1, y);
								double log = Math.log(max / 512d);
								nh = -(Math.pow(Math.E, 3.6) * log) - 25;
								chunk.setBlockState(pos, replaceBlock.getDefaultState()
												.with(IGBaseBlock.NATURAL, Boolean.TRUE).with(IGBaseBlock.HARDNESS,
												Math.min(256, Math.max(1, (int)Math.ceil(nh)))),
										true);

							}
						}
					}
				}
				for (int y = (int) totalHeight + 1; y <= (SEA_LEVEL - 1); y++) {
					pos.setPos(chunkX + x, y, chunkZ + z);
					chunk.setBlockState(pos, settings.getDefaultFluid(), false);
				}
			}
		}

		// Now set biomes
		chunk.setBiomes(localBiomes);

		// Height maps
		chunk.func_217303_b(Heightmap.Type.OCEAN_FLOOR_WG);
		chunk.func_217303_b(Heightmap.Type.WORLD_SURFACE_WG);
	}
	
	@Override
	public void generateSurface(IChunk chunk) {
		
		ChunkPos chunkPos = chunk.getPos();
		SharedSeedRandom random = new SharedSeedRandom();
		random.setBaseChunkSeed(chunkPos.x, chunkPos.z);
		
		Biome[] biomes = chunk.getBiomes();
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		
		for(int x = 0; x < 16; x++) {
			for(int z = 0; z < 16; z++) {
				float temperature = 5;
				float rainfall = 200;
				float noise = 0;
				int topYLevel = chunk.getTopBlockY(Heightmap.Type.WORLD_SURFACE_WG, x, z) + 1;
				
				((IGBiome) biomes[x + 16 * z]).getIGSurfaceBuilder().buildSurface(random, chunk, chunkPos.getXStart() + x, chunkPos.getZStart() + z, topYLevel + 1, temperature, rainfall, noise);
			}
		}
		
		
		makeBedrock(chunk, random);
	}

	public static final BlockState BEDROCK = Blocks.BEDROCK.getDefaultState();

	@Override
	public void makeBedrock(IChunk chunk, Random random) {
		// should not reference ImersiveGenerationSettings directly, should use
		// getSettings()
		boolean flatBedrock = ImmersiveGenerationSettings.isFlatBedrock();
		BlockPos.MutableBlockPos posAt = new BlockPos.MutableBlockPos();
		for (BlockPos pos : BlockPos.getAllInBoxMutable(chunk.getPos().getXStart(), 0, chunk.getPos().getZStart(),
				chunk.getPos().getXStart() + 15, 0, chunk.getPos().getZStart() + 15)) {
			if (flatBedrock) {
				chunk.setBlockState(pos, BEDROCK, false);
			} else {
				int yMax = random.nextInt(5);
				for (int y = 4; y >= 0; y--) {
					if (y <= yMax) {
						chunk.setBlockState(posAt.setPos(pos.getX(), y, pos.getZ()), BEDROCK, false);
					}
				}
			}
		}
	}

	@Override
	protected double[] func_222549_a(int p_222549_1_, int p_222549_2_) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected double func_222545_a(double p_222545_1_, double p_222545_3_, int p_222545_5_) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void func_222548_a(double[] p_222548_1_, int p_222548_2_, int p_222548_3_) {
		// TODO Auto-generated method stub

	}
}
