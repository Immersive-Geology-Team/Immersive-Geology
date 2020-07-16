package com.igteam.immersivegeology.common.world.layer.layers;

import com.igteam.immersivegeology.common.world.layer.IGLayerUtil;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum AddOasisLayer implements ICastleTransformer
{
	INSTANCE;

	@Override
	public int apply(INoiseRandom context, int north, int west, int south, int east, int center)
	{
		if(IGLayerUtil.isOasisCompatible(north)&&IGLayerUtil.isOasisCompatible(west)&&IGLayerUtil.isOasisCompatible(south)&&IGLayerUtil.isOasisCompatible(east)&&IGLayerUtil.isOasisCompatible(center))
		{
			if(context.random(15)==0)
			{
				return IGLayerUtil.OASIS;
			}
		}
		return center;
	}
} 