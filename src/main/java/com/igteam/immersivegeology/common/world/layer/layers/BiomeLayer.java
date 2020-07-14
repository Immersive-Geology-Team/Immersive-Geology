package com.igteam.immersivegeology.common.world.layer.layers;

import com.igteam.immersivegeology.common.world.layer.IGLayerUtil;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC0Transformer;

import static com.igteam.immersivegeology.common.world.layer.IGLayerUtil.*;

public enum BiomeLayer implements IC0Transformer
{
	INSTANCE;

	//never meets a high biome
	public static final int[] LOW_BIOMES = new int[]{ARCTIC_DESERT, PLAINS, HILLS, LOW_CANYONS, LOWLANDS, HILLS};

	//can meet either a low or a high biome
	public static final int[] MID_BIOMES = new int[]{ROLLING_HILLS, OLD_MOUNTAINS, BADLANDS, HILLS, PLAINS};

	//never meets a low biome
	public static final int[] HIGH_BIOMES = new int[]{DESERT, PLATEAU, BADLANDS, MOUNTAINS, FLOODED_MOUNTAINS, LUSH_MOUNTAINS};

	@Override
	public int apply(INoiseRandom context, int value)
	{
		if(value==DEEP_OCEAN)
		{
			if(context.random(8)==0)
			{
				return DEEP_OCEAN_VOLCANIC;
			}
			else
			{
				return DEEP_OCEAN;
			}
		}
		else if(value==PLAINS)
		{
			return LOW_BIOMES[context.random(LOW_BIOMES.length)];
		}
		else if(value==IGLayerUtil.HILLS)
		{
			return MID_BIOMES[context.random(MID_BIOMES.length)];
		}
		else if(value==IGLayerUtil.MOUNTAINS)
		{
			return HIGH_BIOMES[context.random(HIGH_BIOMES.length)];
		}
		return 0;
	}
}