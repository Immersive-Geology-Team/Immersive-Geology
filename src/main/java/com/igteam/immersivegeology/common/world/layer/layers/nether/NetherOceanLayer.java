package com.igteam.immersivegeology.common.world.layer.layers.nether;

import com.igteam.immersivegeology.common.world.layer.IGLayerUtil;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum NetherOceanLayer implements ICastleTransformer
{
	INSTANCE;

	@Override
	public int apply(INoiseRandom context, int north, int west, int south, int east, int center)
	{
		if(context.random(10) > 5) {
			if (center == IGLayerUtil.MANTLE) {
				if (!IGLayerUtil.isNetherOcean(north) || !IGLayerUtil.isNetherOcean(west) || !IGLayerUtil.isNetherOcean(south) || !IGLayerUtil.isNetherOcean(east)) {
					return IGLayerUtil.NETHER_OCEAN;
				}
			}
		}
		return center;
	}
}
