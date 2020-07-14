package com.igteam.immersivegeology.common.world.layer.layers;

import com.igteam.immersivegeology.common.world.layer.IGLayerUtil;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IBishopTransformer;

public enum AddIslandLayer implements IBishopTransformer
{
	NORMAL(5),
	HEAVY(12);

	private final int oceanWeight;

	AddIslandLayer(int oceanWeight)
	{
		this.oceanWeight = oceanWeight;
	}

	public int apply(INoiseRandom context, int bottomLeft, int bottomRight, int topRight, int topLeft, int center)
	{
		if(!IGLayerUtil.isOcean(center)||(IGLayerUtil.isOcean(bottomLeft)&&IGLayerUtil.isOcean(bottomRight)&&IGLayerUtil.isOcean(topLeft)&&IGLayerUtil.isOcean(topRight)))
		{
			if(context.random(oceanWeight)==0)
			{
				if(IGLayerUtil.isOcean(topLeft))
				{
					return topLeft;
				}
				if(IGLayerUtil.isOcean(topRight))
				{
					return topRight;
				}
				if(IGLayerUtil.isOcean(bottomLeft))
				{
					return bottomLeft;
				}
				if(IGLayerUtil.isOcean(bottomRight))
				{
					return bottomRight;
				}
			}
			return center;
		}
		int counter = 1;
		int replacement = 1;
		if(!IGLayerUtil.isOcean(topLeft)&&context.random(counter++)==0)
		{
			replacement = topLeft;
		}

		if(!IGLayerUtil.isOcean(topRight)&&context.random(counter++)==0)
		{
			replacement = topRight;
		}

		if(!IGLayerUtil.isOcean(bottomLeft)&&context.random(counter++)==0)
		{
			replacement = bottomLeft;
		}

		if(!IGLayerUtil.isOcean(bottomRight)&&context.random(counter)==0)
		{
			replacement = bottomRight;
		}

		if(context.random(3)==0)
		{
			return replacement;
		}
		else
		{
			return center;
		}
	}
}