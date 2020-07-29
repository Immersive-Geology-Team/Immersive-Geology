package com.igteam.immersivegeology.common.world.chunk;

import com.igteam.immersivegeology.api.materials.MaterialTypes;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.api.materials.material_bases.MaterialMineralBase.EnumMineralType;
import com.igteam.immersivegeology.api.util.IGRegistryGrabber;
import com.igteam.immersivegeology.common.blocks.IGBaseBlock;
import com.igteam.immersivegeology.common.materials.EnumMaterials;
import com.igteam.immersivegeology.common.materials.EnumMaterials;
import com.igteam.immersivegeology.common.world.ImmersiveBiomeProvider;
import com.igteam.immersivegeology.common.world.biome.IGBiome;
import com.igteam.immersivegeology.common.world.biome.IGBiomes;
import com.igteam.immersivegeology.common.world.chunk.data.ChunkDataProvider;
import com.igteam.immersivegeology.common.world.gen.carver.ImmersiveCarver;
import com.igteam.immersivegeology.common.world.gen.carver.WorleyCaveCarver;
import com.igteam.immersivegeology.common.world.gen.carver.WorleyOreCarver;
import com.igteam.immersivegeology.common.world.gen.config.ImmersiveGenerationSettings;
import com.igteam.immersivegeology.common.world.layer.BiomeLayerData;
import com.igteam.immersivegeology.common.world.layer.WorldLayerData;
import com.igteam.immersivegeology.common.world.noise.INoise2D;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.*;
import net.minecraft.world.gen.Heightmap.Type;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author alcatrazEscapee modified for use in IG by Muddykat
 */

public class ChunkGeneratorImmersiveOverworld extends ChunkGenerator<ImmersiveGenerationSettings>
{

	public static WorldLayerData data;

	private static final double[] PARABOLIC_FIELD = Util.make(new double[9*9], array -> {
		for(int x = 0; x < 9; x++)
		{
			for(int z = 0; z < 9; z++)
			{
				array[x+9*z] = 0.0211640211641D*(1-0.03125D*((z-4)*(z-4)+(x-4)*(x-4)));
			}
		}
	});

	private final WorleyCaveCarver worleyCaveCarver;
	private final ImmersiveCarver immersiveCarver;
	
	private final ImmersiveBiomeProvider biomeProvider;
	private final ChunkDataProvider chunkDataProvider;

	public ChunkGeneratorImmersiveOverworld(IWorld world, BiomeProvider provider,
											ImmersiveGenerationSettings settings)
	{
		super(world, provider, settings);
		boolean usePerlin = true;
		Random seedGenerator = new Random(world.getSeed());
		this.surfaceDepthNoise = usePerlin?new PerlinNoiseGenerator(seedGenerator, 4)
				: new OctavesNoiseGenerator(seedGenerator, 4);
		data = new WorldLayerData();
		final long biomeNoiseSeed = seedGenerator.nextLong();

		this.biomeNoiseMap = new HashMap<>();
		IGBiomes.getBiomes().forEach(biome -> biomeNoiseMap.put(biome, biome.createNoiseLayer(biomeNoiseSeed)));

		this.biomeProvider = (ImmersiveBiomeProvider)provider;

		this.immersiveCarver = new ImmersiveCarver();
		this.immersiveCarver.initialize(world);
		this.worleyCaveCarver = new WorleyCaveCarver(seedGenerator);

		EnumMaterials.filterWorldGen().forEach((ore) -> {
				WorleyOreCarver.INSTANCE.setupNewLayer(seedGenerator, ore);
		});
		

		this.chunkDataProvider = new ChunkDataProvider(world, settings, seedGenerator);
	}


	@Override
	public void carve(IChunk chunkIn, GenerationStage.Carving stage)
	{
		if(stage==GenerationStage.Carving.AIR)
		{
			// First, run worley cave carver
			worleyCaveCarver.carve(chunkIn, chunkIn.getPos().x<<4, chunkIn.getPos().z<<4);
			this.immersiveCarver.carve(chunkIn, chunkIn.getPos().x, chunkIn.getPos().z);
			
			
			for(BiomeLayerData biomeData : data.worldLayerData)
			{
				int totalLayers = biomeData.getLayerCount();
				for(int layer = totalLayers; layer > 0; layer--)
				{
					if(biomeData.getLayerOre(layer)!=null)
					{
						WorleyOreCarver.INSTANCE.carve(chunkIn, chunkIn.getPos().x<<4, chunkIn.getPos().z<<4, biomeData, layer);
					}
				}
			}

		}

		// Fire other world carvers
		super.carve(chunkIn, stage);
	}

	@Override
	public int getGroundHeight()
	{
		return getSeaLevel()+1;
	}

	@Override
	public int getSeaLevel()
	{
		return SEA_LEVEL;
	}

	private final INoiseGenerator surfaceDepthNoise;

	private final Map<Biome, INoise2D> biomeNoiseMap;
	public static int SEA_LEVEL = 96;

	@Override
	public void makeBase(IWorld worldIn, IChunk chunk)
	{
		// Initialization
		ChunkPos chunkPos = chunk.getPos();
		SharedSeedRandom random = new SharedSeedRandom();
		int chunkX = chunkPos.getXStart(), chunkZ = chunkPos.getZStart();
		random.setBaseChunkSeed(chunkPos.x, chunkPos.z);

		// The spread biomes (for calculating terrain smoothing), and the 16x16 biome
		// grid (for height map creation)
		IGBiome[] spreadBiomes = biomeProvider.getBiomes(chunkX-4, chunkZ-4, 24, 24, false);
		IGBiome[] localBiomes = new IGBiome[16*16];

		// Build the base height map, and also assign surface types (different from
		// biomes because we need more control)
		double[] baseHeight = new double[16*16];
		double[] baseRockHeight = new double[16*16];
		Object2DoubleMap<IGBiome> heightBiomeMap = new Object2DoubleOpenHashMap<>(4);
		for(int x = 0; x < 16; x++)
		{
			for(int z = 0; z < 16; z++)
			{
				// At each position, apply the parabolic field to a 9x9 square area around the
				// start position
				heightBiomeMap.clear();
				double totalHeight = 0, riverHeight = 0, shoreHeight = 0;
				double riverWeight = 0, shoreWeight = 0;
				for(int xOffset = 0; xOffset < 9; xOffset++)
				{
					for(int zOffset = 0; zOffset < 9; zOffset++)
					{
						// Get the biome at the position and add it to the height biome map
						IGBiome biomeAt = spreadBiomes[(x+xOffset)+24*(z+zOffset)];
						heightBiomeMap.mergeDouble(biomeAt, PARABOLIC_FIELD[xOffset+9*zOffset], Double::sum);
						// Sum the rock layer height
						baseRockHeight[z+16*x] += PARABOLIC_FIELD[zOffset+9*xOffset]*(SEA_LEVEL/2);                                                                            // biomeAt.getDefaultRockHeight();
					}
				}

				// The biome to reference when building the initial surface
				IGBiome biomeAt = spreadBiomes[(x+4)+24*(z+4)];
				IGBiome shoreBiomeAt = biomeAt, standardBiomeAt = biomeAt;
				double maxShoreWeight = 0, maxStandardBiomeWeight = 0;

				// calculate the total height based on the biome noise map, using a custom
				// Noise2D for each biome
				for(Object2DoubleMap.Entry<IGBiome> entry : heightBiomeMap.object2DoubleEntrySet())
				{
					double weight = entry.getDoubleValue();
					double height = weight*biomeNoiseMap.get(entry.getKey()).noise(chunkX+x, chunkZ+z);
					totalHeight += height;
					if(entry.getKey()==IGBiomes.RIVER)
					{
						riverHeight += height;
						riverWeight += weight;
					}
					else if(entry.getKey()==IGBiomes.SHORE||entry.getKey()==IGBiomes.STONE_SHORE)
					{
						shoreHeight += height;
						shoreWeight += weight;

						if(maxShoreWeight < weight)
						{
							shoreBiomeAt = entry.getKey();
							maxShoreWeight = weight;
						}
					}
					else if(maxStandardBiomeWeight < weight)
					{
						standardBiomeAt = entry.getKey();
						maxStandardBiomeWeight = weight;
					}
				}

				// Create river valleys - carve cliffs around river biomes, and smooth out the
				// edges
				double actualHeight = totalHeight;
				if(riverWeight > 0.6)
				{
					// River bottom / shore
					double aboveWaterDelta = actualHeight-riverHeight/riverWeight;
					if(aboveWaterDelta > 0)
					{
						if(aboveWaterDelta > 20)
						{
							aboveWaterDelta = 20;
						}
						double adjustedAboveWaterDelta = 0.02*aboveWaterDelta*(40-aboveWaterDelta)-0.48;
						actualHeight = riverHeight/riverWeight+adjustedAboveWaterDelta;
					}
					biomeAt = IGBiomes.RIVER; // Use river surface for the bottom of the river + small shore beneath
					// cliffs
				}
				else if(riverWeight > 0)
				{
					double adjustedRiverWeight = 0.6*riverWeight;
					actualHeight = (totalHeight-riverHeight)*((1-adjustedRiverWeight)/(1-riverWeight))
							+riverHeight*(adjustedRiverWeight/riverWeight);

					if(biomeAt==IGBiomes.RIVER)
					{
						biomeAt = standardBiomeAt;
					}
				}

				// Flatten shores, and create cliffs on the edges
				if(shoreWeight > 0.4)
				{
					if(actualHeight > SEA_LEVEL+1)
					{
						actualHeight = SEA_LEVEL+1;
					}
					biomeAt = shoreBiomeAt;
				}
				else if(shoreWeight > 0)
				{
					double adjustedShoreWeight = 0.4*shoreWeight;
					actualHeight = (actualHeight-shoreHeight)*((1-adjustedShoreWeight)/(1-shoreWeight))
							+shoreHeight*(adjustedShoreWeight/shoreWeight);

					if(biomeAt==shoreBiomeAt)
					{
						biomeAt = standardBiomeAt;
					}
				}

				baseHeight[x+16*z] = actualHeight;
				localBiomes[x+16*z] = biomeAt;
			}
		}

		// Build Rough Terrain
		IGBaseBlock replaceBlock = IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Rhyolite.material);

		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		for(int x = 0; x < 16; x++)
		{
			for(int z = 0; z < 16; z++)
			{
				double totalHeight = baseHeight[x+16*z];
				boolean noGen = true;
				for(int y = 0; y <= (int)totalHeight; y++)
				{
					for(BiomeLayerData b : data.worldLayerData)
					{

						pos.setPos(chunkX+x, y, chunkZ+z);
						Biome biome = chunk.getBiome(new BlockPos(x, y, z));
						if(b.getLbiome()==biome)
						{

							noGen = false;
							int lc = b.getLayerCount();
							for(int l = lc; l > 0; l--)
							{
								int totHeight = 256;
								replaceBlock = b.getLayerBlock(l);

								//merge layers together a tad
								if((y <= (totHeight*l)/lc)&&(y > ((totHeight*l)/lc)-2))
								{
									if(random.nextInt(3)==1)
									{
										replaceBlock = b.getLayerBlock(Math.min(lc, l+1));
									}
								}
								else
								{
									replaceBlock = b.getLayerBlock(l);
								}

								if((y <= (totHeight*l)/lc)
										&&(y >= (((totHeight*l)/lc)-((totHeight*l)/lc)/l)))
								{


									chunk.setBlockState(pos, replaceBlock.getDefaultState()
													.with(IGBaseBlock.NATURAL, Boolean.TRUE),
											true);
								}

							}
						}
					}

					if(noGen)
					{
						BiomeLayerData b = data.worldLayerData.get(0);
						int lc = b.getLayerCount();
						for(int l = lc; l > 0; l--)
						{
							int totHeight = 256;
							if((y <= (totHeight*l)/lc)
									&&(y >= (((totHeight*l)/lc)-((totHeight*l)/lc)/l)))
							{
								replaceBlock = IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Pegamite.material);
								for(int randomLevel = 0; randomLevel < 3; randomLevel++)
								{

									if(randomLevel!=0&&random.nextInt()%2==0)
									{
										pos.add(0, random.nextInt(2)-random.nextInt(2), 0);
									}

									chunk.setBlockState(pos, replaceBlock.getDefaultState()
											.with(IGBaseBlock.NATURAL, Boolean.TRUE), true);
								}
							}
						}
					}
				}
				for(int y = (int)totalHeight+1; y <= (SEA_LEVEL-1); y++)
				{
					pos.setPos(chunkX+x, y, chunkZ+z);
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
	public void generateSurface(IChunk chunk)
	{

		ChunkPos chunkPos = chunk.getPos();
		SharedSeedRandom random = new SharedSeedRandom();
		random.setBaseChunkSeed(chunkPos.x, chunkPos.z);

		Biome[] biomes = chunk.getBiomes();

		//TODO Get this to work with the chunk data provider to allow the game to change it's temp
		float temperature = chunkDataProvider.getOrCreate(chunkPos).getRegionalTemp();
		float rainfall = chunkDataProvider.getOrCreate(chunkPos).getRainfall();

		for(int x = 0; x < 16; x++)
		{
			for(int z = 0; z < 16; z++)
			{
				float noise = 0;
				int topYLevel = chunk.getTopBlockY(Heightmap.Type.WORLD_SURFACE_WG, x, z)+1;
				((IGBiome)biomes[x+16*z]).getIGSurfaceBuilder().buildSurface(random, chunk, chunkPos.getXStart()+x, chunkPos.getZStart()+z, topYLevel+1, temperature, rainfall, noise);
			}
		}

		makeBedrock(chunk, random);
	}

	public static final BlockState BEDROCK = Blocks.BEDROCK.getDefaultState();


	public void makeBedrock(IChunk chunk, Random random)
	{
		// should not reference ImersiveGenerationSettings directly, should use
		// getSettings()
		boolean flatBedrock = ImmersiveGenerationSettings.isFlatBedrock();
		BlockPos.MutableBlockPos posAt = new BlockPos.MutableBlockPos();
		for(BlockPos pos : BlockPos.getAllInBoxMutable(chunk.getPos().getXStart(), 0, chunk.getPos().getZStart(),
				chunk.getPos().getXStart()+15, 0, chunk.getPos().getZStart()+15))
		{
			if(flatBedrock)
			{
				chunk.setBlockState(pos, BEDROCK, false);
			}
			else
			{
				int yMax = random.nextInt(5);
				for(int y = 4; y >= 0; y--)
				{
					if(y <= yMax)
					{
						chunk.setBlockState(posAt.setPos(pos.getX(), y, pos.getZ()), BEDROCK, false);
					}
				}
			}
		}
	}

	@Nonnull
	public ChunkDataProvider getChunkDataProvider()
	{
		return chunkDataProvider;
	}

	@Override
	public int func_222529_a(int p_222529_1_, int p_222529_2_, Type p_222529_3_)
	{
		// TODO Auto-generated method stub
		return 0;
	}
}
