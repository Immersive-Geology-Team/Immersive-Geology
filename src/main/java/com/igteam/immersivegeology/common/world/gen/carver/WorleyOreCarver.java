package com.igteam.immersivegeology.common.world.gen.carver;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.common.blocks.IGMaterialBlock;
import com.igteam.immersivegeology.common.blocks.IGOreBearingBlock;
import com.igteam.immersivegeology.common.blocks.property.IGProperties;
import com.igteam.immersivegeology.common.materials.EnumOreBearingMaterials;
import com.igteam.immersivegeology.common.util.IGBlockGrabber;
import com.igteam.immersivegeology.common.world.biome.IGBiome;
import com.igteam.immersivegeology.common.world.biome.biomes.MountainsBiome;
import com.igteam.immersivegeology.common.world.layer.BiomeLayerData;
import com.igteam.immersivegeology.common.world.layer.BiomeLayerData.LayerOre;
import com.igteam.immersivegeology.common.world.noise.INoise3D;
import com.igteam.immersivegeology.common.world.noise.NoiseUtil;
import com.igteam.immersivegeology.common.world.noise.OpenSimplexNoise;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunk;

public class WorleyOreCarver {
	
	private static final int SAMPLE_HEIGHT = 32;
	private static float NOISE_THRESHOLD = 0.35f;
	private static float HEIGHT_FADE_THRESHOLD = 45; 

	private static final BlockState AIR = Blocks.AIR.getDefaultState();
	private static final BlockState BEDROCK = Blocks.BEDROCK.getDefaultState();	
	
	// size of the cave intrest points!
	private static final float FEATURE_SIZE = 8;
	
	 
	private Map<String, INoise3D> caveNoiseArray = new HashMap<String, INoise3D>();
	public static WorleyOreCarver INSTANCE = new WorleyOreCarver();
	
	public WorleyOreCarver() {} 
	
	public void setupNewLayer(Random seedGenerator, BiomeLayerData biomeData, int biomeLayerID, LayerOre ore) {
		// The reason we need a new generator each layer, biome and ore is due to the fact that we need a new set of noise to prevent it from just 
		// replacing previously generated blocks, each layer can have unquie ores, so we need a new generator for that ore type...
		// I'm tempted to attempt to rework the generator to not create a unquie generator per ore per biome per layer, but rather
		// per biome, per ore, and remove the layer iteration from the setup... 
		// However we need that to get the coverage for each biomes layer data
		
		if(!caveNoiseArray.containsKey(biomeData.getLbiome().getDisplayName().toString() + "_" + String.valueOf(biomeLayerID) + "_" + ore.getOre().toString().toLowerCase())) {
			OpenSimplexNoise caveNoiseWorley = new OpenSimplexNoise(seedGenerator.nextLong() + biomeLayerID);
			
			INoise3D caveNoise = (x, y, z) -> {
	            return caveNoiseWorley.spread(0.5f).noise(x / FEATURE_SIZE, y / FEATURE_SIZE, z / FEATURE_SIZE);
			};
			caveNoiseArray.put(biomeData.getLbiome().getDisplayName().toString() + "_" + String.valueOf(biomeLayerID) + "_" + ore.getOre().toString().toLowerCase(), caveNoise);
		}
	}

	//TODO layer ore not spawning correctly (the top layer seems to be missing) (that means pretty likely all layers aside from bottom are missing
    public void carve(IChunk chunkIn, int chunkX, int chunkZ, BiomeLayerData biomeData, int biomeLayerID, int layer, EnumOreBearingMaterials oreMaterial, float coverage)
    {
    	
    	NOISE_THRESHOLD = (1 - coverage);
    	
    	//We need to make sure that ore won't replace EVERYTHING, so a min of 0.25 is required.
    	if(NOISE_THRESHOLD < 0.45) {
    		NOISE_THRESHOLD = 0.45f;
    	}
    	
    	if(NOISE_THRESHOLD > 0.8) {
    		NOISE_THRESHOLD = 0.8f; //makes it so 'rare' ores actually have a chance to spawn!
    	}
    	
    	if(!caveNoiseArray.containsKey(biomeData.getLbiome().getDisplayName().toString() + "_" + String.valueOf(biomeLayerID) + "_" + oreMaterial.toString().toLowerCase())) {
    		return;
    	}
    	
    	INoise3D caveNoise = caveNoiseArray.get(biomeData.getLbiome().getDisplayName().toString() + "_" + String.valueOf(biomeLayerID) + "_" + oreMaterial.toString().toLowerCase());
    	
        float[] noiseValues = new float[5 * 5 * SAMPLE_HEIGHT];
        
        // Sample initial noise values
        for (int x = 0; x < 5; x++)
        {
            for (int z = 0; z < 5; z++)
            { 
                for (int y = 0; y < SAMPLE_HEIGHT; y++)
                {
                    noiseValues[x + (z * 5) + (y * 25)] = caveNoise.noise((chunkX + x * 4),(y * 7.3f), (chunkZ + z * 4));
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
                    
                    if (prevSection != null)
                    {
                        // We aren't on the first section, so we need to interpolate between sections, and assign blocks from the previous section up until this one
                        for (int y0 = 4 - 1; y0 >= 0; y0--)
                        {
                            int yPos = y * 4 + y0;
                            float heightFadeValue = 1;
                            for (int x0 = x * 4; x0 < (x + 1) * 4; x0++)
                            {
                                for (int z0 = z * 4; z0 < (z + 1) * 4; z0++)
                                {
                                    // set the current position
                                    pos.setPos(chunkX + x0, yPos, chunkZ + z0);
                                    BlockState replacementState = Blocks.PINK_WOOL.getDefaultState();
                                   
                                    if(chunkIn.getBiome(pos) instanceof IGBiome) {
                                    	IGBiome foundBiome = (IGBiome) chunkIn.getBiome(pos);
                                    	if(biomeData.getLbiome().equals(foundBiome)) {
                                    		float finalNoise = NoiseUtil.lerp(section[x0 + 16 * z0], prevSection[x0 + 16 * z0], 0.25f * y0);
                                    		float shrinkValue = 0.2f;
	                                    	
                                    		//change height fade thershold based on biome so ores spawn in 'tall' biomes
	                                    	if(foundBiome instanceof MountainsBiome) {
	                                    		shrinkValue = 0.1f;
	                                    		HEIGHT_FADE_THRESHOLD = 50;
	                                    	} else {
	                                    		shrinkValue = 0.2f;
	                                    		HEIGHT_FADE_THRESHOLD = 40;
	                                    	}

	                                    	//change height fade value so we get less ores the higher up we are
	                                    	heightFadeValue = yPos > HEIGHT_FADE_THRESHOLD ? 1 - (shrinkValue * (1 + ((yPos - HEIGHT_FADE_THRESHOLD) / HEIGHT_FADE_THRESHOLD))) : 1;
                                    	
	                                    	int totalLayers = biomeData.getLayerCount();
	                                    	int totHeight = 256;
	                                    	
	                                    	if ((yPos <= (totHeight * layer) / totalLayers) && 
	                                    	    (yPos >= (((totHeight * layer) / totalLayers) - ((totHeight * layer) / totalLayers) / layer))
	                                    	   ) {
	                                    		Material baseMaterial = ((IGMaterialBlock) biomeData.getLayerBlock((layer))).material;
	                                    		replacementState = IGBlockGrabber.grabOreBlock(MaterialUseType.ORE_BEARING, baseMaterial, oreMaterial).getDefaultState().with(IGProperties.NATURAL, true);
	                                    		
	                                    		//Run spawn in here to avoid creating ore outside of OUR layer.
	                                    		finalNoise *= heightFadeValue;

		                                         if (finalNoise > NOISE_THRESHOLD)
		                                         {
		                                             // Create cave if possible
		                                             BlockState originalState = chunkIn.getBlockState(pos);
		                                             if (!originalState.isAir(chunkIn, pos) && originalState != BEDROCK && !originalState.getMaterial().isLiquid() && (originalState.getBlock() instanceof IGMaterialBlock))
		                                             {
		                                                 chunkIn.setBlockState(pos, replacementState, false);
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
