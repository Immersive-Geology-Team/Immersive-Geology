package com.igteam.immersivegeology.common.world.layer.layers;

import com.igteam.immersivegeology.common.world.layer.IGLayerUtil;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IAreaTransformer0;

public class IslandLayer implements IAreaTransformer0
{
	private final int islandFrequency;

	public IslandLayer(int islandFrequency)
	{
		this.islandFrequency = islandFrequency;
	}

	@Override
	public int apply(INoiseRandom random, int x, int z)
	{
		if(x==0&&z==0)
		{
			return IGLayerUtil.PLAINS;
		}
		else
		{
			int TEMPERATE = IGLayerUtil.PLAINS;
			int HOT = IGLayerUtil.DESERT;
			int COLD = IGLayerUtil.LOWLANDS;
			int FROZEN = IGLayerUtil.ARCTIC_DESERT;
			int WARM = IGLayerUtil.LOW_CANYONS;
			
			int TEMPERATURE_BIOME = (z > 0) ? ((z) < 2) ?  TEMPERATE :  ((z) > 5) ? HOT:  WARM :  ((z) > -2) ?  TEMPERATE :  ((z) < -5) ? FROZEN : COLD;
			
			return random.random(islandFrequency)==0?TEMPERATURE_BIOME: IGLayerUtil.DEEP_OCEAN;
		}
	}
}