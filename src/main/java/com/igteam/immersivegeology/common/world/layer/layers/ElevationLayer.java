package com.igteam.immersivegeology.common.world.layer.layers;

import com.igteam.immersivegeology.common.world.layer.IGLayerUtil;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC1Transformer;

import static com.igteam.immersivegeology.common.world.layer.layers.TemperateLayer.*;

import java.util.List;

import com.igteam.immersivegeology.api.util.IGMathHelper;

public enum ElevationLayer implements IC1Transformer
{
	INSTANCE;

	@Override
	public int apply(INoiseRandom context, int value)
	{
		if(!IGLayerUtil.isOcean(value))
		{
//			int rand = context.random(4);
//			List<Integer> returnID = IGMathHelper.intersection(TEMPERATE, low);
//			
			return value;
		}
		return value;
	}
}
