package com.igteam.immersivegeology.common.world.layer.layers;

import com.igteam.immersivegeology.common.world.layer.IGLayerUtil;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

import java.util.function.IntPredicate;
import java.util.function.Predicate;

public enum EdgeBiomeLayer implements ICastleTransformer
{
	INSTANCE;

	@Override
	public int apply(INoiseRandom context, int north, int west, int south, int east, int center)
	{
		Predicate<IntPredicate> matcher = p -> p.test(north)||p.test(west)||p.test(south)||p.test(east);
		if(center==IGLayerUtil.PLATEAU||center==IGLayerUtil.BADLANDS)
		{
			if(matcher.test(i -> i==IGLayerUtil.LOW_CANYONS||i==IGLayerUtil.LOWLANDS))
			{
				return IGLayerUtil.CANYONS;
			}
			else if(matcher.test(i -> i==IGLayerUtil.PLAINS||i==IGLayerUtil.HILLS))
			{
				return IGLayerUtil.ROLLING_HILLS;
			}
			else if(matcher.test(i -> i==IGLayerUtil.ROLLING_HILLS))
			{
				return IGLayerUtil.BADLANDS;
			}
		}
		else if(IGLayerUtil.isMountains(center))
		{
			if(matcher.test(IGLayerUtil::isLow))
			{
				return IGLayerUtil.ROLLING_HILLS;
			}
		}
//		else if(IGLayerUtil.isDeepOcean(center))
//		{
//			if(matcher.test(IGLayerUtil::isShallowOcean))
//			{
//				return IGLayerUtil.OCEAN_EDGE;
//			}
//		}
		// Inverses of above conditions
		else if(center==IGLayerUtil.LOWLANDS||center==IGLayerUtil.LOW_CANYONS)
		{
			if(matcher.test(i -> i==IGLayerUtil.PLATEAU||i==IGLayerUtil.BADLANDS))
			{
				return IGLayerUtil.CANYONS;
			}
			else if(matcher.test(IGLayerUtil::isMountains))
			{
				return IGLayerUtil.ROLLING_HILLS;
			}
		}
		else if(center==IGLayerUtil.PLAINS||center==IGLayerUtil.HILLS)
		{
			if(matcher.test(i -> i==IGLayerUtil.PLATEAU||i==IGLayerUtil.BADLANDS))
			{
				return IGLayerUtil.HILLS;
			}
			else if(matcher.test(IGLayerUtil::isMountains))
			{
				return IGLayerUtil.ROLLING_HILLS;
			}
		}
		else if(center==IGLayerUtil.ROLLING_HILLS)
		{
			if(matcher.test(i -> i==IGLayerUtil.PLATEAU||i==IGLayerUtil.BADLANDS))
			{
				return IGLayerUtil.BADLANDS;
			}
		}
		return center;
	}

}
