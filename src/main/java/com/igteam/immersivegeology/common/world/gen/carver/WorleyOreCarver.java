package com.igteam.immersivegeology.common.world.gen.carver;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.common.IGRegistryGrabber;
import com.igteam.immersivegeology.common.blocks.IGMaterialBlock;
import com.igteam.immersivegeology.common.blocks.property.IGProperties;
import com.igteam.immersivegeology.common.materials.EnumMaterials;
import com.igteam.immersivegeology.common.world.biome.IGBiome;
import com.igteam.immersivegeology.common.world.chunk.ChunkGeneratorImmersiveOverworld;
import com.igteam.immersivegeology.common.world.layer.BiomeLayerData;
import com.igteam.immersivegeology.common.world.layer.BiomeLayerData.LayerOre;
import com.igteam.immersivegeology.common.world.layer.WorldLayerData;
import com.igteam.immersivegeology.common.world.noise.INoise3D;
import com.igteam.immersivegeology.common.world.noise.NoiseUtil;
import com.igteam.immersivegeology.common.world.noise.OpenSimplexNoise;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunk;

import java.util.*;

public class WorleyOreCarver
{

	private static final int SAMPLE_HEIGHT = 64;
	private static float NOISE_THRESHOLD = 0.35f;

	private static final BlockState BEDROCK = Blocks.BEDROCK.getDefaultState();

	// size of the cave intrest points!
	private static final float FEATURE_SIZE = 16;

	private Map<String, INoise3D> oreNoiseArray = new HashMap<String, INoise3D>();
	public static WorleyOreCarver INSTANCE = new WorleyOreCarver();

	public WorleyOreCarver(){};

	public void setupNewLayer(Random seedGenerator, EnumMaterials oreMaterial, int offset)
	{
		if(!oreNoiseArray.containsKey(oreMaterial.toString().toLowerCase()))
		{

			OpenSimplexNoise oreNoiseWorley = new OpenSimplexNoise(seedGenerator.nextLong() + offset);
			OpenSimplexNoise oreNoiseWorleySub = new OpenSimplexNoise(seedGenerator.nextLong() + offset + 1);

			//this is to make ore veins less common (without this it's way too common)
			INoise3D oreNoiseSub = (x, y, z) -> {
				return oreNoiseWorleySub.flattened(0.4f, 1f).octaves(1, 0.75f).noise(x/(FEATURE_SIZE*5), y/(FEATURE_SIZE*5), z/(FEATURE_SIZE*5));
			};

			INoise3D oreNoise = (x, y, z) -> {
				return oreNoiseWorley.flattened(0f, 1f).octaves(3, 0.8f).noise(x/FEATURE_SIZE, y/FEATURE_SIZE, z/FEATURE_SIZE);
			};


			oreNoiseArray.put(oreMaterial.toString().toLowerCase(), oreNoise.sub(oreNoiseSub));
		}
	}

	WorldLayerData worldLayerData = ChunkGeneratorImmersiveOverworld.data;

	//TODO layer ore not spawning correctly (the top layer seems to be missing) (that means pretty likely all layers aside from bottom are missing
	public void carve(IChunk chunkIn, int chunkX, int chunkZ, BiomeLayerData biomeData, int currentLayer)
	{
		ArrayList<LayerOre> oreArrayData = biomeData.getLayerOre(currentLayer);
		// run this through a full loop, for each ore, returns out if ore is not set in the biomes layer data!
		int totalLayerCount = biomeData.getLayerCount();
		Material baseMaterial = ((IGMaterialBlock)biomeData.getLayerBlock((currentLayer))).getMaterial();
		LayerOre oreData = null;
		for(LayerOre ore : oreArrayData)
		{
			oreData = ore;

			//if ore data is not found, no ore to generate, exit out of carve function
			if(oreData==null)
			{
				return;
			}

			//ore data has been retrieved, set ore material data for later use
			Material oreMaterial = oreData.getOre().material;

			//if ore data is found, generate ore
			float coverage = oreData.getCoverage();

			NOISE_THRESHOLD = (1-coverage);

			//We need to make sure that ore won't replace EVERYTHING, so a min of 0.25 is required.
			if(NOISE_THRESHOLD < 0.35)
			{
				NOISE_THRESHOLD = 0.35f;
			}

			if(NOISE_THRESHOLD > 0.8)
			{
				NOISE_THRESHOLD = 0.8f; //makes it so 'rare' ores actually have a chance to spawn!
			}

			//  if their is no noise array for material, return out of function again!
			// (This should never be the case, but this is incase someone does a stupid
			if(!oreNoiseArray.containsKey(oreMaterial.toString().toLowerCase()))
			{
				return;
			}

			INoise3D oreNoise = oreNoiseArray.get(oreMaterial.toString().toLowerCase());

			float[] noiseValues = new float[5*5*SAMPLE_HEIGHT];

			// Sample initial noise values
			for(int x = 0; x < 5; x++)
			{
				for(int z = 0; z < 5; z++)
				{
					for(int y = 0; y < SAMPLE_HEIGHT; y++)
					{
						noiseValues[x+(z*5)+(y*25)] = oreNoise.noise((chunkX+x*4), (y*7.3f), (chunkZ+z*4));
					}
				}
			}

			float[] section = new float[16*16];
			float[] prevSection = null;
			BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

			// Create caves, layer by layer
			for(int y = SAMPLE_HEIGHT-1; y >= 0; y--)
			{
				for(int x = 0; x < 4; x++)
				{
					for(int z = 0; z < 4; z++)
					{
						float noiseUNW = noiseValues[(x+0)+((z+0)*5)+((y+0)*25)];
						float noiseUNE = noiseValues[(x+1)+((z+0)*5)+((y+0)*25)];
						float noiseUSW = noiseValues[(x+0)+((z+1)*5)+((y+0)*25)];
						float noiseUSE = noiseValues[(x+1)+((z+1)*5)+((y+0)*25)];

						float noiseMidN, noiseMidS;

						// Lerp east-west edges
						for(int sx = 0; sx < 4; sx++)
						{
							// Increasing x -> moving east
							noiseMidN = NoiseUtil.lerp(noiseUNW, noiseUNE, 0.25f*sx);
							noiseMidS = NoiseUtil.lerp(noiseUSW, noiseUSE, 0.25f*sx);

							// Lerp faces
							for(int sz = 0; sz < 4; sz++)
							{
								// Increasing z -> moving south
								section[(x*4+sx)+(z*4+sz)*16] = NoiseUtil.lerp(noiseMidN, noiseMidS, 0.25f*sz);
							}
						}

						if(prevSection!=null)
						{
							// We aren't on the first section, so we need to interpolate between sections, and assign blocks from the previous section up until this one
							for(int y0 = 4-1; y0 >= 0; y0--)
							{
								int yPos = y*4+y0;
								float heightFadeValue = 1;
								for(int x0 = x*4; x0 < (x+1)*4; x0++)
								{
									for(int z0 = z*4; z0 < (z+1)*4; z0++)
									{
										// set the current position
										pos.setPos(chunkX+x0, yPos, chunkZ+z0);
										BlockState replacementState = Blocks.PINK_WOOL.getDefaultState();

										if(chunkIn.getBiome(pos) instanceof IGBiome)
										{
											IGBiome foundBiome = (IGBiome)chunkIn.getBiome(pos);
											if(foundBiome.equals(biomeData.getLbiome()))
											{

												int totHeight = 256;
												float finalNoise = NoiseUtil.lerp(section[x0+16*z0], prevSection[x0+16*z0], 0.25f*y0);
												//change height fade value so we get less ores the higher up we are

												int topOfLayer = (totHeight*currentLayer)/totalLayerCount;
												int bottomOfLayer = (((totHeight*currentLayer)/totalLayerCount)-((totHeight*currentLayer)/totalLayerCount)/currentLayer);

												if((yPos > (topOfLayer * 0.66))) {
													float dif = (float) ((yPos - (topOfLayer * 0.66)) / topOfLayer);
													heightFadeValue = 1 - dif;
												}

												if((yPos <= topOfLayer) && (yPos >= bottomOfLayer))
												{
													replacementState = IGRegistryGrabber.grabBlock(MaterialUseType.ORE_BEARING, baseMaterial, oreMaterial).getDefaultState().with(IGProperties.NATURAL, true);

													//Run spawn in here to avoid creating ore outside of OUR layer.
													finalNoise *= heightFadeValue;

													if(finalNoise > NOISE_THRESHOLD)
													{
														int richness;

														if(finalNoise >= NOISE_THRESHOLD+(1-NOISE_THRESHOLD)*0.85)
														{
															richness = 3; // DENSE
														}
														else if(finalNoise >= NOISE_THRESHOLD+(1-NOISE_THRESHOLD)*0.45)
														{
															richness = 2; // RICH
														}
														else if(finalNoise >= NOISE_THRESHOLD+(1-NOISE_THRESHOLD)*0.3)
														{
															richness = 1; // NORMAL
														}
														else
														{
															richness = 0;
														}

														// Create cave if possible
														BlockState originalState = chunkIn.getBlockState(pos);
														if(!originalState.isAir(chunkIn, pos)&&originalState!=BEDROCK&&!originalState.getMaterial().isLiquid()&&(originalState.getBlock() instanceof IGMaterialBlock))
														{
															chunkIn.setBlockState(pos, replacementState.with(IGProperties.ORE_RICHNESS, richness), false);
														}
													}
												}
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
}
